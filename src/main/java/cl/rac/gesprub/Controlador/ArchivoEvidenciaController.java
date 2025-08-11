package cl.rac.gesprub.Controlador;

import cl.rac.gesprub.Entidad.ArchivoEvidencia;
import cl.rac.gesprub.Servicio.ArchivoEvidenciaService;
import cl.rac.gesprub.dto.ArchivoEvidenciaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ArchivoEvidenciaController {

    @Autowired
    private ArchivoEvidenciaService archivoEvidenciaService;

    @PostMapping("/evidencia/{idEvidencia}/archivos")
    public ArchivoEvidencia createArchivoParaEvidencia(
            @PathVariable Long idEvidencia,
            @RequestBody ArchivoEvidencia archivoEvidencia) {
        
        return archivoEvidenciaService.create(idEvidencia, archivoEvidencia);
    }

    @GetMapping("/evidencia/{idEvidencia}/archivos")
    public List<ArchivoEvidenciaDTO> getArchivosPorEvidencia(@PathVariable Long idEvidencia) {
        return archivoEvidenciaService.getArchivosPorEvidencia(idEvidencia);
    }
}