package cl.rac.gesprub.modulo.vector.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimulacionResponseDTO {
    private int totalRecibidos;
    private int cantidadNuevos;
    private int cantidadIgnorados;
    private List<ModificadoDetalleDTO> modificados;
}