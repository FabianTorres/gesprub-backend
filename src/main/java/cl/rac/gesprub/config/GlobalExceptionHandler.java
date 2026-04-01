package cl.rac.gesprub.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import cl.rac.gesprub.dto.ImportResultDTO;
import cl.rac.gesprub.dto.LoteErrorResponseDTO;
import cl.rac.gesprub.exception.BatchValidationException;
import cl.rac.gesprub.exception.ImportValidationException;

import java.util.HashMap;
import java.util.Map;


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
    
    
    @ExceptionHandler(BatchValidationException.class)
    public ResponseEntity<LoteErrorResponseDTO> handleBatchValidationException(BatchValidationException ex) {
        LoteErrorResponseDTO errorResponse = new LoteErrorResponseDTO(ex.getMessage(), ex.getErrores());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    
    /**
     * Atrapa errores cuando el JSON viene mal formado o con tipos de datos incorrectos
     * (Ejemplo: Viene el texto "undefined" en vez de un número, o falta una coma en el JSON).
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Error en el formato de los datos enviados. Verifique que el Excel no tenga filas vacías o datos corruptos (como 'undefined').");
        // Opcional: imprimir el error real en consola para ti
        System.err.println("JSON parse error: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Atrapa errores de las validaciones de Spring (@Valid, @NotNull, @NotEmpty)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> response = new HashMap<>();
        
        // Extraemos el primer error de validación que encuentre
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> "El campo '" + error.getField() + "' " + error.getDefaultMessage())
                .findFirst()
                .orElse("Datos de entrada no válidos.");

        response.put("mensaje", errorMessage);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * (Opcional pero Recomendado) El "Atrapalotodo" final.
     * Si ocurre un error rarísimo (Ej: NullPointerException), esto evita que vaya a /error y lance 403.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllUncaughtException(Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Ocurrió un error inesperado en el servidor: " + ex.getMessage());
        ex.printStackTrace(); // Para que lo veas en la consola
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * Atrapa errores de validación en Listas (Novedad en Spring Boot 3.2+)
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, String>> handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
        Map<String, String> response = new HashMap<>();
        
        // Mensaje limpio para el frontend
        response.put("mensaje", "Error de validación en los datos enviados. Verifique que no haya filas vacías o campos obligatorios sin llenar.");
        
        System.err.println("Error de validación en Lista: " + ex.getMessage());
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    
}