package cl.rac.gesprub.Servicio;

import cl.rac.gesprub.Entidad.ArchivoEvidencia;
import cl.rac.gesprub.Entidad.Caso;
import cl.rac.gesprub.Entidad.Componente;
import cl.rac.gesprub.Entidad.Evidencia;
import cl.rac.gesprub.Repositorio.ArchivoEvidenciaRepository;
import cl.rac.gesprub.Repositorio.CasoRepository;
import cl.rac.gesprub.Repositorio.ComponenteRepository;
import cl.rac.gesprub.Repositorio.EvidenciaRepository;
import cl.rac.gesprub.dto.ArchivoEvidenciaDTO;
import cl.rac.gesprub.dto.FileDownloadDTO;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional; 
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ArchivoEvidenciaService {

    @Autowired
    private ArchivoEvidenciaRepository archivoEvidenciaRepository;

    @Autowired
    private EvidenciaRepository evidenciaRepository;
    
    @Autowired
    private CasoRepository casoRepository;
    @Autowired
    private ComponenteRepository componenteRepository;
    
    @Autowired
    private AzureStorageService azureStorageService;
    
    // El nombre del contenedor en Azure donde se guardarán los archivos.
    private final String CONTAINER_NAME = "archivos";
    
    /**
     * Orquesta la subida del archivo a Azure y la creación del registro en la BD.
     */
    @Transactional
    public ArchivoEvidenciaDTO subirYGuardarArchivo(Long idEvidencia, MultipartFile file) throws IOException {
        // 1. Buscar Evidencia y sus padres para construir la ruta
        Evidencia evidencia = evidenciaRepository.findById(idEvidencia)
                .orElseThrow(() -> new RuntimeException("Evidencia no encontrada: " + idEvidencia));
        
        Caso caso = casoRepository.findById((long) evidencia.getIdCaso())
                .orElseThrow(() -> new RuntimeException("Caso no encontrado"));
                
        Componente componente = componenteRepository.findById((long) caso.getIdComponente())
                .orElseThrow(() -> new RuntimeException("Componente no encontrado"));
        
        Long proyectoId = componente.getProyecto().getId_proyecto();

        // 2. Generar ruta: {proyectoId}/{componenteId}/{casoId}/{uuid}.{ext}
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String uuid = UUID.randomUUID().toString();
        // Construimos el path virtual
        String blobPath = String.format("%d/%d/%d/%s%s", 
                proyectoId, 
                componente.getId_componente(), 
                caso.getId_caso(), 
                uuid, 
                extension);

        // 3. Subir a Azure usando la ruta generada
        azureStorageService.uploadFile(CONTAINER_NAME, blobPath, file);

        // 4. Guardar en BD
        ArchivoEvidencia nuevoArchivo = new ArchivoEvidencia();
        nuevoArchivo.setNombre_archivo(originalFilename); // Nombre real para el usuario
        nuevoArchivo.setRuta_archivo(blobPath);           // Ruta interna en Azure
        nuevoArchivo.setEvidencia(evidencia);

        ArchivoEvidencia archivoGuardado = archivoEvidenciaRepository.save(nuevoArchivo);
        return new ArchivoEvidenciaDTO(archivoGuardado);
    }
    
    /**
     * Obtiene el nombre de un archivo de la BD y genera una URL de descarga segura.
     */
    public String generarUrlDescarga(Long idArchivo) {
        ArchivoEvidencia archivo = archivoEvidenciaRepository.findById(idArchivo)
                .orElseThrow(() -> new RuntimeException("Archivo no encontrado: " + idArchivo));
        return azureStorageService.generateSasUrl(CONTAINER_NAME, archivo.getRuta_archivo());
    }


    public ArchivoEvidencia create(Long idEvidencia, ArchivoEvidencia archivoEvidencia) {
        Evidencia evidencia = evidenciaRepository.findById(idEvidencia)
                .orElseThrow(() -> new RuntimeException("Evidencia no encontrada con id: " + idEvidencia));

        archivoEvidencia.setEvidencia(evidencia);
        return archivoEvidenciaRepository.save(archivoEvidencia);
    }

    public List<ArchivoEvidenciaDTO> getArchivosPorEvidencia(Long idEvidencia) {
        List<ArchivoEvidencia> archivos = archivoEvidenciaRepository.findByEvidenciaId(idEvidencia);
        return archivos.stream().map(ArchivoEvidenciaDTO::new).collect(Collectors.toList());
    }
    
    /**
     * Prepara el flujo de un archivo para ser retransmitido (streaming) al cliente.
     */
    public FileDownloadDTO streamFile(Long idArchivo) {
        ArchivoEvidencia archivo = archivoEvidenciaRepository.findById(idArchivo)
                .orElseThrow(() -> new RuntimeException("Archivo no encontrado: " + idArchivo));
        
        // Usamos ruta_archivo en lugar de url_archivo
        return azureStorageService.downloadFile(CONTAINER_NAME, archivo.getRuta_archivo(), archivo.getNombre_archivo());
    }
    
    /**
     * Descarga masiva ZIP por Componente
     */
    @Transactional(readOnly = true) // Importante para rendimiento y lazy loading
    public FileDownloadDTO descargarZipComponente(Long idComponente) {
        // 1. Obtener nombre del componente para el archivo ZIP
        Componente componente = componenteRepository.findById(idComponente)
                .orElseThrow(() -> new RuntimeException("Componente no encontrado"));
        String nombreZip = "Evidencias_" + componente.getNombre_componente().replaceAll("\\s+", "_") + ".zip";

        // 2. Buscar todos los casos del componente
        List<Caso> casos = casoRepository.findByIdComponente(idComponente.intValue());
        if (casos.isEmpty()) {
            throw new RuntimeException("El componente no tiene casos.");
        }
        List<Integer> idsCasos = casos.stream().map(c -> c.getId_caso().intValue()).collect(Collectors.toList());

        // 3. Buscar evidencias y sus archivos
        List<Evidencia> evidencias = evidenciaRepository.findByIdCasoIn(idsCasos);
        
        // Mapa para controlar nombres duplicados en el ZIP: ruta -> contador
        Map<String, Integer> nombresUsados = new HashMap<>();
        
        // Usamos un stream en memoria para el ZIP
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            
            // Creamos un mapa rápido de casos para obtener nombres
            Map<Long, String> nombresCasos = casos.stream()
                    .collect(Collectors.toMap(Caso::getId_caso, Caso::getNombre_caso));

            for (Evidencia evi : evidencias) {
                List<ArchivoEvidencia> archivos = archivoEvidenciaRepository.findByEvidenciaId(evi.getId_evidencia());
                
                for (ArchivoEvidencia archivo : archivos) {
                    try {
                        // Descargar contenido desde Azure
                        FileDownloadDTO descarga = azureStorageService.downloadFile(CONTAINER_NAME, archivo.getRuta_archivo(), archivo.getNombre_archivo());
                        
                        // Construir ruta legible en el ZIP: {NombreCaso}/{NombreArchivo}
                        String nombreCaso = nombresCasos.get((long)evi.getIdCaso()).replaceAll("[^a-zA-Z0-9.\\-]", "_");
                        String nombreArchivo = archivo.getNombre_archivo();
                        
                        // Manejo de duplicados
                        String rutaZip = nombreCaso + "/" + nombreArchivo;
                        if (nombresUsados.containsKey(rutaZip)) {
                            int count = nombresUsados.get(rutaZip) + 1;
                            nombresUsados.put(rutaZip, count);
                            // Insertar contador antes de la extensión: foto.png -> foto(1).png
                            int dotIndex = nombreArchivo.lastIndexOf(".");
                            if (dotIndex > 0) {
                                nombreArchivo = nombreArchivo.substring(0, dotIndex) + "(" + count + ")" + nombreArchivo.substring(dotIndex);
                            } else {
                                nombreArchivo = nombreArchivo + "(" + count + ")";
                            }
                            rutaZip = nombreCaso + "/" + nombreArchivo;
                        } else {
                            nombresUsados.put(rutaZip, 0);
                        }

                        // Añadir al ZIP
                        ZipEntry entry = new ZipEntry(rutaZip);
                        zos.putNextEntry(entry);
                        
                        // Copiar stream
                        try (InputStream is = descarga.getDataStream()) {
                            is.transferTo(zos);
                        }
                        zos.closeEntry();
                        
                    } catch (Exception e) {
                        // Si falla un archivo, lo logueamos pero intentamos seguir con los demás
                        System.err.println("Error al agregar archivo al ZIP: " + archivo.getRuta_archivo() + " - " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al generar el archivo ZIP", e);
        }

        // Retornar el ZIP como un stream
        InputStream zipStream = new ByteArrayInputStream(baos.toByteArray());
        return new FileDownloadDTO(nombreZip, (long) baos.size(), zipStream);
    }
}