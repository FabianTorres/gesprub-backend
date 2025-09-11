package cl.rac.gesprub.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class KanbanDTO {
    private List<CasoDTO> porHacer;
    private List<CasoDTO> completado;
    private List<CasoDTO> conError;

    public KanbanDTO(List<CasoDTO> porHacer, List<CasoDTO> completado, List<CasoDTO> conError) {
        this.porHacer = porHacer;
        this.completado = completado;
        this.conError = conError;
    }
}