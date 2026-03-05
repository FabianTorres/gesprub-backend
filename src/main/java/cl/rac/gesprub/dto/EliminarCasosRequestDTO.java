package cl.rac.gesprub.dto;

import java.util.List;

public class EliminarCasosRequestDTO {
    private List<Long> idsCasos;

    public List<Long> getIdsCasos() { return idsCasos; }
    public void setIdsCasos(List<Long> idsCasos) { this.idsCasos = idsCasos; }
}