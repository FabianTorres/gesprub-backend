package cl.rac.gesprub.Servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.rac.gesprub.Entidad.Evidencia;
import cl.rac.gesprub.Repositorio.EvidenciaRepository;
import cl.rac.gesprub.Repositorio.CasoRepository;


@Service
public class EvidenciaService {
	
	@Autowired
    private EvidenciaRepository evidenciaRepository;
	
	@Autowired
    private CasoRepository casoRepository;
	
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
    
    @Transactional
    public Evidencia moverEvidencia(Long idEvidencia, int nuevoIdCaso) {
        // 1. Validar que el caso de destino exista.
        casoRepository.findById((long) nuevoIdCaso)
                .orElseThrow(() -> new RuntimeException("El caso de destino con id " + nuevoIdCaso + " no fue encontrado."));

        // 2. Buscar la evidencia que queremos mover.
        Evidencia evidencia = evidenciaRepository.findById(idEvidencia)
                .orElseThrow(() -> new RuntimeException("Evidencia no encontrada con id: " + idEvidencia));
        
        // 3. Actualizar el id_caso.
        evidencia.setIdCaso(nuevoIdCaso);
        
        // 4. Guardar y devolver la evidencia actualizada.
        return evidenciaRepository.save(evidencia);
    }

}
