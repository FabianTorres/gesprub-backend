package cl.rac.gesprub.Servicio;

import cl.rac.gesprub.Entidad.Caso;
import cl.rac.gesprub.Entidad.Ciclo;
import cl.rac.gesprub.Entidad.CicloCaso;
import cl.rac.gesprub.Repositorio.CicloCasoRepository;
import cl.rac.gesprub.Repositorio.CicloRepository;
import cl.rac.gesprub.Repositorio.ComponenteRepository;
import cl.rac.gesprub.Repositorio.EvidenciaRepository;
import cl.rac.gesprub.dto.AsignacionCasosRequestDTO;
import cl.rac.gesprub.Entidad.EstadoModificacion; 
import cl.rac.gesprub.Repositorio.EstadoModificacionRepository;
import cl.rac.gesprub.dto.CerrarCicloRequestDTO;
import cl.rac.gesprub.dto.CicloDTO;
import cl.rac.gesprub.dto.CicloRequestDTO;
import cl.rac.gesprub.dto.CicloResumenDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import cl.rac.gesprub.dto.ReporteCicloDetalleDTO;
import cl.rac.gesprub.Entidad.Evidencia;
import cl.rac.gesprub.Entidad.Componente;
import java.util.Map;
import java.util.function.Function;
import java.util.Collections;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CicloService {

    private final CicloRepository cicloRepository;
    private final CicloCasoRepository cicloCasoRepository;
    private final EvidenciaRepository evidenciaRepository;
    private final ComponenteRepository componenteRepository;
    private final EstadoModificacionRepository estadoModificacionRepository;

    @Transactional
    public Ciclo createCiclo(CicloRequestDTO dto) {
        Ciclo ciclo = new Ciclo();
        
        ciclo.setIdProyecto(dto.getIdProyecto());
        
        ciclo.setJiraKey(dto.getJiraKey());
        ciclo.setNombre(dto.getNombre());
        ciclo.setDescripcion(dto.getDescripcion());
        ciclo.setFechaLiberacion(dto.getFechaLiberacion());
        ciclo.setIdUsuarioCreador(dto.getIdUsuarioCreador());
        
        // Campos de auditoría (Spring ya tiene @CreationTimestamp, pero lo forzamos aquí)
        ciclo.setFechaCreacion(LocalDateTime.now());
        ciclo.setActivo(1);
        
        return cicloRepository.save(ciclo);
    }
    
    /**
     * Obtiene los ciclos por proyecto y estado y calcula los KPI
     */
    public List<CicloDTO> getCiclos(Long idProyecto, String estado) {
        List<Ciclo> ciclos;
        
        if (estado == null) estado = "activos";
        
        switch (estado.toLowerCase()) {
            case "cerrados":
                // Históricos del proyecto
                ciclos = cicloRepository.findByIdProyectoAndActivo(idProyecto, 0);
                break;
            case "todos":
                // Todos los del proyecto
                ciclos = cicloRepository.findByIdProyecto(idProyecto);
                break;
            case "activos":
            default:
                // Activos del proyecto
                ciclos = cicloRepository.findByIdProyectoAndActivo(idProyecto, 1);
                break;
        }

        return ciclos.stream()
                .map(ciclo -> {
                    CicloDTO dto = new CicloDTO(ciclo);
                    calcularKpisCiclo(dto); // Mantenemos el cálculo de KPIs existente
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Método privado que orquesta el cálculo de métricas para un ciclo.
     */
    private void calcularKpisCiclo(CicloDTO dto) {
        Integer idCiclo = dto.getIdCiclo();

        // A. Total Asignados (Alcance)
        long totalAsignados = cicloCasoRepository.countByIdCiclo(idCiclo);
        dto.setTotalCasosAsignados((int) totalAsignados);

        // B. Distribución de Estados (Ejecución)
        // Obtenemos la lista cruda: [["OK", 5], ["NK", 2]]
        List<Object[]> resultados = evidenciaRepository.countEstadosUltimaEvidenciaPorCiclo(idCiclo);
        
        int certificados = 0;
        int errores = 0;

        for (Object[] fila : resultados) {
            String estado = (String) fila[0];
            Long cantidad = ((Number) fila[1]).longValue(); // Casteo seguro

            if (estado != null) {
                if ("OK".equalsIgnoreCase(estado)) {
                    certificados += cantidad.intValue();
                } else if ("NK".equalsIgnoreCase(estado)) {
                    errores += cantidad.intValue();
                }
                // Otros estados se ignoran en estos contadores específicos
            }
        }

        dto.setCasosCertificados(certificados);
        dto.setCasosError(errores);

        // C. Casos Sin Ejecutar (Fórmula solicitada)
        // Total - (OK + NK). Cualquier otro estado o la ausencia de ejecución cae aquí.
        int sinEjecutar = (int) totalAsignados - (certificados + errores);
        // Aseguramos que no sea negativo (por si acaso hubiera inconsistencias de datos antiguos)
        dto.setCasosSinEjecutar(Math.max(0, sinEjecutar));
    }
    
    @Transactional
    public Ciclo cerrarCiclo(Integer idCiclo, CerrarCicloRequestDTO dto) {
        Ciclo ciclo = cicloRepository.findById(idCiclo)
                .orElseThrow(() -> new RuntimeException("Ciclo no encontrado con ID: " + idCiclo));
        
        if (ciclo.getActivo() == 0) {
            throw new IllegalArgumentException("El ciclo con ID " + idCiclo + " ya se encuentra cerrado.");
        }
        
        ciclo.setActivo(0);
        ciclo.setFechaCierre(LocalDateTime.now());
        ciclo.setIdUsuarioCierre(dto.getIdUsuarioCierre());
        
        return cicloRepository.save(ciclo);
    }
    
    @Transactional
    public List<Long> asignarCasos(Integer idCiclo, AsignacionCasosRequestDTO dto) {
        // 1. Verificamos que el ciclo exista (lanzará RuntimeException si no existe)
        Ciclo ciclo = cicloRepository.findById(idCiclo)
                .orElseThrow(() -> new RuntimeException("Ciclo no encontrado con ID: " + idCiclo));
        
        // 2. Opcional: Podríamos verificar si el ciclo está activo, pero la lógica de negocio podría permitir reasignación.
        
        // 3. Eliminamos el alcance anterior (para hacer una sustitución completa)
        cicloCasoRepository.deleteByIdCiclo(idCiclo);
        
        // 4. Creamos las nuevas relaciones de alcance
        List<CicloCaso> nuevasRelaciones = dto.getIdsCasos().stream()
                .map(idCaso -> {
                    CicloCaso cc = new CicloCaso();
                    cc.setIdCiclo(idCiclo);
                    cc.setIdCaso(idCaso);
                    return cc;
                })
                .collect(Collectors.toList());
        
        cicloCasoRepository.saveAll(nuevasRelaciones);
        
        // Devolvemos los IDs asignados
        return dto.getIdsCasos();
    }
    
    public List<Long> getAlcanceCiclo(Integer idCiclo) {
        // Traemos solo los IDs de casos para el alcance
        return cicloCasoRepository.findIdCasosByIdCiclo(idCiclo);
    }
    
    @Transactional
    public Ciclo updateCiclo(Integer idCiclo, CicloRequestDTO dto) {
        // 1. Buscar el ciclo existente
        Ciclo ciclo = cicloRepository.findById(idCiclo)
                .orElseThrow(() -> new RuntimeException("Ciclo no encontrado con ID: " + idCiclo));

        // 2. Actualizar campos descriptivos
        ciclo.setJiraKey(dto.getJiraKey());
        ciclo.setNombre(dto.getNombre());
        ciclo.setDescripcion(dto.getDescripcion());
        ciclo.setFechaLiberacion(dto.getFechaLiberacion());

        // Nota: No actualizamos idUsuarioCreador ni fechaCreacion para mantener la auditoría original.
        // Si quisieras registrar quién modificó, necesitarías un campo 'idUsuarioModificacion' en la BD.

        // 3. Guardar cambios
        return cicloRepository.save(ciclo);
    }
    
    public List<CicloResumenDTO> getCiclosActivosPorCaso(Long idCaso) {
        // Obtenemos las entidades Ciclo directamente desde la consulta optimizada
        List<Ciclo> ciclosActivos = cicloCasoRepository.findCiclosActivosByCaso(idCaso);
        
        // Convertimos a DTOs resumen
        return ciclosActivos.stream()
                .map(CicloResumenDTO::new)
                .collect(Collectors.toList());
    }
    
    public CicloDTO getCicloById(Integer idCiclo) {
        Ciclo ciclo = cicloRepository.findById(idCiclo)
                .orElseThrow(() -> new RuntimeException("Ciclo no encontrado con ID: " + idCiclo));
        
        return new CicloDTO(ciclo);
    }
    
    public List<ReporteCicloDetalleDTO> getReporteDetallado(Integer idCiclo) {
        // 1. Obtener el alcance (asignaciones)
        List<CicloCaso> asignaciones = cicloCasoRepository.findByIdCiclo(idCiclo);
        
        if (asignaciones.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. Obtener mapa de nombres de componentes (Optimización)
        // Extraemos IDs de componentes de los casos
        List<Long> idsComponentes = asignaciones.stream()
                .map(cc -> (long) cc.getCaso().getIdComponente())
                .distinct()
                .collect(Collectors.toList());
        
        Map<Long, String> mapaComponentes = componenteRepository.findAllById(idsComponentes).stream()
                .collect(Collectors.toMap(Componente::getId_componente, Componente::getNombre_componente));
        
        // Traemos todos los estados (son pocos) y los mapeamos ID -> Nombre
        Map<Integer, String> mapaEstadosModificacion = estadoModificacionRepository.findAll().stream()
                .collect(Collectors.toMap(
                        em -> em.getId_estado_modificacion().intValue(), // Key: ID (int)
                        EstadoModificacion::getNombre                    // Value: Nombre (String)
                ));

        // 3. Obtener TODAS las evidencias de este ciclo
        List<Evidencia> evidenciasDelCiclo = evidenciaRepository.findByIdCiclo(idCiclo);

        // 4. Agrupar evidencias por Caso y quedarse con la ÚLTIMA (Max Fecha)
        Map<Integer, Evidencia> ultimaEvidenciaPorCaso = evidenciasDelCiclo.stream()
                .collect(Collectors.toMap(
                        Evidencia::getIdCaso,       // Key: ID Caso
                        Function.identity(),        // Value: La evidencia
                        (e1, e2) -> e1.getFechaEvidencia().after(e2.getFechaEvidencia()) ? e1 : e2 // Merge: Ganador por fecha
                ));

        // 5. Construir el reporte iterando sobre el alcance
        return asignaciones.stream().map(asignacion -> {
            ReporteCicloDetalleDTO dto = new ReporteCicloDetalleDTO();
            Caso caso = asignacion.getCaso();

            // Datos del Caso y Componente
            dto.setIdCaso(caso.getId_caso());
            dto.setNombreCaso(caso.getNombre_caso());
            dto.setVersionCaso(caso.getVersion());
            dto.setNombreComponente(mapaComponentes.getOrDefault((long) caso.getIdComponente(), "Desconocido"));
            // Obtenemos el nombre del estado usando el ID guardado en el caso
            String nombreActualizacion = mapaEstadosModificacion.getOrDefault(
                    caso.getId_estado_modificacion(), 
                    "Desconocido"
            );
            dto.setActualizacion(nombreActualizacion);

            // Datos de la Ejecución (Si existe)
            Evidencia evidencia = ultimaEvidenciaPorCaso.get(caso.getId_caso().intValue());
            
            if (evidencia != null) {
                dto.setEstadoEjecucion(evidencia.getEstado_evidencia());
                dto.setFechaEjecucion(evidencia.getFechaEvidencia());
                dto.setObservacion(evidencia.getDescripcion_evidencia());
                
             // Mapeamos el id_jira numérico. Si es > 0 lo asignamos, si no, null.
                if (evidencia.getId_jira() > 0) {
                    dto.setJiraDefecto(evidencia.getId_jira());
                } else {
                    dto.setJiraDefecto(null);
                }

                if (evidencia.getUsuarioEjecutante() != null) {
                    dto.setTester(evidencia.getUsuarioEjecutante().getNombreUsuario());
                }
            } else {
                // Si no hay ejecución en este ciclo, enviamos nulls explícitos (o valores por defecto)
                dto.setEstadoEjecucion(null);
                dto.setJiraDefecto(null);
            }

            return dto;
        }).collect(Collectors.toList());
    }

    // Pendiente: Métodos para calcular KPIs (casosCertificados, casosError, etc.)
}