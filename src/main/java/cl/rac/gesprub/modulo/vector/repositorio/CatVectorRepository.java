package cl.rac.gesprub.modulo.vector.repositorio;

import cl.rac.gesprub.modulo.vector.entidad.CatVectorEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatVectorRepository extends JpaRepository<CatVectorEntity, Long> {
	// Buscar activos por periodo
    List<CatVectorEntity> findByPeriodoAndEstadoTrue(Integer periodo);
    
    // Buscar todos por periodo (incluye eliminados)
    List<CatVectorEntity> findByPeriodo(Integer periodo);

    // Validar existencia única en el periodo
    boolean existsByVectorIdAndPeriodo(Integer vectorId, Integer periodo);
    
    // Buscar por ID Negocio y Periodo (para bajas)
    CatVectorEntity findByVectorIdAndPeriodo(Integer vectorId, Integer periodo);
    
}