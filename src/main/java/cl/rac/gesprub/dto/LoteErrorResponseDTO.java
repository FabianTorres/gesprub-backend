package cl.rac.gesprub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class LoteErrorResponseDTO {
    private String mensaje;
    private List<LoteErrorDetalleDTO> errores;
}