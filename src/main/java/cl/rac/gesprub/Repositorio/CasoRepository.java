package cl.rac.gesprub.Repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.rac.gesprub.Entidad.Caso;

@Repository
public interface CasoRepository extends JpaRepository<Caso, Long>{

}
