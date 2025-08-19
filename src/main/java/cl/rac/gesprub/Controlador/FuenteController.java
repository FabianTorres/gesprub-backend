package cl.rac.gesprub.Controlador;

import cl.rac.gesprub.Entidad.Fuente;
import cl.rac.gesprub.Servicio.FuenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/fuentes")
public class FuenteController {

    @Autowired
    private FuenteService fuenteService;

    // --- Endpoint para el ListBox del Frontend ---
    @GetMapping
    public List<Fuente> getAllFuentes() {
        return fuenteService.getAllFuentes();
    }

    @PostMapping
    public Fuente createFuente(@RequestBody Fuente fuente) {
        return fuenteService.createFuente(fuente);
    }

    @PutMapping("/{id}")
    public Fuente updateFuente(@PathVariable Long id, @RequestBody Fuente fuenteDetails) {
        return fuenteService.updateFuente(id, fuenteDetails);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFuente(@PathVariable Long id) {
        fuenteService.deleteFuente(id);
        return ResponseEntity.noContent().build(); // Devuelve un estado 204 No Content
    }
}