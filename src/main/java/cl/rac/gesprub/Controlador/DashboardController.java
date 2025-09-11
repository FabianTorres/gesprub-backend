package cl.rac.gesprub.Controlador;

import cl.rac.gesprub.Servicio.DashboardService;
import cl.rac.gesprub.dto.dashboard.DashboardDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
}