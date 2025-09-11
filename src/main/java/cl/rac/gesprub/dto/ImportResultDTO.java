package cl.rac.gesprub.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ImportResultDTO {
    private String mensaje;
    private List<ValidationErrorDTO> errores;

    public ImportResultDTO(String mensaje, List<ValidationErrorDTO> errores) {
        this.mensaje = mensaje;
        this.errores = errores;
    }
}