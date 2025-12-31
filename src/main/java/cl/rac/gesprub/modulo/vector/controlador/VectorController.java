package cl.rac.gesprub.modulo.vector.controlador;

import cl.rac.gesprub.modulo.vector.dto.CatVectorDTO;
import cl.rac.gesprub.modulo.vector.dto.VectorDTO;
import cl.rac.gesprub.modulo.vector.dto.VectorLogDTO;
import cl.rac.gesprub.modulo.vector.entidad.CatVectorEntity;
import cl.rac.gesprub.modulo.vector.servicio.VectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/vectores")
@RequiredArgsConstructor
public class VectorController {

    private final VectorService vectorService;

    @GetMapping
    public ResponseEntity<List<VectorDTO>> listar() {
        return ResponseEntity.ok(vectorService.listarTodos());
    }

    @PostMapping
    public ResponseEntity<VectorDTO> guardar(@RequestBody VectorDTO dto) {
        return ResponseEntity.ok(vectorService.guardar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VectorDTO> actualizar(@PathVariable Long id, @RequestBody VectorDTO dto) {
        return ResponseEntity.ok(vectorService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, @RequestParam String usuarioResponsable) {
    	vectorService.eliminar(id, usuarioResponsable);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/descargar-sql")
    public ResponseEntity<InputStreamResource> descargarSql() {
        ByteArrayInputStream stream = vectorService.generarArchivoSql();
        String nombreArchivo = "inserta_vx_2026_pp_rac.sql";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + nombreArchivo);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.TEXT_PLAIN)
                .body(new InputStreamResource(stream));
    }
    
    
    @GetMapping("/descargar-txt")
    public ResponseEntity<InputStreamResource> descargarTxt() {
        ByteArrayInputStream stream = vectorService.generarArchivoTxt();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
        String nombreArchivo = "vectores_bigdata_" + timestamp + ".txt";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + nombreArchivo);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.TEXT_PLAIN)
                .body(new InputStreamResource(stream));
    }
    
    /**
     * Validador preventivo.
     * Uso Frontend: 
     * 1. Usuario llena formulario.
     * 2. Front llama a este endpoint.
     * 3. Si devuelve TRUE -> Front muestra alerta: "Ya existe, ¿Desea continuar?".
     * 4. Si usuario dice SÍ -> Front llama a POST /api/vectores.
     */
    @GetMapping("/verificar-existencia")
    public ResponseEntity<Boolean> verificarExistencia(
            @RequestParam Long rut,
            @RequestParam Integer periodo,
            @RequestParam Integer vector) {
        
        boolean existe = vectorService.existeCombinacion(rut, periodo, vector);
        return ResponseEntity.ok(existe);
    }
    
    /**
     * Obtiene el historial de auditoria (Ultimos 100 registros).
     */
    @GetMapping("/logs")
    public ResponseEntity<List<VectorLogDTO>> listarLogs() {
        return ResponseEntity.ok(vectorService.listarLogs());
    }
    
    // 	Obtener catalogo
    @GetMapping("/catalogo")
    public ResponseEntity<List<CatVectorEntity>> getCatalogo() {
        return ResponseEntity.ok(vectorService.listarCatalogo());
    }
    
    
 // 1. Crear Vector en Catálogo
    @PostMapping("/catalogo")
    public ResponseEntity<?> crearVectorCatalogo(@RequestBody CatVectorDTO dto) {
        try {
            CatVectorDTO nuevo = vectorService.crearVectorCatalogo(dto);
            return ResponseEntity.ok(nuevo);
        } catch (RuntimeException e) {
            // Capturamos el error de duplicado para devolver un Bad Request o Conflict
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    // 2. Actualizar Vector en Catálogo
    @PutMapping("/catalogo/{id}")
    public ResponseEntity<?> actualizarVectorCatalogo(@PathVariable Integer id, @RequestBody CatVectorDTO dto) {
        try {
            CatVectorDTO actualizado = vectorService.actualizarVectorCatalogo(id, dto);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}