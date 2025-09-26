package cl.rac.gesprub.Controlador;

import cl.rac.gesprub.dto.DownloadUrlDTO;
import cl.rac.gesprub.Servicio.ArchivoEvidenciaService;
import cl.rac.gesprub.dto.ArchivoEvidenciaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; 
import java.io.IOException;
import java.util.List;

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
     * Genera y devuelve una URL de descarga segura y temporal para un archivo.
     */
    @GetMapping("/archivos/{idArchivo}/descargar")
    public DownloadUrlDTO getDownloadUrl(@PathVariable Long idArchivo) {
        String url = archivoEvidenciaService.generarUrlDescarga(idArchivo);
        return new DownloadUrlDTO(url);
    }
}