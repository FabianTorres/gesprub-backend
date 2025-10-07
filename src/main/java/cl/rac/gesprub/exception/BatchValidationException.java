package cl.rac.gesprub.exception;

import cl.rac.gesprub.dto.LoteErrorDetalleDTO;
import lombok.Getter;
import java.util.List;

@Getter
public class BatchValidationException extends RuntimeException {
    private final List<LoteErrorDetalleDTO> errores;

    public BatchValidationException(String message, List<LoteErrorDetalleDTO> errores) {
        super(message);
        this.errores = errores;
    }
}