package cl.rac.gesprub.dto.dashboard;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ProductividadDashboardDTO {
    private List<CargaPorUsuarioDTO> cargaPorUsuario;
    private List<EjecucionesPorPeriodoDTO> ejecucionesPorPeriodo;
}