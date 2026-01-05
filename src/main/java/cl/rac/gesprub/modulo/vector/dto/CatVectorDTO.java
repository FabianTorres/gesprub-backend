package cl.rac.gesprub.modulo.vector.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CatVectorDTO {
	private Long id;
    private Integer vectorId;
    private Integer periodo;
    private String nombre;
    private String tipoTecnologia;
    private Boolean estado;
    private String versionIngreso;
    private String versionRetiro;
    
    // Constructores auxiliares
    public CatVectorDTO() {}
    
    public CatVectorDTO(Integer vectorId, String nombre, String tipoTecnologia) {
        this.vectorId = vectorId;
        this.nombre = nombre;
        this.tipoTecnologia = tipoTecnologia;
    }

	public CatVectorDTO(Long id, Integer vectorId, Integer periodo, String nombre, String tipoTecnologia, Boolean estado,
			String versionIngreso, String versionRetiro) {
		this.id = id;
		this.vectorId = vectorId;
		this.periodo = periodo;
		this.nombre = nombre;
		this.tipoTecnologia = tipoTecnologia;
		this.estado = estado;
		this.versionIngreso = versionIngreso;
		this.versionRetiro = versionRetiro;
	}
    
    
}