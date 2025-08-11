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
    private String version_ejecucion;
    private Timestamp fecha_evidencia;
    private String criticidad;
    private String url_evidencia;
	private int id_jira;
	private String rut;
    private String nombreUsuarioEjecutante; 
}