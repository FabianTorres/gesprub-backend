package cl.rac.gesprub.Controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.rac.gesprub.Entidad.Evidencia;
import cl.rac.gesprub.Servicio.EvidenciaService;
import cl.rac.gesprub.dto.EvidenciaDTO;
import cl.rac.gesprub.dto.MoverEvidenciaDTO;


@RestController
@RequestMapping("/api/evidencia")
public class EvidenciaController {
	
	@Autowired
    private EvidenciaService evidenciaService;
	
	@PostMapping
    public Evidencia createEvidencia(@RequestBody Evidencia evidencia) {
        return evidenciaService.createEvidencia(evidencia);
    }
	
	@GetMapping
    public List<Evidencia> getAllEvidencias() {
        return evidenciaService.getAllEvidencias();
    }
	
	@GetMapping("/{id}")
    public Evidencia getEvidenciaById(@PathVariable Long id) {
        return evidenciaService.getEvidenciaById(id);
    }
	
	@PutMapping("/{id}")
    public Evidencia updateEvidencia(@PathVariable Long id, @RequestBody Evidencia evidencia) {
        return evidenciaService.updateEvidencia(id, evidencia);
    }
	
	@DeleteMapping("/{id}")
    public void deleteEvidencia(@PathVariable Long id) {
		evidenciaService.deleteEvidencia(id);
    }
	
	@PatchMapping("/{idEvidencia}/mover")
    public EvidenciaDTO moverEvidencia( 
            @PathVariable Long idEvidencia, 
            @RequestBody MoverEvidenciaDTO body) {
        
        Evidencia evidenciaActualizada = evidenciaService.moverEvidencia(idEvidencia, body.getNuevoIdCaso());
        
        return new EvidenciaDTO(evidenciaActualizada);
    }

}
