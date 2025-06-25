package cl.rac.gesprub.Controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.rac.gesprub.Entidad.Jira;
import cl.rac.gesprub.Servicio.JiraService;


@RestController
@RequestMapping("/api/jira")
public class JiraController {
	
	@Autowired
    private JiraService jiraService;
	
	@PostMapping
    public Jira createJira(@RequestBody Jira jira) {
        return jiraService.createJira(jira);
    }
	
	@GetMapping
    public List<Jira> getAllJiras() {
        return jiraService.getAllJiras();
    }
	
	@GetMapping("/{id}")
    public Jira getJiraById(@PathVariable Long id) {
        return jiraService.getJiraById(id);
    }
	
	@PutMapping("/{id}")
    public Jira updateJira(@PathVariable Long id, @RequestBody Jira jira) {
        return jiraService.updateJira(id, jira);
    }
	
	@DeleteMapping("/{id}")
    public void deleteJira(@PathVariable Long id) {
		jiraService.deleteJira(id);
    }

}
