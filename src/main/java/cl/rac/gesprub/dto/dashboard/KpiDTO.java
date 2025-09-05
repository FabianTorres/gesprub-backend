package cl.rac.gesprub.dto.dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KpiDTO {
    private long totalCasos;
    private long totalEjecuciones;
    private long casosSinEjecutar;
    private double promedioEjecucionesDiarias;
}