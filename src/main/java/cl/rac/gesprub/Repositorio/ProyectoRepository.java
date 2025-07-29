package cl.rac.gesprub.Repositorio;

import cl.rac.gesprub.Entidad.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {

    
    List<Proyecto> findByActivo(int activo);
}