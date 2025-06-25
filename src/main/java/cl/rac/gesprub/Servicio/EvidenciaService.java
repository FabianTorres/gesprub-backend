package cl.rac.gesprub.Servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.rac.gesprub.Entidad.Evidencia;
import cl.rac.gesprub.Repositorio.EvidenciaRepository;


@Service
public class EvidenciaService {
	
	@Autowired
    private EvidenciaRepository evidenciaRepository;
	
	public Evidencia createEvidencia(Evidencia evidencia) {
        return evidenciaRepository.save(evidencia);
    }

    public List<Evidencia> getAllEvidencias() {
        return evidenciaRepository.findAll();
    }

    public Evidencia getEvidenciaById(Long id_evidencia) {
        return evidenciaRepository.findById(id_evidencia).orElse(null);
    }

    public Evidencia updateEvidencia(Long id_evidencia, Evidencia evidencia) {
    	evidencia.setId_evidencia(id_evidencia);
        return evidenciaRepository.save(evidencia);
    }

    public void deleteEvidencia(Long id_evidencia) {
    	evidenciaRepository.deleteById(id_evidencia);
    }

}
