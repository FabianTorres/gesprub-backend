package cl.rac.gesprub.Servicio;

import cl.rac.gesprub.Entidad.Caso;
import cl.rac.gesprub.Entidad.Evidencia;
import cl.rac.gesprub.Entidad.EstadoModificacion;
import cl.rac.gesprub.Repositorio.CasoRepository;
import cl.rac.gesprub.Repositorio.ComponenteRepository;
import cl.rac.gesprub.Repositorio.EvidenciaRepository;
import cl.rac.gesprub.Repositorio.EstadoModificacionRepository;
import cl.rac.gesprub.dto.dashboard.ChartDTO;
import cl.rac.gesprub.dto.dashboard.DashboardDTO;
import cl.rac.gesprub.dto.dashboard.DatasetDTO;
import cl.rac.gesprub.dto.dashboard.KpiDTO;
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
}