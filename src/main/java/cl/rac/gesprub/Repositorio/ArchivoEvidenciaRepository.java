package cl.rac.gesprub.Repositorio;

import cl.rac.gesprub.Entidad.ArchivoEvidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ArchivoEvidenciaRepository extends JpaRepository<ArchivoEvidencia, Long> {

    @Query("SELECT a FROM ArchivoEvidencia a WHERE a.evidencia.id_evidencia = :idEvidencia")
    List<ArchivoEvidencia> findByEvidenciaId(@Param("idEvidencia") Long idEvidencia);
}