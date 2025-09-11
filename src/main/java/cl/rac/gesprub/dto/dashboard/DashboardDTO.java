package cl.rac.gesprub.dto.dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardDTO {
    private KpiDTO kpis;
    private ChartDTO estadoEjecuciones;
    private ChartDTO actividadSemanal;
    private ChartDTO casosPorEstado;
}