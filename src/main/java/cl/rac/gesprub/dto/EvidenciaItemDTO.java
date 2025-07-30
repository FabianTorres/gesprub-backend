package cl.rac.gesprub.dto;

import lombok.Getter;
import lombok.Setter;
import java.sql.Timestamp;

@Getter
@Setter
public class EvidenciaItemDTO {
    private Long id_evidencia;
    private String descripcion_evidencia;
    private String estado_evidencia;
    private Timestamp fechaEvidencia;
    private String criticidad;
    private String url_evidencia;
    private String nombreUsuarioEjecutante; // El campo extra que necesitamos
}