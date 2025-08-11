package cl.rac.gesprub.Servicio;

import cl.rac.gesprub.Entidad.ArchivoEvidencia;
import cl.rac.gesprub.Entidad.Evidencia;
import cl.rac.gesprub.Repositorio.ArchivoEvidenciaRepository;
import cl.rac.gesprub.Repositorio.EvidenciaRepository;
import cl.rac.gesprub.dto.ArchivoEvidenciaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArchivoEvidenciaService {

    @Autowired
    private ArchivoEvidenciaRepository archivoEvidenciaRepository;

    @Autowired
    private EvidenciaRepository evidenciaRepository;

    public ArchivoEvidencia create(Long idEvidencia, ArchivoEvidencia archivoEvidencia) {
        Evidencia evidencia = evidenciaRepository.findById(idEvidencia)
                .orElseThrow(() -> new RuntimeException("Evidencia no encontrada con id: " + idEvidencia));

        archivoEvidencia.setEvidencia(evidencia);
        return archivoEvidenciaRepository.save(archivoEvidencia);
    }

    public List<ArchivoEvidenciaDTO> getArchivosPorEvidencia(Long idEvidencia) {
        List<ArchivoEvidencia> archivos = archivoEvidenciaRepository.findByEvidenciaId(idEvidencia);
        return archivos.stream()
                .map(ArchivoEvidenciaDTO::new)
                .collect(Collectors.toList());
    }
}