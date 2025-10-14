package cl.rac.gesprub.dto.dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CasosNkDetalleDTO {
    private long total = 0;
    private long leve = 0;
    private long medio = 0;
    private long grave = 0;
    private long critico = 0;
}