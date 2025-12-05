package cl.rac.gesprub.Servicio;

import cl.rac.gesprub.Entidad.Ciclo;
import cl.rac.gesprub.Entidad.CicloCaso;
import cl.rac.gesprub.Repositorio.CicloCasoRepository;
import cl.rac.gesprub.Repositorio.CicloRepository;
import cl.rac.gesprub.Repositorio.EvidenciaRepository;
import cl.rac.gesprub.dto.AsignacionCasosRequestDTO;
import cl.rac.gesprub.dto.CerrarCicloRequestDTO;
import cl.rac.gesprub.dto.CicloDTO;
import cl.rac.gesprub.dto.CicloRequestDTO;
import cl.rac.gesprub.dto.CicloResumenDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CicloService {

    private final CicloRepository cicloRepository;
    private final CicloCasoRepository cicloCasoRepository;
    private final EvidenciaRepository evidenciaRepository;

    @Transactional
    public Ciclo createCiclo(CicloRequestDTO dto) {
        Ciclo ciclo = new Ciclo();
        
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
     * Obtiene los ciclos y CALCULA LOS KPIs para cada uno.
     */
    public List<CicloDTO> getCiclos(String estado) {
        List<Ciclo> ciclos;
        
        // 1. Filtrado (Lógica existente)
        if (estado == null) estado = "activos";
        switch (estado.toLowerCase()) {
            case "cerrados": ciclos = cicloRepository.findByActivo(0); break;
            case "todos": ciclos = cicloRepository.findAll(); break;
            case "activos":
            default: ciclos = cicloRepository.findByActivo(1); break;
        }

        // 2. Transformación y Cálculo de KPIs
        return ciclos.stream()
                .map(ciclo -> {
                    // Creamos el DTO base
                    CicloDTO dto = new CicloDTO(ciclo);
                    // Invocamos el método privado para rellenar los contadores
                    calcularKpisCiclo(dto);
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

    // Pendiente: Métodos para calcular KPIs (casosCertificados, casosError, etc.)
}