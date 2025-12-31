package cl.rac.gesprub.modulo.vector.repositorio;

import cl.rac.gesprub.modulo.vector.entidad.CatVectorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatVectorRepository extends JpaRepository<CatVectorEntity, Integer> {
    
}