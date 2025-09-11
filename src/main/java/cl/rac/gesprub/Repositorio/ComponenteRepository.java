package cl.rac.gesprub.Repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

import cl.rac.gesprub.Entidad.Componente;

@Repository
public interface ComponenteRepository extends JpaRepository<Componente, Long>{
	
	@Query("SELECT c FROM Componente c WHERE c.proyecto.id_proyecto = :proyectoId")
    List<Componente> findByProyectoId(@Param("proyectoId") Long proyectoId);
	
	 /**
     * Encuentra todos los IDs de componentes que pertenecen a un proyecto.
     */
    @Query("SELECT c.id_componente FROM Componente c WHERE c.proyecto.id_proyecto = :proyectoId")
    List<Integer> findComponenteIdsByProyectoId(@Param("proyectoId") Long proyectoId);

}
