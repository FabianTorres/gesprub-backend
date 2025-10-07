package cl.rac.gesprub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CasoLoteDTO {
    // Para actualizaciones
    private Long id_caso;

    // Campos comunes
    @NotBlank(message = "El nombre del caso no puede estar vacío.")
    private String nombre_caso;

    @NotBlank(message = "La descripción no puede estar vacía.")
    private String descripcion_caso;

    @NotBlank(message = "La versión no puede estar vacía.")
    private String version;

    // Para creación
    @NotNull(message = "El id_componente no puede ser nulo.")
    private Integer id_componente;
    
    @NotNull(message = "El id_usuario_creador no puede ser nulo.")
    private Integer id_usuario_creador;

    // Campos a resolver
    @NotBlank(message = "El nombre del estado de modificación no puede estar vacío.")
    private String nombre_estado_modificacion;
    
    private String nombres_fuentes;
    
    // Campos opcionales
    private String precondiciones;
    private String pasos;
    private String resultado_esperado;
}