package cl.rac.gesprub.dto.dashboard;

import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Getter
@Setter
public class ActividadRecienteDTO {
    private Long idCaso;
    private String nombreCaso;
    private String estado;
    private String nombreTester;
    private Instant fechaEjecucion;
}