package cl.rac.gesprub.Controlador;

import cl.rac.gesprub.Entidad.Ciclo;
import cl.rac.gesprub.Servicio.CicloService;
import cl.rac.gesprub.dto.AsignacionCasosRequestDTO;
import cl.rac.gesprub.dto.CerrarCicloRequestDTO;
import cl.rac.gesprub.dto.CicloDTO;
import cl.rac.gesprub.dto.CicloRequestDTO;
import cl.rac.gesprub.dto.CicloResumenDTO;
import cl.rac.gesprub.dto.ReporteCicloDetalleDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ciclos")
@RequiredArgsConstructor
public class CicloController {

    private final CicloService cicloService;

    /**
     * Lista los ciclos de prueba según el filtro de estado.
     * Endpoint: GET /api/ciclos?estado={valor}
     */
    @GetMapping
    public ResponseEntity<List<CicloDTO>> getCiclos(
    		@RequestParam Long idProyecto,
            @RequestParam(value = "estado", defaultValue = "activos") String estado) {
        
        List<CicloDTO> ciclos = cicloService.getCiclos(idProyecto, estado);
        return ResponseEntity.ok(ciclos);
    }
    
    /**
     * Crea un nuevo Ciclo de Prueba/Liberación.
     */
    @PostMapping
    public ResponseEntity<CicloDTO> createCiclo(@Valid @RequestBody CicloRequestDTO dto) {
        Ciclo nuevoCiclo = cicloService.createCiclo(dto);
        // Convertimos a DTO antes de responder
        return ResponseEntity.ok(new CicloDTO(nuevoCiclo));
    }
    
    /**
     * Cierra un ciclo, marcándolo como inactivo y registrando el usuario y fecha de cierre.
     */
    @PostMapping("/{idCiclo}/cerrar")
    public ResponseEntity<CicloDTO> cerrarCiclo(
            @PathVariable Integer idCiclo, 
            @Valid @RequestBody CerrarCicloRequestDTO dto) {
        
        Ciclo cicloCerrado = cicloService.cerrarCiclo(idCiclo, dto);
        // Convertimos a DTO antes de responder para evitar el error de Jackson/Hibernate Proxy
        return ResponseEntity.ok(new CicloDTO(cicloCerrado));
    }
    
    /**
     * Define o reemplaza el alcance de casos de prueba para un ciclo.
     */
    @PostMapping("/{idCiclo}/asignar")
    public ResponseEntity<List<Long>> asignarCasosACiclo(
            @PathVariable Integer idCiclo, 
            @Valid @RequestBody AsignacionCasosRequestDTO dto) {
        
        List<Long> idsAsignados = cicloService.asignarCasos(idCiclo, dto);
        return ResponseEntity.ok(idsAsignados);
    }
    
    /**
     * Obtiene los IDs de los casos que forman parte del alcance de un ciclo específico.
     */
    @GetMapping("/{idCiclo}/alcance")
    public ResponseEntity<List<Long>> getAlcanceCiclo(@PathVariable Integer idCiclo) {
        List<Long> idsCasos = cicloService.getAlcanceCiclo(idCiclo);
        return ResponseEntity.ok(idsCasos);
    }
    
    @PutMapping("/{idCiclo}")
    public ResponseEntity<CicloDTO> updateCiclo(
            @PathVariable Integer idCiclo, 
            @Valid @RequestBody CicloRequestDTO dto) {
        
        Ciclo cicloActualizado = cicloService.updateCiclo(idCiclo, dto);
        // Convertimos a DTO antes de responder
        return ResponseEntity.ok(new CicloDTO(cicloActualizado));
    }
    
    /**
     * Devuelve los ciclos activos a los que pertenece un caso específico.
     * Útil para ofrecer contexto de ejecución al usuario.
     */
    @GetMapping("/activos/por-caso/{idCaso}")
    public ResponseEntity<List<CicloResumenDTO>> getCiclosActivosPorCaso(@PathVariable Long idCaso) {
        List<CicloResumenDTO> resumen = cicloService.getCiclosActivosPorCaso(idCaso);
        return ResponseEntity.ok(resumen);
    }
    
    @GetMapping("/{idCiclo}")
    public ResponseEntity<CicloDTO> getCicloById(@PathVariable Integer idCiclo) {
        CicloDTO ciclo = cicloService.getCicloById(idCiclo);
        return ResponseEntity.ok(ciclo);
    }
    
    /**
     * Genera un reporte detallado del estado de ejecución de todos los casos
     * asignados a un ciclo específico.
     */
    @GetMapping("/{idCiclo}/reporte")
    public ResponseEntity<List<ReporteCicloDetalleDTO>> getReporteCiclo(@PathVariable Integer idCiclo) {
        List<ReporteCicloDetalleDTO> reporte = cicloService.getReporteDetallado(idCiclo);
        return ResponseEntity.ok(reporte);
    }
}