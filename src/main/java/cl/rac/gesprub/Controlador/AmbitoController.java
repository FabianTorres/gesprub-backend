package cl.rac.gesprub.Controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.rac.gesprub.Entidad.Ambito;
import cl.rac.gesprub.Servicio.AmbitoService;


@RestController
@RequestMapping("/api/ambito")
public class AmbitoController {
	
	@Autowired
    private AmbitoService ambitoService;
	
	@PostMapping
    public Ambito createAmbito(@RequestBody Ambito ambito) {
        return ambitoService.createAmbito(ambito);
    }
	
	@GetMapping
    public List<Ambito> getAllAmbitos() {
        return ambitoService.getAllAmbitos();
    }
	
	@GetMapping("/{id}")
    public Ambito getAmbitoById(@PathVariable Long id) {
        return ambitoService.getAmbitoById(id);
    }
	
	@PutMapping("/{id}")
    public Ambito updateAmbito(@PathVariable Long id, @RequestBody Ambito ambito) {
        return ambitoService.updateAmbito(id, ambito);
    }

}
