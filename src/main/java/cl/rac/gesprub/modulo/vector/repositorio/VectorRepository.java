package cl.rac.gesprub.modulo.vector.repositorio;

import cl.rac.gesprub.modulo.vector.entidad.VectorEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VectorRepository extends JpaRepository<VectorEntity, Long> {
    
    // Metodo para validar duplicados (RUT + PERIODO + VECTOR)
    boolean existsByRutAndPeriodoAndVector(Long rut, Integer periodo, Integer vector);
    
    /**
     * Trae solo los registros cuyo vector en el catalogo sea 'BATCH'
     * Hacemos un JOIN implicito (o cartesiano filtrado) entre la tabla de datos y el catálogo.
     */
    @Query("SELECT v FROM VectorEntity v, CatVectorEntity c WHERE v.vector = c.vectorId AND c.tipoTecnologia = 'BATCH'")
    List<VectorEntity> findAllBatchVectors();

    /**
     * Trae solo los registros cuyo vector en el catalogo sea 'BIGDATA_INTEGRADO'
     */
    @Query("SELECT v FROM VectorEntity v, CatVectorEntity c WHERE v.vector = c.vectorId AND c.tipoTecnologia = 'BIGDATA_INTEGRADO'")
    List<VectorEntity> findAllBigDataVectors();
}