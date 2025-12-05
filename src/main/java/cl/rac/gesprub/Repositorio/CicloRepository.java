package cl.rac.gesprub.Repositorio;

import cl.rac.gesprub.Entidad.Ciclo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CicloRepository extends JpaRepository<Ciclo, Integer> {
    
    List<Ciclo> findByActivo(Integer activo);
}