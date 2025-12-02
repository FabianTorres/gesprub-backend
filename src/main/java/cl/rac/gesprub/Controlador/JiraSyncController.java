package cl.rac.gesprub.Controlador;

import cl.rac.gesprub.Servicio.JiraService;
import cl.rac.gesprub.dto.JiraTicketDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/integracion/jira")
public class JiraSyncController {

    @Autowired
    private JiraService jiraService;

    /** * Clave pre-compartida para la autenticación del agente local.
     * Se inyecta desde application.properties (o variables de entorno).
     */
    @Value("${app.integration.jira.api-key:UNEXPECTED_ERROR_KEY}")
    private String apiKeyProperty;

    /**
     * Endpoint utilizado por el Agente Local para sincronizar tickets de Jira.
     * Implementa una autenticación simple basada en API Key.
     * ESTE ENDPOINT ES PÚBLICO EN SECURITYCONFIG PERO PROTEGIDO POR LA API KEY.
     * @param tickets Lista de tickets Jira a sincronizar (UPSERT).
     * @param apiKey  La clave de seguridad enviada en la cabecera "X-API-KEY".
     * @return Respuesta HTTP 200 OK si la sincronización es exitosa, o 401/500 en caso de error.
     */
    @PostMapping("/sync")
    public ResponseEntity<String> sincronizar(
            @RequestBody List<JiraTicketDTO> tickets,
            @RequestHeader(value = "X-API-KEY", required = false) String apiKey) {

        // 1. Lógica de Seguridad (Validación manual de API Key)
        if (apiKey == null || !apiKey.equals(apiKeyProperty)) {
            // Usamos UNATHORIZED (401) para indicar que la clave de acceso es incorrecta.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("API Key inválida o ausente.");
        }
        
        // 2. Lógica de Negocio (Delegada al servicio)
        try {
            jiraService.sincronizarTickets(tickets);
            return ResponseEntity.ok("Sincronización completada. Tickets procesados: " + tickets.size());
        } catch (Exception e) {
            // Se debe registrar este error crítico en el log
            e.printStackTrace(); // Reemplazar con log.error() una vez que se integre Slf4j
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar tickets: " + e.getMessage());
        }
    }
}