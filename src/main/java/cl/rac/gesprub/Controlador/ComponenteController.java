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

import cl.rac.gesprub.Entidad.Componente;
import cl.rac.gesprub.Servicio.ComponenteService;
import cl.rac.gesprub.dto.ComponenteDTO;


@RestController
@RequestMapping("/api/componente")
public class ComponenteController {
	
	@Autowired
    private ComponenteService componenteService;
	
	@PostMapping
    public Componente createComponente(@RequestBody Componente componente) {
        return componenteService.createComponente(componente);
    }
	
	@GetMapping
    public List<ComponenteDTO> getAllComponentes(@RequestParam(required = false) Long proyectoId) {
        return componenteService.getComponentes(proyectoId);
    }
	
	@GetMapping("/{id}")
    public Componente getComponenteById(@PathVariable Long id) {
        return componenteService.getComponenteById(id);
    }
	
	@PutMapping("/{id}")
    public Componente updateComponente(@PathVariable Long id, @RequestBody Componente componente) {
        return componenteService.updateComponente(id, componente);
    }
	
	@DeleteMapping("/{id}")
    public void deleteCaso(@PathVariable Long id) {
		componenteService.deleteComponente(id);
    }
}
