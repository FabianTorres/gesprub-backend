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
		
//		List<Evidencia> findByIdCasoOrderByFechaEvidenciaDesc(int idCaso);
		
		@EntityGraph(attributePaths = {"usuarioEjecutante", "ciclo"}) 
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
	    
	    
	    /**
	     * Obtiene el conteo agrupado por estado de la ÚLTIMA evidencia de cada caso
	     * dentro de un ciclo específico.
	     * Retorna una lista de arrays: [String estado, Long cantidad]
	     */
	    @Query(value = """
	        SELECT e.estado_evidencia, COUNT(*)
	        FROM evidencia e
	        INNER JOIN (
	            SELECT id_caso, MAX(fecha_evidencia) as max_fecha
	            FROM evidencia
	            WHERE id_ciclo = :idCiclo
	            GROUP BY id_caso
	        ) ultimas ON e.id_caso = ultimas.id_caso AND e.fecha_evidencia = ultimas.max_fecha
	        WHERE e.id_ciclo = :idCiclo
	        GROUP BY e.estado_evidencia
	    """, nativeQuery = true)
	    List<Object[]> countEstadosUltimaEvidenciaPorCiclo(@Param("idCiclo") Integer idCiclo);
	    
	    // Mtodo para traer todas las evidencias asociadas a un ciclo
	    List<Evidencia> findByIdCiclo(Integer idCiclo);
	    
	    
	    /**
	     * CORRECCIÓN DE ESTADÍSTICAS:
	     * Cuenta los estados de la última evidencia, PERO solo si el caso
	     * sigue existiendo en la tabla de asignaciones (ciclos_casos) del ciclo.
	     */
	    @Query(value = """
	        SELECT e.estado_evidencia, COUNT(*)
	        FROM evidencia e
	        -- EL JOIN SALVADOR: Valida que el caso siga asignado al ciclo
	        INNER JOIN ciclos_casos cc ON e.id_caso = cc.id_caso AND e.id_ciclo = cc.id_ciclo
	        -- Subquery para obtener la última evidencia (por si hubo reintentos)
	        INNER JOIN (
	            SELECT id_caso, MAX(fecha_evidencia) as max_fecha
	            FROM evidencia
	            WHERE id_ciclo = :idCiclo AND activo = 1
	            GROUP BY id_caso
	        ) ultimas ON e.id_caso = ultimas.id_caso AND e.fecha_evidencia = ultimas.max_fecha
	        WHERE e.id_ciclo = :idCiclo
	        AND e.activo = 1
	        GROUP BY e.estado_evidencia
	    """, nativeQuery = true)
	    List<Object[]> countEstadosEvidenciaValidadaPorCiclo(@Param("idCiclo") Integer idCiclo);
	    
	    

}
