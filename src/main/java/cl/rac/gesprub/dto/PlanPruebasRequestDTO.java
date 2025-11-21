package cl.rac.gesprub.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class PlanPruebasRequestDTO {
    private List<Integer> ids_casos;
}