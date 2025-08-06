package cl.rac.gesprub.Repositorio;

import cl.rac.gesprub.Entidad.EstadoModificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoModificacionRepository extends JpaRepository<EstadoModificacion, Integer> {
    // Spring Data JPA nos dará los métodos básicos como findAll(), findById(), save(), etc.
    // por arte de magia, no necesitamos escribir nada más aquí por ahora.
}