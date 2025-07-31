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
import cl.rac.gesprub.dto.EvidenciaItemDTO;
import cl.rac.gesprub.dto.HistorialDTO;

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
    
    
    //Se obtiene las evidencias de un caso por su id
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
    
    public HistorialDTO getHistorialPorCaso(int idCaso) {
        // 1. Buscamos la entidad Caso. Si no existe, lanzamos un error.
        Caso caso = casoRepository.findById((long) idCaso)
                .orElseThrow(() -> new RuntimeException("Caso no encontrado con id: " + idCaso));

        // 2. Buscamos todas las evidencias asociadas a ese caso.
        // Spring Data JPA crea este método automáticamente a partir del nombre.
        List<Evidencia> evidencias = evidenciaRepository.findByIdCasoOrderByFechaEvidenciaDesc(idCaso);

        // 3. Transformamos la lista de Entidades a una lista de DTOs.
        List<EvidenciaItemDTO> historialItems = evidencias.stream()
                .map(evidencia -> {
                    EvidenciaItemDTO item = new EvidenciaItemDTO();
                    item.setId_evidencia(evidencia.getId_evidencia());
                    item.setDescripcion_evidencia(evidencia.getDescripcion_evidencia());
                    item.setEstado_evidencia(evidencia.getEstado_evidencia());
                    item.setFecha_evidencia(evidencia.getFechaEvidencia());
                    item.setCriticidad(evidencia.getCriticidad());
                    item.setUrl_evidencia(evidencia.getUrl_evidencia());
                    item.setId_jira(evidencia.getId_jira());

                    // ¡Aquí está la magia! Obtenemos el nombre del usuario relacionado.
                    if (evidencia.getUsuarioEjecutante() != null) {
                        item.setNombreUsuarioEjecutante(evidencia.getUsuarioEjecutante().getNombreUsuario());
                    }

                    return item;
                })
                .collect(Collectors.toList());

        // 4. Creamos el DTO final de respuesta.
        HistorialDTO resultado = new HistorialDTO();
        resultado.setId_caso(caso.getId_caso());
        resultado.setNombre_caso(caso.getNombre_caso());
        resultado.setDescripcion_caso(caso.getDescripcion_caso());
        resultado.setHistorial(historialItems);

        return resultado;
    }

}
