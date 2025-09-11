package cl.rac.gesprub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MuroDTO {
    private List<CasoDTO> backlog;
    private List<CasoDTO> misTareas;
}