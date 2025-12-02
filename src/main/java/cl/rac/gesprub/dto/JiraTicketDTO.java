package cl.rac.gesprub.dto;

import lombok.Getter;
import lombok.Setter;
import java.sql.Date;

@Getter
@Setter
public class JiraTicketDTO {
    private String clave_jira;      // Clave única (Ej: PROJ-123)
    private String descripcion;
    private Date fecha_creacion;
    private Date fecha_finalizacion;
    private String responsable;
    private String estado;
    private String criticidad;
    
    // Campos adicionales que mencionaste (si la entidad Jira los soporta en el futuro)
    private String creador;
    private String tipo;
}