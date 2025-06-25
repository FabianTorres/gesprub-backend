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

import cl.rac.gesprub.Entidad.Autenticacion;
import cl.rac.gesprub.Servicio.AutenticacionService;


@RestController
@RequestMapping("/api/autenticacion")
public class AutenticacionController {
	
	@Autowired
    private AutenticacionService autenticacionService;
	
	@PostMapping
    public Autenticacion createAutenticacion(@RequestBody Autenticacion autenticacion) {
        return autenticacionService.createAutenticacion(autenticacion);
    }
	
	@GetMapping
    public List<Autenticacion> getAllAutenticaciones() {
        return autenticacionService.getAllAutenticaciones();
    }
	
	@GetMapping("/{id}")
    public Autenticacion getAutenticacionById(@PathVariable Long id) {
        return autenticacionService.getAutenticacionById(id);
    }
	
	@PutMapping("/{id}")
    public Autenticacion updateAutenticacion(@PathVariable Long id, @RequestBody Autenticacion autenticacion) {
        return autenticacionService.updateAutenticacion(id, autenticacion);
    }
	
	@DeleteMapping("/{id}")
    public void deleteAutenticacion(@PathVariable Long id) {
		autenticacionService.deleteAutenticacion(id);
    }


}
