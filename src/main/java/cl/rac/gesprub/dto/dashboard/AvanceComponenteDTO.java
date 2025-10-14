package cl.rac.gesprub.dto.dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvanceComponenteDTO {
    private Long idComponente;
    private String nombreComponente;
    private long totalCasos;
    private long casosOk;
    private CasosNkDetalleDTO casosNk;
    private long casosSinEjecutar;
}