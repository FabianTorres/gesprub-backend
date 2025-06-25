package cl.rac.gesprub.Repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.rac.gesprub.Entidad.Evidencia;

@Repository
public interface EvidenciaRepository extends JpaRepository<Evidencia, Long>{

}
