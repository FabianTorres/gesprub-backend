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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
     * Obtiene solo el nombre sugerido para el ZIP.
     */
    public String obtenerNombreZipComponente(Long idComponente) {
        Componente componente = componenteRepository.findById(idComponente)
                .orElseThrow(() -> new RuntimeException("Componente no encontrado"));
        return "Evidencias_" + componente.getNombre_componente().replaceAll("\\s+", "_") + ".zip";
    }
    
    /**
     * Genera el ZIP escribiendo directamente en el flujo de salida (OutputStream),
     * sin almacenar el archivo completo en memoria.
     */
    @Transactional(readOnly = true)
    public void generarZipStream(Long idComponente, Integer idEstadoModificacion, OutputStream outputStream) {
    	// 1. Usamos la nueva query con filtro opcional
        List<Caso> casos = casoRepository.findByComponenteAndEstadoModificacionOpcional(
                idComponente.intValue(), 
                idEstadoModificacion
        );
        if (casos.isEmpty()) return;
        
        List<Integer> idsCasos = casos.stream().map(c -> c.getId_caso().intValue()).collect(Collectors.toList());
        List<Evidencia> evidencias = evidenciaRepository.findByIdCasoIn(idsCasos);
        Map<Long, String> nombresCasos = casos.stream().collect(Collectors.toMap(Caso::getId_caso, Caso::getNombre_caso));
        Map<String, Integer> nombresUsados = new HashMap<>();

        // 2. Crear el ZipOutputStream envolviendo el outputStream que nos pasan (el del navegador)
        try (ZipOutputStream zos = new ZipOutputStream(outputStream)) {
            
            for (Evidencia evi : evidencias) {
                List<ArchivoEvidencia> archivos = archivoEvidenciaRepository.findByEvidenciaId(evi.getId_evidencia());
                
                for (ArchivoEvidencia archivo : archivos) {
                    try {
                        // Descargamos el stream de Azure (esto no carga el archivo en memoria, solo abre el grifo)
                        FileDownloadDTO descarga = azureStorageService.downloadFile(CONTAINER_NAME, archivo.getRuta_archivo(), archivo.getNombre_archivo());
                        
                        // --- Lógica de nombres (igual que antes) ---
                        String nombreCaso = nombresCasos.get((long)evi.getIdCaso()).replaceAll("[^a-zA-Z0-9.\\-]", "_");
                        String nombreArchivo = archivo.getNombre_archivo();
                        String rutaZip = nombreCaso + "/" + nombreArchivo;
                        
                        if (nombresUsados.containsKey(rutaZip)) {
                            int count = nombresUsados.get(rutaZip) + 1;
                            nombresUsados.put(rutaZip, count);
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
                        // -------------------------------------------

                        // Creamos la entrada en el ZIP
                        ZipEntry entry = new ZipEntry(rutaZip);
                        zos.putNextEntry(entry);
                        
                        // COPIADO EFICIENTE: Leemos de Azure y escribimos al ZIP en vuelo
                        try (InputStream is = descarga.getDataStream()) {
                            is.transferTo(zos);
                        }
                        zos.closeEntry();
                        
                    } catch (Exception e) {
                        System.err.println("Error al agregar archivo al ZIP: " + archivo.getRuta_archivo() + " - " + e.getMessage());
                    }
                }
            }
            zos.finish(); // Finalizamos el ZIP correctamente
        } catch (IOException e) {
            throw new RuntimeException("Error al generar el flujo ZIP", e);
        }
    }
    
    /**
     * Método principal que decide la estrategia (Caso A o Caso B).
     */
    @Transactional(readOnly = true)
    public void generarZipStreamInteligente(Long idComponente, Integer idEstadoModificacion, Long limiteBytes, OutputStream outputStream) {
        // 1. Obtener los archivos a descargar
        List<Caso> casos = casoRepository.findByComponenteAndEstadoModificacionOpcional(
                idComponente.intValue(), 
                idEstadoModificacion
        );
        if (casos.isEmpty()) return;

        List<Integer> idsCasos = casos.stream().map(c -> c.getId_caso().intValue()).collect(Collectors.toList());
        List<Evidencia> evidencias = evidenciaRepository.findByIdCasoIn(idsCasos);
        
        // Aplanamos la lista de archivos para trabajar más fácil
        List<ArchivoEvidencia> todosLosArchivos = evidencias.stream()
            .flatMap(evi -> archivoEvidenciaRepository.findByEvidenciaId(evi.getId_evidencia()).stream())
            .collect(Collectors.toList());

        if (todosLosArchivos.isEmpty()) return;

        // 2. Calcular peso total estimado
        // NOTA: Esto hace llamadas a Azure para obtener metadata. Si es muy lento, 
        // considera guardar el tamaño del archivo en la BD al momento de subirlo.
        long pesoTotal = 0;
        Map<Long, Long> mapaTamanos = new HashMap<>(); // Cache para no pedirlo dos veces
        
        for (ArchivoEvidencia archivo : todosLosArchivos) {
            try {
                // Obtenemos solo el tamaño sin descargar el contenido
                long tamano = azureStorageService.getFileSize(CONTAINER_NAME, archivo.getRuta_archivo());
                mapaTamanos.put(archivo.getId_archivo(), tamano);
                pesoTotal += tamano;
            } catch (Exception e) {
                // Si falla obtener tamaño, asumimos un valor seguro o 0
                mapaTamanos.put(archivo.getId_archivo(), 0L);
            }
        }

        // 3. Decidir estrategia
        try (ZipOutputStream zos = new ZipOutputStream(outputStream)) {
            // Mapa auxiliar para nombres de casos (usado en ambas estrategias)
            Map<Long, String> nombresCasos = casos.stream().collect(Collectors.toMap(Caso::getId_caso, Caso::getNombre_caso));

            if (pesoTotal <= limiteBytes) {
                // CASO A: Comportamiento original (Plano)
                generarZipPlano(todosLosArchivos, nombresCasos, zos);
            } else {
                // CASO B: Agrupación por Lotes (Nested ZIPs)
                generarZipPorLotes(todosLosArchivos, nombresCasos, mapaTamanos, limiteBytes, zos);
            }
            
            zos.finish();
        } catch (IOException e) {
            throw new RuntimeException("Error al generar el ZIP", e);
        }
    }
    
    
 // --- Lógica del CASO A (Tu código original refactorizado) ---
    private void generarZipPlano(List<ArchivoEvidencia> archivos, Map<Long, String> nombresCasos, ZipOutputStream zos) throws IOException {
        Map<String, Integer> nombresUsados = new HashMap<>();
        
        for (ArchivoEvidencia archivo : archivos) {
            agregarArchivoAlZip(archivo, nombresCasos, nombresUsados, zos);
        }
    }

    // --- Lógica del CASO B (Lotes) ---
    private void generarZipPorLotes(List<ArchivoEvidencia> archivos, Map<Long, String> nombresCasos, 
                                    Map<Long, Long> mapaTamanos, Long limiteBytes, ZipOutputStream masterZos) throws IOException {
        
        int numeroLote = 1;
        long tamanoActualLote = 0;
        
        // Iniciamos el primer lote
        ZipOutputStream loteZos = iniciarNuevoLote(masterZos, numeroLote);
        Map<String, Integer> nombresUsadosEnLote = new HashMap<>();

        for (ArchivoEvidencia archivo : archivos) {
            Long tamanoArchivo = mapaTamanos.getOrDefault(archivo.getId_archivo(), 0L);

            // Verificar si cabe en el lote actual
            // (Si el lote está vacío, metemos el archivo aunque sea gigante para no romper el loop)
            if (tamanoActualLote > 0 && (tamanoActualLote + tamanoArchivo) > limiteBytes) {
                // Cerramos lote actual
                loteZos.finish(); // Importante: Finish, no close, porque usamos ShieldedOutputStream
                masterZos.closeEntry(); // Cerramos la entrada del lote en el master

                // Abrimos siguiente lote
                numeroLote++;
                loteZos = iniciarNuevoLote(masterZos, numeroLote);
                tamanoActualLote = 0;
                nombresUsadosEnLote.clear();
            }

            // Agregamos al lote actual
            agregarArchivoAlZip(archivo, nombresCasos, nombresUsadosEnLote, loteZos);
            tamanoActualLote += tamanoArchivo;
        }

        // Cerrar el último lote pendiente
        loteZos.finish();
        masterZos.closeEntry();
    }

    // --- Helper para crear la entrada del sub-zip ---
    private ZipOutputStream iniciarNuevoLote(ZipOutputStream masterZos, int numero) throws IOException {
        String nombreLote = "Lote_" + numero + ".zip";
        ZipEntry loteEntry = new ZipEntry(nombreLote);
        masterZos.putNextEntry(loteEntry);
        
        // TRUCO VITAL: ShieldedOutputStream evita que al cerrar el zip interno se cierre el master
        return new ZipOutputStream(new ShieldedOutputStream(masterZos));
    }

    // --- Helper común para meter 1 archivo (Reutiliza tu lógica de nombres) ---
    private void agregarArchivoAlZip(ArchivoEvidencia archivo, Map<Long, String> nombresCasos, 
                                     Map<String, Integer> nombresUsados, ZipOutputStream zos) {
        try {
            FileDownloadDTO descarga = azureStorageService.downloadFile(CONTAINER_NAME, archivo.getRuta_archivo(), archivo.getNombre_archivo());
            
            String nombreCaso = nombresCasos.get((long) archivo.getEvidencia().getIdCaso()).replaceAll("[^a-zA-Z0-9.\\-]", "_");
            String nombreArchivo = archivo.getNombre_archivo();
            String rutaZip = nombreCaso + "/" + nombreArchivo;

            // Manejo de duplicados (Tu lógica exacta)
            if (nombresUsados.containsKey(rutaZip)) {
                int count = nombresUsados.get(rutaZip) + 1;
                nombresUsados.put(rutaZip, count);
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

            ZipEntry entry = new ZipEntry(rutaZip);
            zos.putNextEntry(entry);
            try (InputStream is = descarga.getDataStream()) {
                is.transferTo(zos);
            }
            zos.closeEntry();

        } catch (Exception e) {
            System.err.println("Error al agregar archivo " + archivo.getId_archivo() + ": " + e.getMessage());
        }
    }

    // --- CLASE INTERNA VITAL PARA ZIP ANIDADOS ---
    // Esta clase actúa como un escudo. Cuando el ZipOutputStream interno llama a close(),
    // este escudo lo intercepta y NO cierra el stream real (el del Zip Maestro).
    private static class ShieldedOutputStream extends FilterOutputStream {
        public ShieldedOutputStream(OutputStream out) {
            super(out);
        }

        @Override
        public void close() throws IOException {
            // No hacemos nada intencionalmente para no cerrar el stream padre
            // El stream padre se encargará de hacer flush/close cuando toque.
        }
    }
}