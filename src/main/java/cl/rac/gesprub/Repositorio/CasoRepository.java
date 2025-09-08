package cl.rac.gesprub.Repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cl.rac.gesprub.Entidad.Caso;

@Repository
public interface CasoRepository extends JpaRepository<Caso, Long>{
	
	List<Caso> findByIdComponente(int idComponente);
	
	@Query("SELECT DISTINCT c.num_formulario FROM Caso c WHERE c.num_formulario IS NOT NULL ORDER BY c.num_formulario ASC")
    List<Integer> findDistinctNumFormulario();
	
	/**
     * Cuenta todos los casos que pertenecen a una lista de componentes.
     */
    @Query("SELECT count(c) FROM Caso c WHERE c.idComponente IN :componenteIds")
    long countByComponenteIds(@Param("componenteIds") List<Integer> componenteIds);
    
    /**
     * Encuentra todos los casos cuya columna idComponente esté en la lista de IDs proporcionada.
     */
    List<Caso> findAllByIdComponenteIn(List<Integer> idComponentes);
    

    List<Caso> findByIdComponenteAndActivo(int idComponente, int activo);
    
    /**
     * Busca todos los casos activos asignados a un usuario específico
     * dentro de una lista de IDs de componentes.
     */
    List<Caso> findByActivoAndIdUsuarioAsignadoAndIdComponenteIn(int activo, int idUsuarioAsignado, List<Integer> idComponentes);
	

}
