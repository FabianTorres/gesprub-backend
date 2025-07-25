package cl.rac.gesprub.dto;

import cl.rac.gesprub.Entidad.Caso;
import cl.rac.gesprub.Entidad.Evidencia;

public class CasoConEvidenciaDTO {
	
	private Caso caso;
    private Evidencia ultimaEvidencia;
    
    public CasoConEvidenciaDTO(Caso caso, Evidencia ultimaEvidencia) {
        this.caso = caso;
        this.ultimaEvidencia = ultimaEvidencia;
    }

    public Caso getCaso() {
        return caso;
    }

    public Evidencia getUltimaEvidencia() {
        return ultimaEvidencia;
    }

}
