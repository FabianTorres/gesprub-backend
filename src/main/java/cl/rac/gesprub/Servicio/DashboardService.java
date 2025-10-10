package cl.rac.gesprub.Servicio;

import cl.rac.gesprub.Entidad.Caso;
import cl.rac.gesprub.Entidad.Componente;
import cl.rac.gesprub.Entidad.Evidencia;
import cl.rac.gesprub.Entidad.EstadoModificacion;
import cl.rac.gesprub.Repositorio.CasoRepository;
import cl.rac.gesprub.Repositorio.ComponenteRepository;
import cl.rac.gesprub.Repositorio.EvidenciaRepository;
import cl.rac.gesprub.Repositorio.EstadoModificacionRepository;
import cl.rac.gesprub.dto.dashboard.ActividadRecienteDTO;
import cl.rac.gesprub.dto.dashboard.AvanceComponenteDTO;
import cl.rac.gesprub.dto.dashboard.ChartDTO;
import cl.rac.gesprub.dto.dashboard.DashboardDTO;
import cl.rac.gesprub.dto.dashboard.DashboardGeneralDTO;
import cl.rac.gesprub.dto.dashboard.DatasetDTO;
import cl.rac.gesprub.dto.dashboard.DistribucionEstadosDTO;
import cl.rac.gesprub.dto.dashboard.KpiDTO;
import cl.rac.gesprub.dto.dashboard.KpiGeneralDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class DashboardService {

    @Autowired
    private CasoRepository casoRepository;
    @Autowired
    private ComponenteRepository componenteRepository;
    @Autowired
    private EvidenciaRepository evidenciaRepository;
    @Autowired
    private EstadoModificacionRepository estadoModificacionRepository;

    public DashboardDTO getDashboardData(Long proyectoId, Optional<Long> usuarioId) {

        // --- 1. OBTENCIÓN DE DATOS ---
        List<Integer> componenteIds = componenteRepository.findComponenteIdsByProyectoId(proyectoId);
        if (componenteIds.isEmpty()) {
            return createEmptyDashboard(); // Si no hay componentes, devolvemos un dashboard vacío
        }

        List<Caso> casosDelProyecto = casoRepository.findAllByIdComponenteIn(componenteIds);
        List<Integer> casosIdsDelProyecto = casosDelProyecto.stream().map(c -> c.getId_caso().intValue()).collect(Collectors.toList());
        
        List<Evidencia> evidenciasDelProyecto = evidenciaRepository.findByIdCasoIn(casosIdsDelProyecto);
        
        // Aplicar filtro de usuario si se proporciona
        List<Evidencia> evidenciasFiltradas = usuarioId.map(uid -> evidenciasDelProyecto.stream()
                .filter(e -> e.getUsuarioEjecutante() != null && e.getUsuarioEjecutante().getIdUsuario().equals(uid))
                .collect(Collectors.toList()))
                .orElse(evidenciasDelProyecto);

        List<Evidencia> evidenciasActivas = evidenciasFiltradas.stream().filter(e -> e.getActivo() == 1).collect(Collectors.toList());

        // --- 2. CÁLCULOS ---
        DashboardDTO dashboard = new DashboardDTO();
        dashboard.setKpis(calculateKpis(casosDelProyecto, evidenciasActivas, casosIdsDelProyecto));
        dashboard.setEstadoEjecuciones(calculateEstadoEjecuciones(casosDelProyecto, evidenciasActivas));
        dashboard.setActividadSemanal(calculateActividadSemanal(evidenciasActivas));
        dashboard.setCasosPorEstado(calculateCasosPorEstado(casosDelProyecto));

        return dashboard;
    }

    private KpiDTO calculateKpis(List<Caso> casos, List<Evidencia> evidenciasActivas, List<Integer> casosIds) {
        KpiDTO kpis = new KpiDTO();
        kpis.setTotalCasos(casos.size());
        kpis.setTotalEjecuciones(evidenciasActivas.size());

        Set<Integer> casosConEvidenciaActiva = evidenciasActivas.stream().map(Evidencia::getIdCaso).collect(Collectors.toSet());
        long casosSinEjecutar = casos.stream().filter(c -> !casosConEvidenciaActiva.contains(c.getId_caso().intValue())).count();
        kpis.setCasosSinEjecutar(casosSinEjecutar);

        LocalDate hace7Dias = LocalDate.now().minusDays(6);
        long ejecucionesUltimaSemana = evidenciasActivas.stream()
                .filter(e -> e.getFechaEvidencia().toLocalDateTime().toLocalDate().isAfter(hace7Dias.minusDays(1)))
                .count();
        kpis.setPromedioEjecucionesDiarias(Math.round((double) ejecucionesUltimaSemana / 7.0 * 100.0) / 100.0);

        return kpis;
    }

    private ChartDTO calculateEstadoEjecuciones(List<Caso> casos, List<Evidencia> evidenciasActivas) {
        Map<Integer, Evidencia> ultimaEvidenciaPorCaso = evidenciasActivas.stream()
                .collect(Collectors.toMap(
                        Evidencia::getIdCaso,
                        Function.identity(),
                        (e1, e2) -> e1.getFechaEvidencia().after(e2.getFechaEvidencia()) ? e1 : e2
                ));

        long okCount = ultimaEvidenciaPorCaso.values().stream().filter(e -> "OK".equalsIgnoreCase(e.getEstado_evidencia())).count();
        long nkCount = ultimaEvidenciaPorCaso.values().stream().filter(e -> "NK".equalsIgnoreCase(e.getEstado_evidencia())).count();
        long sinEjecutarCount = casos.size() - ultimaEvidenciaPorCaso.size();

        ChartDTO chart = new ChartDTO();
        chart.setLabels(Arrays.asList("OK", "NK", "Sin Ejecutar"));
        chart.setDatasets(Collections.singletonList(
                new DatasetDTO("Estado de Casos", Arrays.asList(okCount, nkCount, sinEjecutarCount))
        ));
        return chart;
    }

    private ChartDTO calculateActividadSemanal(List<Evidencia> evidenciasActivas) {
        LocalDate hoy = LocalDate.now();
        Map<LocalDate, Long> conteoPorDia = evidenciasActivas.stream()
                .map(e -> e.getFechaEvidencia().toLocalDateTime().toLocalDate())
                .filter(d -> !d.isBefore(hoy.minusDays(6)))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM");

        for (int i = 6; i >= 0; i--) {
            LocalDate dia = hoy.minusDays(i);
            labels.add(dia.format(formatter));
            data.add(conteoPorDia.getOrDefault(dia, 0L));
        }

        ChartDTO chart = new ChartDTO();
        chart.setLabels(labels);
        chart.setDatasets(Collections.singletonList(
                new DatasetDTO("Ejecuciones por Día", data)
        ));
        return chart;
    }
    
    private ChartDTO calculateCasosPorEstado(List<Caso> casos) {
        Map<Integer, EstadoModificacion> estadosMap = estadoModificacionRepository.findAll().stream()
                .collect(Collectors.toMap(em -> em.getId_estado_modificacion().intValue(), Function.identity()));
        
        // Creamos una instancia por defecto para los casos cuyo estado no se encuentre
        EstadoModificacion estadoDesconocido = new EstadoModificacion();
        estadoDesconocido.setNombre("Desconocido");
        
        Map<String, Long> conteoPorEstado = casos.stream()
            .map(caso -> estadosMap.getOrDefault(caso.getId_estado_modificacion(), estadoDesconocido).getNombre())
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            
        List<String> labels = new ArrayList<>(conteoPorEstado.keySet());
        List<Long> data = new ArrayList<>(conteoPorEstado.values());

        ChartDTO chart = new ChartDTO();
        chart.setLabels(labels);
        chart.setDatasets(Collections.singletonList(
                new DatasetDTO("Casos por Estado de Modificación", data)
        ));
        return chart;
    }

    private DashboardDTO createEmptyDashboard() {
        // Devuelve un DTO con valores por defecto si no hay datos
        DashboardDTO dashboard = new DashboardDTO();
        KpiDTO kpis = new KpiDTO();
        kpis.setTotalCasos(0);
        kpis.setTotalEjecuciones(0);
        kpis.setCasosSinEjecutar(0);
        kpis.setPromedioEjecucionesDiarias(0.0);
        dashboard.setKpis(kpis);

        ChartDTO emptyChart = new ChartDTO();
        emptyChart.setLabels(Collections.emptyList());
        emptyChart.setDatasets(Collections.singletonList(new DatasetDTO("", Collections.emptyList())));
        
        dashboard.setEstadoEjecuciones(emptyChart);
        dashboard.setActividadSemanal(emptyChart);
        dashboard.setCasosPorEstado(emptyChart);
        
        return dashboard;
    }
    
    /**
     * Calcula y ensambla todos los datos para el dashboard general del proyecto.
     */
    public DashboardGeneralDTO getDashboardGeneralData(Long proyectoId, Optional<Long> componenteId) {
        DashboardGeneralDTO dashboard = new DashboardGeneralDTO();

        // --- 1. DETERMINAR ALCANCE (PROYECTO O COMPONENTE) ---
        List<Integer> idsDeComponentesAFiltrar;

        if (componenteId.isPresent()) {
            // Si se provee un componenteId, el alcance es solo ese componente.
            idsDeComponentesAFiltrar = Collections.singletonList(componenteId.get().intValue());
        } else {
            // Si no, el alcance es todos los componentes del proyecto (comportamiento anterior).
            idsDeComponentesAFiltrar = componenteRepository.findComponenteIdsByProyectoId(proyectoId);
        }

        if (idsDeComponentesAFiltrar.isEmpty()) {
            return createEmptyGeneralDashboard();
        }

        // --- El resto de la lógica es EXACTAMENTE LA MISMA, pero usando la lista de IDs determinada ---
        
        List<Caso> casosActivos = casoRepository.findByActivoAndIdComponenteIn(1, idsDeComponentesAFiltrar);
        List<Integer> idsCasosActivos = casosActivos.stream().map(c -> c.getId_caso().intValue()).collect(Collectors.toList());
        
        List<Evidencia> todasLasEvidencias = evidenciaRepository.findByIdCasoIn(idsCasosActivos);
        Map<Integer, List<Evidencia>> evidenciasPorCaso = todasLasEvidencias.stream().collect(Collectors.groupingBy(Evidencia::getIdCaso));

        // --- 2. Calcular KPIs ---
        KpiGeneralDTO kpis = new KpiGeneralDTO();
        long totalCasos = casosActivos.size();
        long casosEjecutados = evidenciasPorCaso.keySet().size();
        
        kpis.setTotalCasos(totalCasos);
        kpis.setCasosEjecutados(casosEjecutados);
        kpis.setCasosPendientes(totalCasos - casosEjecutados);
        if (totalCasos > 0) {
            double avance = ((double) casosEjecutados / totalCasos) * 100;
            kpis.setPorcentajeAvance(Math.round(avance * 100.0) / 100.0);
        } else {
            kpis.setPorcentajeAvance(0.0);
        }
        dashboard.setKpis(kpis);

        // --- 3. Calcular Distribución de Estados ---
        DistribucionEstadosDTO distribucion = new DistribucionEstadosDTO();
        Map<Integer, Evidencia> ultimaEvidenciaPorCaso = evidenciasPorCaso.values().stream()
            .flatMap(List::stream)
            .collect(Collectors.toMap(
                Evidencia::getIdCaso,
                Function.identity(),
                (e1, e2) -> e1.getFechaEvidencia().after(e2.getFechaEvidencia()) ? e1 : e2
            ));

        distribucion.setOk(ultimaEvidenciaPorCaso.values().stream().filter(e -> "OK".equalsIgnoreCase(e.getEstado_evidencia())).count());
        distribucion.setNk(ultimaEvidenciaPorCaso.values().stream().filter(e -> "NK".equalsIgnoreCase(e.getEstado_evidencia())).count());
        distribucion.setNa(ultimaEvidenciaPorCaso.values().stream().filter(e -> "N/A".equalsIgnoreCase(e.getEstado_evidencia())).count());
        dashboard.setDistribucionEstados(distribucion);

        // --- 4. Obtener Actividad Reciente ---
        List<Evidencia> actividadRecienteEntidades = evidenciaRepository.findTop5ByIdCasoInOrderByFechaEvidenciaDesc(idsCasosActivos);
        
        Map<Long, String> mapaNombresCasos = casosActivos.stream()
            .collect(Collectors.toMap(Caso::getId_caso, Caso::getNombre_caso));

        List<ActividadRecienteDTO> actividadRecienteDTOs = actividadRecienteEntidades.stream()
            .map(evidencia -> {
                ActividadRecienteDTO dto = new ActividadRecienteDTO();
                dto.setIdCaso((long) evidencia.getIdCaso());
                dto.setNombreCaso(mapaNombresCasos.get((long) evidencia.getIdCaso()));
                dto.setEstado(evidencia.getEstado_evidencia());
                if (evidencia.getUsuarioEjecutante() != null) {
                    dto.setNombreTester(evidencia.getUsuarioEjecutante().getNombreUsuario());
                }
                if (evidencia.getFechaEvidencia() != null) {
                    dto.setFechaEjecucion(evidencia.getFechaEvidencia().toInstant());
                }
                return dto;
            })
            .collect(Collectors.toList());
        dashboard.setActividadReciente(actividadRecienteDTOs);

        return dashboard;
    }
    
    // Método de ayuda para devolver un objeto vacío si no hay datos
    private DashboardGeneralDTO createEmptyGeneralDashboard() {
        DashboardGeneralDTO dashboard = new DashboardGeneralDTO();
        dashboard.setKpis(new KpiGeneralDTO());
        dashboard.setDistribucionEstados(new DistribucionEstadosDTO());
        dashboard.setActividadReciente(new ArrayList<>());
        return dashboard;
    }
    
    /**
     * NUEVO MÉTODO
     * Calcula el avance de ejecución de casos, agrupado por cada componente de un proyecto.
     */
    public List<AvanceComponenteDTO> getAvancePorComponente(Long proyectoId, Optional<Integer> hito) {
        
        // 1. Obtener los componentes a procesar (filtrando por hito si es necesario)
        List<Componente> componentesDelProyecto;
        if (hito.isPresent()) {
            componentesDelProyecto = componenteRepository.findByProyectoIdAndHito(proyectoId, hito.get());
        } else {
            componentesDelProyecto = componenteRepository.findByProyectoId(proyectoId);
        }

        if (componentesDelProyecto.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. Obtener TODOS los datos necesarios en pocas consultas para ser eficientes
        List<Integer> idsComponentes = componentesDelProyecto.stream()
            .map(c -> c.getId_componente().intValue())
            .collect(Collectors.toList());
        
        List<Caso> todosLosCasos = casoRepository.findAllByIdComponenteIn(idsComponentes);
        List<Integer> idsCasos = todosLosCasos.stream().map(c -> c.getId_caso().intValue()).collect(Collectors.toList());
        List<Evidencia> todasLasEvidencias = evidenciaRepository.findByIdCasoIn(idsCasos);

        // 3. Pre-procesar los datos en mapas para un acceso rápido
        Map<Integer, List<Caso>> casosPorComponente = todosLosCasos.stream()
            .collect(Collectors.groupingBy(Caso::getIdComponente));
        
        Map<Integer, Evidencia> ultimaEvidenciaPorCaso = todasLasEvidencias.stream()
            .collect(Collectors.toMap(
                Evidencia::getIdCaso,
                Function.identity(),
                (e1, e2) -> e1.getFechaEvidencia().after(e2.getFechaEvidencia()) ? e1 : e2
            ));

        // 4. Iterar sobre cada componente y calcular sus métricas
        List<AvanceComponenteDTO> resultado = new ArrayList<>();
        for (Componente componente : componentesDelProyecto) {
            AvanceComponenteDTO dto = new AvanceComponenteDTO();
            dto.setIdComponente(componente.getId_componente());
            dto.setNombreComponente(componente.getNombre_componente());

            List<Caso> casosDelComponente = casosPorComponente.getOrDefault(componente.getId_componente().intValue(), new ArrayList<>());
            dto.setTotalCasos(casosDelComponente.size());

            long casosOk = 0;
            long casosNk = 0;
            long casosSinEjecutar = 0;

            for (Caso caso : casosDelComponente) {
                Evidencia ultimaEvidencia = ultimaEvidenciaPorCaso.get(caso.getId_caso().intValue());
                if (ultimaEvidencia == null) {
                    casosSinEjecutar++;
                } else if ("OK".equalsIgnoreCase(ultimaEvidencia.getEstado_evidencia())) {
                    casosOk++;
                } else if ("NK".equalsIgnoreCase(ultimaEvidencia.getEstado_evidencia())) {
                    casosNk++;
                }
            }

            dto.setCasosOk(casosOk);
            dto.setCasosNk(casosNk);
            dto.setCasosSinEjecutar(casosSinEjecutar);
            resultado.add(dto);
        }

        return resultado;
    }
}