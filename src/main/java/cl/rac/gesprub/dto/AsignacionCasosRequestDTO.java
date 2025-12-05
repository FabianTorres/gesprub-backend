package cl.rac.gesprub.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import jakarta.validation.constraints.NotEmpty;

@Getter
@Setter
public class AsignacionCasosRequestDTO {
    
    @NotEmpty(message = "La lista de IDs de casos no puede estar vacía.")
    private List<Long> idsCasos;
}