package cl.rac.gesprub.Servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.rac.gesprub.Entidad.Componente;
import cl.rac.gesprub.Repositorio.ComponenteRepository;

@Service
public class ComponenteService {
	
	@Autowired
    private ComponenteRepository componenteRepository;
	
	public Componente createComponente(Componente componente) {
        return componenteRepository.save(componente);
    }

    public List<Componente> getAllComponentes() {
        return componenteRepository.findAll();
    }

    public Componente getComponenteById(Long id_componente) {
        return componenteRepository.findById(id_componente).orElse(null);
    }

    public Componente updateComponente(Long id_componente, Componente componente) {
    	componente.setId_componente(id_componente);
        return componenteRepository.save(componente);
    }

    public void deleteComponente(Long id_componente) {
    	componenteRepository.deleteById(id_componente);
    }


}
