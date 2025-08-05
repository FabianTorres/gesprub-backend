package cl.rac.gesprub.Controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.rac.gesprub.Entidad.Caso;
import cl.rac.gesprub.Entidad.Evidencia;
import cl.rac.gesprub.Servicio.CasoService;
import cl.rac.gesprub.dto.CasoConEvidenciaDTO;
import cl.rac.gesprub.dto.HistorialDTO;

@RestController
@RequestMapping("/api/caso")
public class CasoController {
	
	@Autowired
    private CasoService casoService;
	
	@PostMapping
    public Caso createCaso(@RequestBody Caso caso) {
        return casoService.createCaso(caso);
    }
	
	@GetMapping
    public List<Caso> getAllCasos() {
        return casoService.getAllCasos();
    }
	
	@GetMapping("/{id}")
    public Caso getCasoById(@PathVariable Long id) {
        return casoService.getCasoById(id);
    }
	
	@PutMapping("/{id}")
    public Caso updateCaso(@PathVariable Long id, @RequestBody Caso caso) {
        return casoService.updateCaso(id, caso);
    }
	
	@DeleteMapping("/{id}")
    public void deleteCaso(@PathVariable Long id) {
        casoService.deleteCaso(id);
    }
	
	
	//Obtiene la ultima evidencia de cada caso
	@GetMapping("/evidencia")
	public List<CasoConEvidenciaDTO> getCasosConUltimaEvidencia() {
	    return casoService.getCasosConUltimaEvidencia();
	}
	
	//Obtiene todas las evidencias de un solo caso, por id
	@GetMapping("/{id}/evidencias")
	public List<Evidencia> getEvidenciasPorCaso(@PathVariable int id) {
	    return casoService.getEvidenciasPorCaso(id);
	}
	
	//Obtiene la ultima evidencia de cada caso por componente
	@GetMapping("/evidenciacomp")
	public List<CasoConEvidenciaDTO> getCasosConUltimaEvidenciaPorComponente(
	        @RequestParam int componenteId) {

	    return casoService.getCasosConUltimaEvidenciaPorComponente(componenteId);
	}
	
	/**
     * NUEVO ENDPOINT
     * Obtiene el historial completo de evidencias para un caso específico.
     * @param id El ID del caso, pasado en la URL.
     * @return Un objeto HistorialDTO con los datos del caso y su lista de evidencias.
     */
	@GetMapping("/{id}/historial")
	public HistorialDTO getHistorialPorCaso(@PathVariable int id) {
	    return casoService.getHistorialPorCaso(id);
	}
	
	@GetMapping("/formularios")
    public List<Integer> getNumerosDeFormulario() {
        return casoService.getNumerosDeFormularioUnicos();
    }


}
