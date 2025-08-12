package cl.rac.gesprub.Controlador;

import cl.rac.gesprub.Entidad.Criticidad;
import cl.rac.gesprub.Servicio.CriticidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/criticidad")
public class CriticidadController {

    @Autowired
    private CriticidadService service;

    @GetMapping
    public List<Criticidad> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Criticidad getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public Criticidad create(@RequestBody Criticidad criticidad) {
        return service.create(criticidad);
    }

    @PutMapping("/{id}")
    public Criticidad update(@PathVariable Long id, @RequestBody Criticidad criticidad) {
        return service.update(id, criticidad);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}