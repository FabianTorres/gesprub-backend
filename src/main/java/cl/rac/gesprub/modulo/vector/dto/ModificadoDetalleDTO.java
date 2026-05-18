package cl.rac.gesprub.modulo.vector.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
public class ModificadoDetalleDTO {
    private Long rut;
    private Integer vector;
    private Long valorAntiguo;
    private Long valorNuevo;
}