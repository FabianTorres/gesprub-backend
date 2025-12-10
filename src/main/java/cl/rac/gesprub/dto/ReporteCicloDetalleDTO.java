package cl.rac.gesprub.dto;

import lombok.Getter;
import lombok.Setter;
import java.sql.Timestamp;

@Getter
@Setter
public class ReporteCicloDetalleDTO {
    private String nombreComponente;
    private Long idCaso;
    private String nombreCaso;
    private String versionCaso;
    private String estadoEjecucion; // OK, NK, etc. o null
    private String tester;          // Nombre del usuario
    private Timestamp fechaEjecucion;
    private Integer jiraDefecto;
    private String observacion;     // descripcion_evidencia
    private String actualizacion;

    public ReporteCicloDetalleDTO() {}
}