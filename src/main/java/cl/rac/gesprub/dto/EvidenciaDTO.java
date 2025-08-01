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
    private Timestamp fechaEvidencia;
    private String url_evidencia;
    private int id_jira;
    private int idCaso;
    private String criticidad;
    // ¡Importante! No incluimos el objeto 'usuarioEjecutante' para evitar el error.
    // Si necesitas el nombre, lo añadiríamos aquí como un String.

    // Un constructor que facilita la conversión
    public EvidenciaDTO(Evidencia evidencia) {
        this.id_evidencia = evidencia.getId_evidencia();
        this.descripcion_evidencia = evidencia.getDescripcion_evidencia();
        this.estado_evidencia = evidencia.getEstado_evidencia();
        this.fechaEvidencia = evidencia.getFechaEvidencia();
        this.url_evidencia = evidencia.getUrl_evidencia();
        this.id_jira = evidencia.getId_jira();
        this.idCaso = evidencia.getIdCaso();
        this.criticidad = evidencia.getCriticidad();
    }
}