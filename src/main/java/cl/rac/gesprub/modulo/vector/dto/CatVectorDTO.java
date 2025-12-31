package cl.rac.gesprub.modulo.vector.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CatVectorDTO {
    private Integer vectorId;
    private String nombre;
    private String tipoTecnologia; // "BATCH" o "BIGDATA_INTEGRADO"
    
    // Constructores auxiliares
    public CatVectorDTO() {}
    
    public CatVectorDTO(Integer vectorId, String nombre, String tipoTecnologia) {
        this.vectorId = vectorId;
        this.nombre = nombre;
        this.tipoTecnologia = tipoTecnologia;
    }
}