package cl.rac.gesprub.Servicio;

import cl.rac.gesprub.Entidad.EstadoEvidencia;
import cl.rac.gesprub.Repositorio.EstadoEvidenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EstadoEvidenciaService {

    @Autowired
    private EstadoEvidenciaRepository repository;

    public List<EstadoEvidencia> getAll() {
        return repository.findAll();
    }

    public EstadoEvidencia getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estado de evidencia no encontrado con id: " + id));
    }

    public EstadoEvidencia create(EstadoEvidencia estado) {
        return repository.save(estado);
    }

    public EstadoEvidencia update(Long id, EstadoEvidencia details) {
        EstadoEvidencia estadoExistente = getById(id);
        estadoExistente.setNombre(details.getNombre());
        estadoExistente.setActivo(details.getActivo());
        return repository.save(estadoExistente);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}