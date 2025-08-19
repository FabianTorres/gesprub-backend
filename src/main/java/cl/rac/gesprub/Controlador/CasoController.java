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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.rac.gesprub.Entidad.Caso;
import cl.rac.gesprub.Entidad.Evidencia;
import cl.rac.gesprub.Servicio.CasoService;
import cl.rac.gesprub.dto.CasoConEvidenciaDTO;
import cl.rac.gesprub.dto.CasoDTO;
import cl.rac.gesprub.dto.CasoVersionUpdateDTO;
import cl.rac.gesprub.dto.HistorialDTO;
import cl.rac.gesprub.Entidad.Fuente;
import java.util.Set;
import cl.rac.gesprub.dto.FuenteDTO;

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
	
	@PatchMapping("/{id}/version")
    public Caso updateCasoVersion(@PathVariable Long id, @RequestBody CasoVersionUpdateDTO versionDto) {
        return casoService.updateCasoVersion(id, versionDto);
    }
	
	//Fuentes
	@PostMapping("/{idCaso}/fuentes")
    public Caso asignarFuenteACaso(@PathVariable Long idCaso, @RequestBody Fuente fuente) {
        Long idFuente = fuente.getId_fuente();
        return casoService.asignarFuenteACaso(idCaso, idFuente);
    }
	
	@DeleteMapping("/{idCaso}/fuentes/{idFuente}")
    public Caso quitarFuenteDeCaso(@PathVariable Long idCaso, @PathVariable Long idFuente) {
        return casoService.quitarFuenteDeCaso(idCaso, idFuente);
    }
	
	@GetMapping("/{idCaso}/fuentes")
    public Set<FuenteDTO> getFuentesPorCaso(@PathVariable Long idCaso) {
        return casoService.getFuentesPorCaso(idCaso);
    }
	
	@PutMapping("/{idCaso}/fuentes")
    public CasoDTO sincronizarFuentes(@PathVariable Long idCaso, @RequestBody List<Long> idsFuente) {
        Caso casoActualizado = casoService.sincronizarFuentesParaCaso(idCaso, idsFuente);
        return new CasoDTO(casoActualizado); // Devolvemos un DTO para ser consistentes
    }


}
