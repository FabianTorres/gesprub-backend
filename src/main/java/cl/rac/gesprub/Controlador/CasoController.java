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
import org.springframework.web.bind.annotation.RestController;

import cl.rac.gesprub.Entidad.Caso;
import cl.rac.gesprub.Servicio.CasoService;

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

}
