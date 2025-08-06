package cl.rac.gesprub.Servicio;

import cl.rac.gesprub.Entidad.EstadoModificacion;
import cl.rac.gesprub.Repositorio.EstadoModificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EstadoModificacionService {

    @Autowired
    private EstadoModificacionRepository repository;

    public List<EstadoModificacion> getAll() {
        return repository.findAll();
    }

    public EstadoModificacion getById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado con id: " + id));
    }

    public EstadoModificacion create(EstadoModificacion estado) {
        return repository.save(estado);
    }

    public EstadoModificacion update(Integer id, EstadoModificacion estadoDetails) {
        EstadoModificacion estadoExistente = getById(id);
        estadoExistente.setNombre(estadoDetails.getNombre());
        estadoExistente.setActivo(estadoDetails.getActivo());
        return repository.save(estadoExistente);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }
}