package cl.rac.gesprub.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetallePlanPruebasDTO {
    private Long id_caso;
    private String nombre_componente;
    private String nombre_caso;
    private String pasos;
    private String resultado_esperado;
    
    // Datos de la Última Evidencia
    private String version_caso;
    private String version_evidencia;
    private String rut_evidencia;
    private String nombre_analista;
    private String resultado_evidencia;
    private Integer id_jira;
    
    // Archivos concatenados
    private String nombres_archivos;

    // Constructor vacío
    public DetallePlanPruebasDTO() {}
}