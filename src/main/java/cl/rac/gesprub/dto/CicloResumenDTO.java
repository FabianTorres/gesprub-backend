package cl.rac.gesprub.dto;

import cl.rac.gesprub.Entidad.Ciclo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CicloResumenDTO {
    private Integer idCiclo;
    private String jiraKey;
    private String nombre;

    // Constructor vacío
    public CicloResumenDTO() {}

    // Constructor desde Entidad
    public CicloResumenDTO(Ciclo ciclo) {
        this.idCiclo = ciclo.getIdCiclo();
        this.jiraKey = ciclo.getJiraKey();
        this.nombre = ciclo.getNombre();
    }
}