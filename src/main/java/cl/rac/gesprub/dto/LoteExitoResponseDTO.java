package cl.rac.gesprub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoteExitoResponseDTO {
    private String mensaje;
    private int casosCreados;
    private int casosActualizados;
}