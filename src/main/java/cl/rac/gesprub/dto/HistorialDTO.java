package cl.rac.gesprub.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class HistorialDTO {
    // Datos del Caso
    private Long id_caso;
    private String nombre_caso;
    private String descripcion_caso;
    private Integer num_formulario;
    private String fuente;
    private int id_estado_modificacion;
    private Set<FuenteDTO> fuentes;
    
    
    // Lista de Evidencias
    private List<EvidenciaItemDTO> historial;
}