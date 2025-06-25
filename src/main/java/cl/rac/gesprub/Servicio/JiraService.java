package cl.rac.gesprub.Servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.rac.gesprub.Entidad.Jira;
import cl.rac.gesprub.Repositorio.JiraRepository;


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
}
