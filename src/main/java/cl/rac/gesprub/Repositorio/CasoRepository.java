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
	
    
    /**
     * Busca casos activos, asignados y dentro de una lista de componentes.
     */
    List<Caso> findByActivoAndIdUsuarioAsignadoIsNotNullAndIdComponenteIn(int activo, List<Integer> idComponentes);
    
    
    /**
     * Busca todos los casos activos que pertenecen a una lista de componentes.
     */
    List<Caso> findByActivoAndIdComponenteIn(int activo, List<Integer> idComponentes);
    
    
    // 1. Buscar todos los casos activos (Global)
    List<Caso> findByActivo(Integer activo);

    // 2. Buscar casos activos por componente (Específico)
    List<Caso> findByIdComponenteAndActivo(int idComponente, Integer activo);
    
    
    /**
     * Busca casos activos de un componente, permitiendo un filtro opcional por estado de modificación.
     * Lógica: (idEstadoMod IS NULL) -> Trae todos los del componente.
     * (idEstadoMod TIENE VALOR) -> Trae solo los que coincidan.
     */
    @Query("SELECT c FROM Caso c " +
           "WHERE c.idComponente = :idComponente " +
           "AND c.activo = 1 " +
           "AND (:idEstadoMod IS NULL OR c.id_estado_modificacion = :idEstadoMod)")
    List<Caso> findByComponenteAndEstadoModificacionOpcional(
            @Param("idComponente") int idComponente, 
            @Param("idEstadoMod") Integer idEstadoMod);
}
