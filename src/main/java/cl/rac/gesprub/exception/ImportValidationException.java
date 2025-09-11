package cl.rac.gesprub.exception;

import cl.rac.gesprub.dto.ValidationErrorDTO;
import lombok.Getter;
import java.util.List;

@Getter
public class ImportValidationException extends RuntimeException {

    private final List<ValidationErrorDTO> errores;

    public ImportValidationException(String message, List<ValidationErrorDTO> errores) {
        super(message);
        this.errores = errores;
    }
}