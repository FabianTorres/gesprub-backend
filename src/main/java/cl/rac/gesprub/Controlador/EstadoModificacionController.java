package cl.rac.gesprub.Controlador;

import cl.rac.gesprub.Entidad.EstadoModificacion;
import cl.rac.gesprub.Servicio.EstadoModificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/estado-modificacion")
public class EstadoModificacionController {

    @Autowired
    private EstadoModificacionService service;

    @GetMapping
    public List<EstadoModificacion> getAllEstados() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public EstadoModificacion getEstadoById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @PostMapping
    public EstadoModificacion createEstado(@RequestBody EstadoModificacion estado) {
        return service.create(estado);
    }

    @PutMapping("/{id}")
    public EstadoModificacion updateEstado(@PathVariable Integer id, @RequestBody EstadoModificacion estado) {
        return service.update(id, estado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEstado(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build(); // Devuelve un estado 204 No Content
    }
}