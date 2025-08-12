package cl.rac.gesprub.Servicio;

import cl.rac.gesprub.Entidad.Criticidad;
import cl.rac.gesprub.Repositorio.CriticidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CriticidadService {

    @Autowired
    private CriticidadRepository criticidadRepository;

    public List<Criticidad> getAll() {
        return criticidadRepository.findAll();
    }

    public Criticidad getById(Long id) {
        return criticidadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Criticidad no encontrada con id: " + id));
    }

    public Criticidad create(Criticidad criticidad) {
        return criticidadRepository.save(criticidad);
    }

    public Criticidad update(Long id, Criticidad details) {
        Criticidad criticidadExistente = getById(id);
        criticidadExistente.setNombre_criticidad(details.getNombre_criticidad());
        criticidadExistente.setActivo(details.getActivo());
        return criticidadRepository.save(criticidadExistente);
    }

    public void delete(Long id) {
        criticidadRepository.deleteById(id);
    }
}