package cl.rac.gesprub.Repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.rac.gesprub.Entidad.Caso;

@Repository
public interface CasoRepository extends JpaRepository<Caso, Long>{
	
	List<Caso> findByIdComponente(int idComponente);

}
