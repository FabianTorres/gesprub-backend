package cl.rac.gesprub.dto.dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EjecucionesPorPeriodoDTO {
    private Long idUsuario;
    private String nombreUsuario;
    private long totalEjecuciones;
}