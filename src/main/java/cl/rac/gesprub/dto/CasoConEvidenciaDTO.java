package cl.rac.gesprub.dto;

import cl.rac.gesprub.Entidad.Caso;
import cl.rac.gesprub.Entidad.Evidencia;

public class CasoConEvidenciaDTO {
	
	private CasoDTO caso; // <-- CAMBIO: De Entidad a DTO
    private EvidenciaDTO ultimaEvidencia; // <-- CAMBIO: De Entidad a DTO
    
    public CasoConEvidenciaDTO(Caso caso, Evidencia ultimaEvidencia) {
        // La conversión ocurre aquí, dentro del constructor
        this.caso = new CasoDTO(caso);
        if (ultimaEvidencia != null) {
            this.ultimaEvidencia = new EvidenciaDTO(ultimaEvidencia);
        } else {
            this.ultimaEvidencia = null;
        }
    }
    
    public CasoDTO getCaso() {
        return caso;
    }

    public EvidenciaDTO getUltimaEvidencia() {
        return ultimaEvidencia;
    }

}
