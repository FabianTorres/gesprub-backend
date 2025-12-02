package cl.rac.gesprub.Servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.rac.gesprub.Entidad.Jira;
import cl.rac.gesprub.Repositorio.JiraRepository;
import cl.rac.gesprub.dto.JiraTicketDTO;

@Service
public class JiraService {

	@Autowired
    private JiraRepository jiraRepository;
	
	public Jira createJira(Jira jira) {
        return jiraRepository.save(jira);
    }

    public List<Jira> getAllJiras() {
        return jiraRepository.findAll();
    }

    public Jira getJiraById(Long id_jira) {
        return jiraRepository.findById(id_jira).orElse(null);
    }

    public Jira updateJira(Long id_jira, Jira jira) {
    	jira.setId_jira(id_jira);
        return jiraRepository.save(jira);
    }

    public void deleteJira(Long id_jira) {
    	jiraRepository.deleteById(id_jira);
    }

    /**
     * Sincroniza una lista de tickets provenientes de un agente externo.
     * Realiza una operación UPSERT (Actualizar o Insertar) usando clave_jira como identificador único.
     * @param tickets Lista de DTOs con la información de los tickets Jira.
     */
    @Transactional
    public void sincronizarTickets(List<JiraTicketDTO> tickets) {
        for (JiraTicketDTO dto : tickets) {
            // 1. Validación de clave: No se procesa si no tiene clave de Jira.
            if (dto.getClave_jira() == null || dto.getClave_jira().isBlank()) {
                continue; 
            }

            // 2. Búsqueda por clave (UPSERT)
            Optional<Jira> existenteOpt = jiraRepository.findByClaveJira(dto.getClave_jira());

            Jira jira;
            if (existenteOpt.isPresent()) {
                // Existe: Se actualiza el registro
                jira = existenteOpt.get();
            } else {
                // No existe: Se crea un nuevo registro
                jira = new Jira();
                jira.setClaveJira(dto.getClave_jira());
            }

            // 3. Mapeo de campos
            jira.setDescripcion(dto.getDescripcion());
            jira.setEstado(dto.getEstado());
            jira.setResponsable(dto.getResponsable());
            jira.setCriticidad(dto.getCriticidad());
            jira.setFecha_creacion(dto.getFecha_creacion());
            jira.setFecha_finalizacion(dto.getFecha_finalizacion());
            
            // Mapeo de campos nuevos
            jira.setCreador(dto.getCreador()); 
            jira.setTipo(dto.getTipo());

            // 4. Guardar
            jiraRepository.save(jira);
        }
    }
}