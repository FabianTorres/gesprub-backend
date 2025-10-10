package cl.rac.gesprub.dto.dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KpiGeneralDTO {
    private long totalCasos;
    private long casosEjecutados;
    private long casosPendientes;
    private double porcentajeAvance;
}