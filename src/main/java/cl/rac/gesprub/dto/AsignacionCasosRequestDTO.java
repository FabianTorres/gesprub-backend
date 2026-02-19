package cl.rac.gesprub.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class AsignacionCasosRequestDTO {
    
	@NotNull(message = "La lista de IDs no puede ser nula.")
    private List<Long> idsCasos;
}