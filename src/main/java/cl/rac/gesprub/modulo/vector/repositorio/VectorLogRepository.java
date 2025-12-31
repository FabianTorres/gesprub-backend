package cl.rac.gesprub.modulo.vector.repositorio;

import cl.rac.gesprub.modulo.vector.entidad.VectorLogEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VectorLogRepository extends JpaRepository<VectorLogEntity, Long> {
	
	List<VectorLogEntity> findTop100ByOrderByLogFechaDesc();
	
}