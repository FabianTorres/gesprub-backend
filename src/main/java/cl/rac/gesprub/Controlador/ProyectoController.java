package cl.rac.gesprub.Controlador;

import cl.rac.gesprub.Entidad.Proyecto;
import cl.rac.gesprub.Servicio.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/proyecto")
public class ProyectoController {

    @Autowired
    private ProyectoService proyectoService;

    @GetMapping
    public List<Proyecto> getProyectos() {
        return proyectoService.getProyectosActivos();
    }
    
    @PostMapping
    public Proyecto createProyecto(@RequestBody Proyecto proyecto) {
        return proyectoService.createProyecto(proyecto);
    }
}