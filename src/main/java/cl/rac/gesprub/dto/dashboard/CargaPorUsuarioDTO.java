package cl.rac.gesprub.dto.dashboard;

import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Getter
@Setter
public class CargaPorUsuarioDTO {
    private Long idUsuario;
    private String nombreUsuario;
    private CasosAsignadosDTO casosAsignados;
    private Instant ultimaActividad;
}