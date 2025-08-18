package cl.rac.gesprub.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class ComponenteDTO {
    private Long id_componente;
    private String nombre_componente;
    private int hito_componente;
    private LocalDate fecha_limite;
    private int activo;
    private Long id_proyecto; 
    private String nombre_proyecto;
    private int id_ambito; 
}