package cl.rac.gesprub.Servicio;

import cl.rac.gesprub.Entidad.ArchivoEvidencia;
import cl.rac.gesprub.Entidad.Evidencia;
import cl.rac.gesprub.Repositorio.ArchivoEvidenciaRepository;
import cl.rac.gesprub.Repositorio.EvidenciaRepository;
import cl.rac.gesprub.dto.ArchivoEvidenciaDTO;
import cl.rac.gesprub.dto.FileDownloadDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional; 
import org.springframework.web.multipart.MultipartFile; 
import java.io.IOException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArchivoEvidenciaService {

    @Autowired
    private ArchivoEvidenciaRepository archivoEvidenciaRepository;

    @Autowired
    private EvidenciaRepository evidenciaRepository;
    
    @Autowired
    private AzureStorageService azureStorageService;
    
    // El nombre del contenedor en Azure donde se guardarán los archivos.
    private final String CONTAINER_NAME = "archivos";
    
    /**
     * Orquesta la subida del archivo a Azure y la creación del registro en la BD.
     */
    @Transactional
    public ArchivoEvidenciaDTO subirYGuardarArchivo(Long idEvidencia, MultipartFile file) throws IOException {
        // 1. Subimos el archivo a Azure Storage y obtenemos la URL.
    	String blobName = azureStorageService.uploadFile(CONTAINER_NAME, file);

        // 2. Buscamos la entidad Evidencia a la que se asociará el archivo.
        Evidencia evidencia = evidenciaRepository.findById(idEvidencia)
                .orElseThrow(() -> new RuntimeException("Evidencia no encontrada con id: " + idEvidencia));

        // 3. Creamos la nueva entidad ArchivoEvidencia con los datos.
        ArchivoEvidencia nuevoArchivo = new ArchivoEvidencia();
        nuevoArchivo.setNombre_archivo(file.getOriginalFilename());
        nuevoArchivo.setUrl_archivo(blobName);
        nuevoArchivo.setEvidencia(evidencia);

        // 4. Guardamos el registro en nuestra base de datos.
        ArchivoEvidencia archivoGuardado = archivoEvidenciaRepository.save(nuevoArchivo);

        // 5. Devolvemos un DTO con la información.
        return new ArchivoEvidenciaDTO(archivoGuardado);
    }
    
    /**
     * Obtiene el nombre de un archivo de la BD y genera una URL de descarga segura.
     */
    public String generarUrlDescarga(Long idArchivo) {
        // 1. Buscamos el registro del archivo en nuestra BD.
        ArchivoEvidencia archivo = archivoEvidenciaRepository.findById(idArchivo)
                .orElseThrow(() -> new RuntimeException("Archivo no encontrado con id: " + idArchivo));
        
        // 2. Obtenemos el nombre del blob (que guardamos en el campo url_archivo).
        String blobName = archivo.getUrl_archivo();

        // 3. Llamamos a nuestro servicio de Azure para que genere el enlace seguro.
        return azureStorageService.generateSasUrl(CONTAINER_NAME, blobName);
    }


    public ArchivoEvidencia create(Long idEvidencia, ArchivoEvidencia archivoEvidencia) {
        Evidencia evidencia = evidenciaRepository.findById(idEvidencia)
                .orElseThrow(() -> new RuntimeException("Evidencia no encontrada con id: " + idEvidencia));

        archivoEvidencia.setEvidencia(evidencia);
        return archivoEvidenciaRepository.save(archivoEvidencia);
    }

    public List<ArchivoEvidenciaDTO> getArchivosPorEvidencia(Long idEvidencia) {
        List<ArchivoEvidencia> archivos = archivoEvidenciaRepository.findByEvidenciaId(idEvidencia);
        return archivos.stream()
                .map(ArchivoEvidenciaDTO::new)
                .collect(Collectors.toList());
    }
    
    /**
     * NUEVO MÉTODO
     * Prepara el flujo de un archivo para ser retransmitido (streaming) al cliente.
     */
    public FileDownloadDTO streamFile(Long idArchivo) {
        // 1. Buscamos el registro del archivo en nuestra BD.
        ArchivoEvidencia archivo = archivoEvidenciaRepository.findById(idArchivo)
                .orElseThrow(() -> new RuntimeException("Archivo no encontrado con id: " + idArchivo));
        
        // 2. Obtenemos el nombre del blob y el nombre original
        String blobName = archivo.getUrl_archivo();
        String nombreOriginal = archivo.getNombre_archivo();

        // 3. Llamamos al servicio de Azure para obtener el flujo de descarga
        return azureStorageService.downloadFile(CONTAINER_NAME, blobName, nombreOriginal);
    }
}