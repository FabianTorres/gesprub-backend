package cl.rac.gesprub.Servicio;

import cl.rac.gesprub.Entidad.ArchivoEvidencia;
import cl.rac.gesprub.Entidad.Caso;
import cl.rac.gesprub.Entidad.Componente;
import cl.rac.gesprub.Entidad.Evidencia;
import cl.rac.gesprub.Repositorio.CasoRepository;
import cl.rac.gesprub.Repositorio.ComponenteRepository;
import cl.rac.gesprub.Repositorio.EvidenciaRepository;
import cl.rac.gesprub.dto.DetallePlanPruebasDTO;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    @Autowired
    private CasoRepository casoRepository;

    @Autowired
    private ComponenteRepository componenteRepository;

    @Autowired
    private EvidenciaRepository evidenciaRepository;
    
    @Transactional
    public List<DetallePlanPruebasDTO> obtenerDetallesPlanPruebas(List<Integer> idsCasos) {
        if (idsCasos == null || idsCasos.isEmpty()) {
            return new ArrayList<>();
        }

        // 1. Obtener los casos solicitados (solo activos)
        List<Long> idsLong = idsCasos.stream().map(Integer::longValue).collect(Collectors.toList());
        List<Caso> casos = casoRepository.findAllById(idsLong).stream()
                .filter(c -> c.getActivo() == 1)
                .collect(Collectors.toList());

        if (casos.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. Obtener nombres de componentes (Optimización: una sola consulta o desde cache)
        List<Long> idsComponentes = casos.stream().map(c -> (long) c.getIdComponente()).distinct().collect(Collectors.toList());
        Map<Long, String> nombresComponentes = componenteRepository.findAllById(idsComponentes).stream()
                .collect(Collectors.toMap(Componente::getId_componente, Componente::getNombre_componente));

        // 3. Obtener la ÚLTIMA evidencia para cada caso (Solo activas)
        // Usamos una consulta personalizada para traer solo las últimas evidencias de estos casos
        List<Integer> idsCasosInt = casos.stream().map(c -> c.getId_caso().intValue()).collect(Collectors.toList());
        List<Evidencia> evidencias = evidenciaRepository.findByIdCasoIn(idsCasosInt); // Asumiendo que este método ya existe y trae todo

        // Agrupar y encontrar la última por fecha
        Map<Integer, Evidencia> ultimaEvidenciaPorCaso = evidencias.stream()
                .filter(e -> e.getActivo() == 1)
                .collect(Collectors.toMap(
                        Evidencia::getIdCaso,
                        Function.identity(),
                        (e1, e2) -> e1.getFechaEvidencia().after(e2.getFechaEvidencia()) ? e1 : e2
                ));

        // 4. Construir la lista de DTOs
        return casos.stream().map(caso -> {
            DetallePlanPruebasDTO dto = new DetallePlanPruebasDTO();
            
            // Datos del Caso
            dto.setId_caso(caso.getId_caso());
            dto.setNombre_componente(nombresComponentes.getOrDefault((long) caso.getIdComponente(), ""));
            dto.setNombre_caso(caso.getNombre_caso());
            dto.setPasos(caso.getPasos());
            dto.setResultado_esperado(caso.getResultado_esperado());
            dto.setVersion_caso(caso.getVersion());

            // Datos de la Evidencia
            Evidencia evidencia = ultimaEvidenciaPorCaso.get(caso.getId_caso().intValue());
            if (evidencia != null) {
                dto.setVersion_evidencia(evidencia.getVersion_ejecucion());
                dto.setRut_evidencia(evidencia.getRut());
                dto.setResultado_evidencia(evidencia.getEstado_evidencia());
                
                
                if (evidencia.getId_jira() > 0) {
                    // Si el valor es > 0, lo consideramos un ID válido
                    dto.setId_jira(evidencia.getId_jira());
                } else {
                    // Si es 0 o menos, se queda como null (valor predeterminado del Integer en el DTO)
                    dto.setId_jira(null);
                }
                
                if (evidencia.getUsuarioEjecutante() != null) {
                    dto.setNombre_analista(evidencia.getUsuarioEjecutante().getNombreUsuario());
                }

                // Concatenar archivos
                if (evidencia.getArchivos() != null && !evidencia.getArchivos().isEmpty()) {
                    String archivosConcat = evidencia.getArchivos().stream()
                            .map(ArchivoEvidencia::getNombre_archivo)
                            .collect(Collectors.joining(", "));
                    dto.setNombres_archivos(archivosConcat);
                }
            } else {
                // Si no hay evidencia, enviamos valores vacíos o nulos según requerimiento
                dto.setResultado_evidencia("N/A"); // O null, según prefieras
                dto.setId_jira(null);
            }

            return dto;
        }).collect(Collectors.toList());
    }
}