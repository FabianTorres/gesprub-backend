package cl.rac.gesprub.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AsignarCasoDTO {
    @NotNull(message = "El campo usuarioId no puede ser nulo.")
    private Long usuarioId;
}