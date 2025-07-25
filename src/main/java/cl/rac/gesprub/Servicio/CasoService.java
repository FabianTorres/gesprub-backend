package cl.rac.gesprub.Servicio;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.rac.gesprub.Entidad.Caso;
import cl.rac.gesprub.Entidad.Evidencia;
import cl.rac.gesprub.Repositorio.CasoRepository;
import cl.rac.gesprub.Repositorio.EvidenciaRepository;
import cl.rac.gesprub.dto.CasoConEvidenciaDTO;

@Service
public class CasoService {
	
	@Autowired
    private CasoRepository casoRepository;
	
	@Autowired
    private EvidenciaRepository evidenciaRepository;
	
	public Caso createCaso(Caso caso) {
        return casoRepository.save(caso);
    }

    public List<Caso> getAllCasos() {
        return casoRepository.findAll();
    }

    public Caso getCasoById(Long id_caso) {
        return casoRepository.findById(id_caso).orElse(null);
    }

    public Caso updateCaso(Long id_caso, Caso caso) {
        caso.setId_caso(id_caso);
        return casoRepository.save(caso);
    }

    public void deleteCaso(Long id_caso) {
        casoRepository.deleteById(id_caso);
    }
    
    public List<CasoConEvidenciaDTO> getCasosConUltimaEvidencia() {
        List<Caso> casos = casoRepository.findAll();
        List<Evidencia> ultimas = evidenciaRepository.findUltimaEvidenciaPorCaso();

        Map<Integer, Evidencia> evidenciaMap = ultimas.stream()
            .collect(Collectors.toMap(Evidencia::getIdCaso, e -> e));

        return casos.stream()
            .map(caso -> new CasoConEvidenciaDTO(caso, evidenciaMap.get(caso.getId_caso().intValue())))
            .collect(Collectors.toList());
    }
    
    public List<Evidencia> getEvidenciasPorCaso(int idCaso) {
        return evidenciaRepository.findByIdCasoOrderByFechaEvidenciaDesc(idCaso);
    }
    
    public List<CasoConEvidenciaDTO> getCasosConUltimaEvidenciaPorComponente(int componenteId) {
        List<Caso> casos = casoRepository.findByIdComponente(componenteId);
        List<Evidencia> ultimas = evidenciaRepository.findUltimaEvidenciaPorCaso();

        Map<Integer, Evidencia> evidenciaMap = ultimas.stream()
            .collect(Collectors.toMap(Evidencia::getIdCaso, e -> e));

        return casos.stream()
            .map(c -> new CasoConEvidenciaDTO(c, evidenciaMap.get(c.getId_caso().intValue())))
            .collect(Collectors.toList());
    }

}
