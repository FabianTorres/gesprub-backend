package cl.rac.gesprub.Controlador;

import cl.rac.gesprub.dto.DownloadUrlDTO;
import cl.rac.gesprub.dto.FileDownloadDTO;
import cl.rac.gesprub.Servicio.ArchivoEvidenciaService;
import cl.rac.gesprub.dto.ArchivoEvidenciaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; 
import java.io.IOException;
import java.util.List;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api")
public class ArchivoEvidenciaController {

    @Autowired
    private ArchivoEvidenciaService archivoEvidenciaService;

    @PostMapping("/evidencia/{idEvidencia}/archivos")
    public ArchivoEvidenciaDTO createArchivoParaEvidencia(
            @PathVariable Long idEvidencia,
            @RequestParam("file") MultipartFile file) throws IOException {
        
            return archivoEvidenciaService.subirYGuardarArchivo(idEvidencia, file);
    }

    @GetMapping("/evidencia/{idEvidencia}/archivos")
    public List<ArchivoEvidenciaDTO> getArchivosPorEvidencia(@PathVariable Long idEvidencia) {
        return archivoEvidenciaService.getArchivosPorEvidencia(idEvidencia);
    }
    
    /**
     * Este endpoint se mantiene, pero ahora el frontend solo lo usará para redes
     * que no tengan problemas de firewall
     */
    @GetMapping("/archivos/{idArchivo}/descargar")
    public DownloadUrlDTO getDownloadUrl(@PathVariable Long idArchivo) {
        String url = archivoEvidenciaService.generarUrlDescarga(idArchivo);
        return new DownloadUrlDTO(url);
    }
    
    /**
     * Descarga el archivo actuando como un proxy. El archivo se sirve
     * desde el dominio gesprub.cl, evitando bloqueos de firewall.
     */
    @GetMapping("/archivos/stream/{idArchivo}")
    public ResponseEntity<InputStreamResource> streamFile(@PathVariable Long idArchivo) {
        // 1. Obtenemos los datos del archivo desde el servicio
        FileDownloadDTO fileData = archivoEvidenciaService.streamFile(idArchivo);

        // 2. Creamos las cabeceras HTTP para la descarga
        HttpHeaders headers = new HttpHeaders();
        // Esta cabecera le dice al navegador que debe descargar el archivo con su nombre original
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileData.getNombreOriginal() + "\"");
        // Indicamos que es un flujo de datos binario
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        // Indicamos el tamaño del archivo para que el navegador muestre el progreso
        headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileData.getContentLength()));

        // 3. Devolvemos la respuesta
        return new ResponseEntity<>(
            new InputStreamResource(fileData.getDataStream()), // El cuerpo de la respuesta es el flujo del archivo
            headers, // Las cabeceras que definimos
            HttpStatus.OK
        );
    }
    
    /**
     * Descarga Masiva (ZIP)
     */
    @GetMapping("/componente/{idComponente}/descargar-zip")
    public ResponseEntity<InputStreamResource> descargarZipComponente(@PathVariable Long idComponente) {
        FileDownloadDTO fileData = archivoEvidenciaService.descargarZipComponente(idComponente);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileData.getNombreOriginal() + "\"");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/zip");
        headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileData.getContentLength()));

        return new ResponseEntity<>(
            new InputStreamResource(fileData.getDataStream()),
            headers,
            HttpStatus.OK
        );
    }
}