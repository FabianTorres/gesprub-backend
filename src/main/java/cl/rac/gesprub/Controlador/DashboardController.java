package cl.rac.gesprub.Controlador;

import cl.rac.gesprub.Servicio.DashboardService;
import cl.rac.gesprub.dto.dashboard.DashboardDTO;
import cl.rac.gesprub.dto.dashboard.DashboardGeneralDTO;
import cl.rac.gesprub.dto.dashboard.AvanceComponenteDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping
    public DashboardDTO getDashboardData(
            @RequestParam Long proyectoId,
            @RequestParam Optional<Long> usuarioId) {
        return dashboardService.getDashboardData(proyectoId, usuarioId);
    }
    
    /**
     * Devuelve los datos agregados para la visión general de un proyecto.
     */
    @GetMapping("/general")
    public DashboardGeneralDTO getDashboardGeneralData(
            @RequestParam Long proyectoId,
            @RequestParam Optional<Long> componenteId) { // <-- Parámetro opcional añadido
        
        return dashboardService.getDashboardGeneralData(proyectoId, componenteId);
    }
    
    /**
     * Devuelve el reporte de avance de ejecución agrupado por componente.
     */
    @GetMapping("/avance-por-componente")
    public List<AvanceComponenteDTO> getAvancePorComponente(
            @RequestParam Long proyectoId,
            @RequestParam Optional<Integer> hito) {
        
        return dashboardService.getAvancePorComponente(proyectoId, hito);
    }
}