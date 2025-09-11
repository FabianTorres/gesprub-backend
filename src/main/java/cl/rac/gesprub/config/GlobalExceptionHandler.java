package cl.rac.gesprub.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import cl.rac.gesprub.dto.ImportResultDTO;
import cl.rac.gesprub.exception.ImportValidationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        // Cuando cualquier controlador lance una IllegalArgumentException,
        // este método la atrapará y devolverá una respuesta HTTP 400 (Bad Request)
        // con el mensaje de la excepción en el cuerpo.
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Atrapa nuestra excepción de importación personalizada y devuelve una respuesta
     * estructurada con todos los detalles de los errores de validación.
     */
    @ExceptionHandler(ImportValidationException.class)
    public ResponseEntity<ImportResultDTO> handleImportValidationException(ImportValidationException ex) {
        ImportResultDTO errorResponse = new ImportResultDTO(ex.getMessage(), ex.getErrores());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}