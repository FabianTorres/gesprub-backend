package cl.rac.gesprub.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class HistorialDTO {
    // Datos del Caso
    private Long id_caso;
    private String nombre_caso;
    private String descripcion_caso;
    
    // Lista de Evidencias
    private List<EvidenciaItemDTO> historial;
}