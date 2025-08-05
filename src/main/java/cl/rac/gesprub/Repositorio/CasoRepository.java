package cl.rac.gesprub.Repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import cl.rac.gesprub.Entidad.Caso;

@Repository
public interface CasoRepository extends JpaRepository<Caso, Long>{
	
	List<Caso> findByIdComponente(int idComponente);
	
	@Query("SELECT DISTINCT c.num_formulario FROM Caso c WHERE c.num_formulario IS NOT NULL ORDER BY c.num_formulario ASC")
    List<Integer> findDistinctNumFormulario();

}
