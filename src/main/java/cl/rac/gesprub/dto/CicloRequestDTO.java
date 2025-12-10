package cl.rac.gesprub.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
public class CicloRequestDTO {
    
    @NotBlank(message = "La clave de Jira no puede estar vacía.")
    private String jiraKey;
    
    @NotBlank(message = "El nombre del ciclo no puede estar vacío.")
    private String nombre;
    
    private String descripcion;
    
    private LocalDate fechaLiberacion;

    @NotNull(message = "El ID del usuario creador es obligatorio.")
    private Integer idUsuarioCreador;
    
    @NotNull(message = "El ID del proyecto es obligatorio.")
    private Long idProyecto; // Nuevo campo obligatorio
}