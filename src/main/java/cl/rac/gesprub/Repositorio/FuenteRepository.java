package cl.rac.gesprub.Repositorio;

import cl.rac.gesprub.Entidad.Fuente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FuenteRepository extends JpaRepository<Fuente, Long> {
   
}