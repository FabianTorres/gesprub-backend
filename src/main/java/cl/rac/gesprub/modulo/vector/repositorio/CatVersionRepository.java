package cl.rac.gesprub.modulo.vector.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.rac.gesprub.modulo.vector.entidad.CatVersionEntity;

@Repository
public interface CatVersionRepository extends JpaRepository<CatVersionEntity, Long> {
    List<CatVersionEntity> findByPeriodoOrderByFechaRegistroDesc(Integer periodo);
    boolean existsByPeriodoAndCodigoVersion(Integer periodo, String codigoVersion);
    // Para el rollover: buscar la versión 1.0 de un periodo
    CatVersionEntity findByPeriodoAndCodigoVersion(Integer periodo, String codigoVersion);
}
