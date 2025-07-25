package cl.rac.gesprub.Repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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

}
