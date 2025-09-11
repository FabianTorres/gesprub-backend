package cl.rac.gesprub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor // Un constructor para asignar fácilmente los valores
public class ValidationErrorDTO {
    private int fila;          // El número de la fila en el archivo importado (ej: 5)
    private String campo;      // El nombre del campo con error (ej: "nombre_caso")
    private String mensaje;    // La descripción del error (ej: "no puede estar vacío")
    private String valor;      // El valor incorrecto que se intentó ingresar (ej: "")
}