package cl.rac.gesprub.dto;

import cl.rac.gesprub.Entidad.Caso;
import cl.rac.gesprub.Entidad.Evidencia;
import java.util.Set;
import java.util.Map;

public class CasoConEvidenciaDTO {
	
	private CasoDTO caso; 
    private EvidenciaDTO ultimaEvidencia; 
    private Set<String> rutsUnicos;
    
    public CasoConEvidenciaDTO(Caso caso, Evidencia ultimaEvidencia, Set<String> rutsUnicos) {
        // La conversión ocurre aquí, dentro del constructor
        this.caso = new CasoDTO(caso);
        if (ultimaEvidencia != null) {
            this.ultimaEvidencia = new EvidenciaDTO(ultimaEvidencia);
        } else {
            this.ultimaEvidencia = null;
        }
        this.rutsUnicos = rutsUnicos;
    }
    
    // Contructor mejorado
    public CasoConEvidenciaDTO(Caso caso, Evidencia ultimaEvidencia, Set<String> rutsUnicos, Map<Integer, String> componenteNombresMap) {
        // Usamos el nuevo constructor de CasoDTO
        this.caso = new CasoDTO(caso, componenteNombresMap.getOrDefault(caso.getIdComponente(), "Desconocido"));
        
        if (ultimaEvidencia != null) {
            this.ultimaEvidencia = new EvidenciaDTO(ultimaEvidencia);
        } else {
            this.ultimaEvidencia = null;
        }
        this.rutsUnicos = rutsUnicos;
    }
    
    public CasoDTO getCaso() {
        return caso;
    }

    public EvidenciaDTO getUltimaEvidencia() {
        return ultimaEvidencia;
    }
    
    public Set<String> getRutsUnicos() { 
        return rutsUnicos;
    }

}
