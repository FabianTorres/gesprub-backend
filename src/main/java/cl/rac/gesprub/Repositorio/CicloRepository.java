package cl.rac.gesprub.Repositorio;

import cl.rac.gesprub.Entidad.Ciclo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CicloRepository extends JpaRepository<Ciclo, Integer> {
    
    List<Ciclo> findByActivo(Integer activo);
    
    // Buscar por proyecto y estado activo (para el filtro por defecto "activos")
    List<Ciclo> findByIdProyectoAndActivo(Long idProyecto, Integer activo);

    // Buscar todos los de un proyecto (para el filtro "todos")
    List<Ciclo> findByIdProyecto(Long idProyecto);
}