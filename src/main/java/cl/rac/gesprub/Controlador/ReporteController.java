package cl.rac.gesprub.Controlador;

import cl.rac.gesprub.Servicio.ReporteService;
import cl.rac.gesprub.dto.DetallePlanPruebasDTO;
import cl.rac.gesprub.dto.PlanPruebasRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @PostMapping("/plan-pruebas/detalles")
    public List<DetallePlanPruebasDTO> obtenerDetallesPlanPruebas(@RequestBody PlanPruebasRequestDTO request) {
        return reporteService.obtenerDetallesPlanPruebas(request.getIds_casos());
    }
}