package cl.rac.gesprub.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CasoActualizarLoteDTO {
    // Para actualizar, solo el ID es obligatorio
    @NotNull(message = "El id_caso es obligatorio para actualizar.")
    private Long id_caso;

    // El resto de los campos son opcionales (sparse object), por lo que NO llevan validaciones
    private Integer activo; 
    private String nombre_caso;
    private String descripcion_caso;
    private String version;
    private String nombre_estado_modificacion;
    private String nombres_fuentes;
    private String precondiciones;
    private String pasos;
    private String resultado_esperado;
    private Integer jp_responsable;
}