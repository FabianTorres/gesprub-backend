package cl.rac.gesprub.Controlador;

import cl.rac.gesprub.Entidad.EstadoEvidencia;
import cl.rac.gesprub.Servicio.EstadoEvidenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/estado-evidencia")
public class EstadoEvidenciaController {

    @Autowired
    private EstadoEvidenciaService service;

    @GetMapping
    public List<EstadoEvidencia> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public EstadoEvidencia getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public EstadoEvidencia create(@RequestBody EstadoEvidencia estado) {
        return service.create(estado);
    }

    @PutMapping("/{id}")
    public EstadoEvidencia update(@PathVariable Long id, @RequestBody EstadoEvidencia estado) {
        return service.update(id, estado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}