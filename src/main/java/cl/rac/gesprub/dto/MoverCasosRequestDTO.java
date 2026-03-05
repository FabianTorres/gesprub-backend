package cl.rac.gesprub.dto;

import java.util.List;

public class MoverCasosRequestDTO {
    private List<Long> idsCasos;
    private Integer idComponenteDestino;

    public List<Long> getIdsCasos() { return idsCasos; }
    public void setIdsCasos(List<Long> idsCasos) { this.idsCasos = idsCasos; }
    
    public Integer getIdComponenteDestino() { return idComponenteDestino; }
    public void setIdComponenteDestino(Integer idComponenteDestino) { this.idComponenteDestino = idComponenteDestino; }
}