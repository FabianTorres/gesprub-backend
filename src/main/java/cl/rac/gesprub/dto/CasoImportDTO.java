package cl.rac.gesprub.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CasoImportDTO {

    // Añadimos validaciones para los campos que consideramos obligatorios.
    // El frontend recibirá un error si alguno de estos falta.

    @NotBlank(message = "El nombre del caso no puede estar vacío.")
    private String nombre_caso;
    
    @NotBlank(message = "La descripción no puede estar vacía.")
    private String descripcion_caso;
    
    @NotBlank(message = "La versión no puede estar vacía.")
    private String version;

    private String nombre_estado_modificacion;
    
    private String nombres_fuentes;
    
    private Integer id_usuario_creador;
    
    private String precondiciones;
    
    private String pasos;
    
    private String resultado_esperado;

}