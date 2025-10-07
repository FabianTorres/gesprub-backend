package cl.rac.gesprub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoteErrorDetalleDTO {
    private String tipo; // "CREACION" o "ACTUALIZACION"
    private String identificador; // "Fila con nombre '...'" o "ID Caso 999"
    private String detalle;
}