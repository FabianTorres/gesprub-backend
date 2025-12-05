package cl.rac.gesprub.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class CerrarCicloRequestDTO {
    
    @NotNull(message = "El ID del usuario de cierre es obligatorio.")
    private Integer idUsuarioCierre;
}