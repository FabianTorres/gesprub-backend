package cl.rac.gesprub.dto.dashboard;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class DashboardGeneralDTO {
    private KpiGeneralDTO kpis;
    private DistribucionEstadosDTO distribucionEstados;
    private List<ActividadRecienteDTO> actividadReciente;
}