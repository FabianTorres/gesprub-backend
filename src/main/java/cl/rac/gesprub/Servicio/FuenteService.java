package cl.rac.gesprub.Servicio;

import cl.rac.gesprub.Entidad.Fuente;
import cl.rac.gesprub.Repositorio.FuenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FuenteService {

    @Autowired
    private FuenteRepository fuenteRepository;

    /**
     * Obtiene todas las fuentes del catálogo.
     */
    public List<Fuente> getAllFuentes() {
        return fuenteRepository.findAll();
    }

    /**
     * Crea una nueva fuente en el catálogo.
     * Valida que no exista una con el mismo nombre.
     */
    public Fuente createFuente(Fuente fuente) {
        // Opcional: Podrías añadir una validación para no permitir nombres duplicados
        // aunque la base de datos ya lo previene con la constraint UNIQUE.
        return fuenteRepository.save(fuente);
    }
    
    /**
     * Actualiza una fuente existente.
     */
    public Fuente updateFuente(Long id, Fuente fuenteDetails) {
        Fuente fuenteExistente = fuenteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fuente no encontrada con id: " + id));
        
        fuenteExistente.setNombre_fuente(fuenteDetails.getNombre_fuente());
        fuenteExistente.setActivo(fuenteDetails.getActivo());
        
        return fuenteRepository.save(fuenteExistente);
    }

    /**
     * Elimina una fuente del catálogo.
     * Gracias a la configuración de la base de datos (ON DELETE CASCADE),
     * al eliminar una fuente, se borrarán automáticamente todas sus asociaciones
     * con los casos en la tabla intermedia.
     */
    public void deleteFuente(Long id) {
        if (!fuenteRepository.existsById(id)) {
            throw new RuntimeException("Fuente no encontrada con id: " + id);
        }
        fuenteRepository.deleteById(id);
    }
}