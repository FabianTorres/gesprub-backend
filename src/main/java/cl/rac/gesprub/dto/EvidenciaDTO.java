package cl.rac.gesprub.dto;

import cl.rac.gesprub.Entidad.Evidencia;
import lombok.Getter;
import lombok.Setter;
import java.sql.Timestamp;

@Getter
@Setter
public class EvidenciaDTO {
    // Copiamos los campos que el frontend necesita de la entidad Evidencia
    private Long id_evidencia;
    private String descripcion_evidencia;
    private String estado_evidencia;
    private int id_estado_evidencia;
    private String version_ejecucion;
    private String rut;
    private Timestamp fechaEvidencia;
    private String url_evidencia;
    private int id_jira;
    private int idCaso;
    private String criticidad;
    private Integer id_criticidad;
    private int activo;

    // Un constructor que facilita la conversión
    public EvidenciaDTO(Evidencia evidencia) {
        this.id_evidencia = evidencia.getId_evidencia();
        this.descripcion_evidencia = evidencia.getDescripcion_evidencia();
        this.estado_evidencia = evidencia.getEstado_evidencia();
        this.id_estado_evidencia = evidencia.getId_estado_evidencia();
        this.version_ejecucion = evidencia.getVersion_ejecucion();
        this.fechaEvidencia = evidencia.getFechaEvidencia();
        this.url_evidencia = evidencia.getUrl_evidencia();
        this.id_jira = evidencia.getId_jira();
        this.idCaso = evidencia.getIdCaso();
        this.criticidad = evidencia.getCriticidad();
        this.id_criticidad = evidencia.getId_criticidad();
        this.rut = evidencia.getRut();
        this.activo = evidencia.getActivo();
    }
}