package cl.rac.gesprub.Servicio;

import cl.rac.gesprub.Entidad.Proyecto;
import cl.rac.gesprub.Repositorio.ProyectoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProyectoService {

    @Autowired
    private ProyectoRepository proyectoRepository;

    public List<Proyecto> getAllProyectos() {
        return proyectoRepository.findAll();
    }
    
    public List<Proyecto> getProyectosActivos() {
        return proyectoRepository.findByActivo(1); // Asumiendo que 1 = activo
    }

    public Proyecto createProyecto(Proyecto proyecto) {
        return proyectoRepository.save(proyecto);
    }
    
    public Proyecto updateProyecto(Long id_proyecto, Proyecto proyecto) {
    	proyecto.setId_proyecto(id_proyecto);
        return proyectoRepository.save(proyecto);
    }
    
}