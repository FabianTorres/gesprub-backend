package cl.rac.gesprub.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class DatasetDTO {
    private String label;
    private List<Long> data;
}