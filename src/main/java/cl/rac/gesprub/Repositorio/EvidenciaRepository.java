package cl.rac.gesprub.Repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;

import cl.rac.gesprub.Entidad.Evidencia;

@Repository
public interface EvidenciaRepository extends JpaRepository<Evidencia, Long>{
	
	
		@Query(value = """
	        SELECT e.* FROM evidencia e
	        INNER JOIN (
	            SELECT id_caso, MAX(fecha_evidencia) as max_fecha
	            FROM evidencia
	            GROUP BY id_caso
	        ) ult
	        ON e.id_caso = ult.id_caso AND e.fecha_evidencia = ult.max_fecha
	        """, nativeQuery = true)
	    List<Evidencia> findUltimaEvidenciaPorCaso();
		
		List<Evidencia> findByIdCasoOrderByFechaEvidenciaDesc(int idCaso);
		
		@Query("SELECT DISTINCT e.rut FROM Evidencia e WHERE e.idCaso = :idCaso AND e.rut IS NOT NULL AND e.rut <> ''")
		List<String> findDistinctRutByIdCaso(@Param("idCaso") int idCaso);
		
		
		/**
	     * Busca todas las evidencias que pertenecen a una lista de IDs de casos.
	     */
	    @Query("SELECT e FROM Evidencia e WHERE e.idCaso IN :idCasos")
	    List<Evidencia> findByIdCasoIn(@Param("idCasos") List<Integer> idCasos);
	    
	    
	    /**
	     * Busca las 5 evidencias más recientes y trae sus Casos y Usuarios asociados
	     * en una sola consulta para evitar problemas de rendimiento (N+1).
	     */
	    @EntityGraph(attributePaths = {"usuarioEjecutante"}) 
	    List<Evidencia> findTop5ByIdCasoInOrderByFechaEvidenciaDesc(List<Integer> idCasos);
	    
	    

}
