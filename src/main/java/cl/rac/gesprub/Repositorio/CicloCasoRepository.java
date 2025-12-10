package cl.rac.gesprub.Repositorio;

import cl.rac.gesprub.Entidad.Ciclo;
import cl.rac.gesprub.Entidad.CicloCaso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CicloCasoRepository extends JpaRepository<CicloCaso, Integer> {

    List<CicloCaso> findByIdCiclo(Integer idCiclo);
    
    // Consulta para eliminar todos los casos de un ciclo
    @Modifying
    @Query("DELETE FROM CicloCaso cc WHERE cc.idCiclo = :idCiclo")
    void deleteByIdCiclo(@Param("idCiclo") Integer idCiclo);

    // Consulta para encontrar solo los IDs de los casos en un ciclo
    @Query("SELECT cc.idCaso FROM CicloCaso cc WHERE cc.idCiclo = :idCiclo")
    List<Long> findIdCasosByIdCiclo(@Param("idCiclo") Integer idCiclo);
    
    /**
     * Busca los ciclos activos asociados a un caso específico.
     * Utiliza la navegación JPA: CicloCaso -> Ciclo.
     */
    @Query("SELECT cc.ciclo FROM CicloCaso cc WHERE cc.idCaso = :idCaso AND cc.ciclo.activo = 1")
    List<Ciclo> findCiclosActivosByCaso(@Param("idCaso") Long idCaso);
    
    /**
     * Cuenta el total de casos asignados a un ciclo (Alcance Total).
     */
    long countByIdCiclo(Integer idCiclo);
    
    
    /**
     * Obtiene los ciclos activos para una lista de IDs de casos.
     * Retorna un Array de Objetos: [Long idCaso, Integer idCiclo, String jiraKey, String nombre]
     */
    @Query("SELECT cc.idCaso, c.idCiclo, c.jiraKey, c.nombre " +
           "FROM CicloCaso cc " +
           "JOIN cc.ciclo c " +
           "WHERE c.activo = 1 AND cc.idCaso IN :idsCasos")
    List<Object[]> findCiclosActivosParaVariosCasos(@Param("idsCasos") List<Long> idsCasos);
}