package cl.rac.gesprub.modulo.vector.repositorio;

import cl.rac.gesprub.modulo.vector.entidad.VectorEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface VectorRepository extends JpaRepository<VectorEntity, Long> {
    
    // Metodo para validar duplicados (RUT + PERIODO + VECTOR)
	boolean existsByRutAndPeriodoAndVector(Long rut, Integer periodo, Integer vector);
    
    /**
     * RIESGOSO, SE ELIMINA
     * Trae solo los registros cuyo vector en el catalogo sea 'BATCH'
     * Hacemos un JOIN implicito (o cartesiano filtrado) entre la tabla de datos y el catálogo.
     */
//    @Query("SELECT v FROM VectorEntity v, CatVectorEntity c WHERE v.vector = c.vectorId AND c.tipoTecnologia = 'BATCH'")
//    List<VectorEntity> findAllBatchVectors();

    /**	
     * Trae solo los registros cuyo vector en el catalogo sea 'BIGDATA_INTEGRADO'
     */
    @Query("SELECT v FROM VectorEntity v, CatVectorEntity c WHERE v.vector = c.vectorId AND c.tipoTecnologia = 'BIGDATA_INTEGRADO'")
    List<VectorEntity> findAllBigDataVectors();
    
    List<VectorEntity> findByPeriodo(Integer periodo);
    
   
    /**
     * EXPORTACIÓN BIGDATA (TXT) - "CATALOG DRIVEN"
     * Lógica:
     * 1. Ignoramos la etiqueta del dato.
     * 2. Exigimos que el catálogo diga 'BIGDATA_INTEGRADO'.
     * Resultado: El vector 253 saldrá aquí SIEMPRE, incluso el registro que dice 'NOMCES'.
     */
    @Query("SELECT v FROM VectorEntity v " +
           "JOIN CatVectorEntity c ON v.vector = c.vectorId AND v.periodo = c.periodo " +
           "WHERE v.periodo = :periodo " +
           "AND c.tipoTecnologia = 'BIGDATA_INTEGRADO' " +
           "AND (v.vector <> 599 OR (v.vector = 599 AND (v.intencionCarga IS NULL OR v.intencionCarga = 'INSERT')))")
    List<VectorEntity> findForBigDataExport(@Param("periodo") Integer periodo);
    
    /**
     * EXPORTACIÓN BATCH (SQL) - "CATALOG DRIVEN"
     * Lógica: 
     * 1. No miramos v.elvcSeq (ignoramos si dice NOMCES o BD_RAC).
     * 2. Exigimos que la definición en CAT_VECTORES para ese periodo sea 'BATCH'.
     * Resultado: El vector 253 (que es BigData en catálogo) NUNCA saldrá aquí.
     */
    @Query("SELECT v FROM VectorEntity v " +
           "JOIN CatVectorEntity c ON v.vector = c.vectorId AND v.periodo = c.periodo " +
           "WHERE v.periodo = :periodo " +
           "AND c.tipoTecnologia = 'BATCH'")
    List<VectorEntity> findForBatchExport(@Param("periodo") Integer periodo);

    // 2. Para el reporte de modificaciones (Excel/CSV):
    // Usamos 'v.periodo' y 'v.vector'
    @Query("SELECT v FROM VectorEntity v WHERE v.periodo = :periodo " +
           "AND v.vector = 599 AND v.intencionCarga = 'UPDATE' AND (v.procesado IS NULL OR v.procesado = false)")
    List<VectorEntity> findModificacionesPendientes(@Param("periodo") Integer periodo);

    // 3. Modificación masiva para marcar como enviados
    // Usamos 'v.periodo' y 'v.vector'
    @Modifying
    @Query("UPDATE VectorEntity v SET v.procesado = true WHERE v.periodo = :periodo AND v.vector = 599 AND v.intencionCarga = 'UPDATE'")
    void marcarModificacionesComoProcesadas(@Param("periodo") Integer periodo);
    
    
}