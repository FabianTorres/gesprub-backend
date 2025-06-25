package cl.rac.gesprub.Repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.rac.gesprub.Entidad.Componente;

@Repository
public interface ComponenteRepository extends JpaRepository<Componente, Long>{

}
