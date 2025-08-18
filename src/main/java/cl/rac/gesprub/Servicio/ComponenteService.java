package cl.rac.gesprub.Servicio;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.rac.gesprub.Entidad.Componente;
import cl.rac.gesprub.Repositorio.ComponenteRepository;
import cl.rac.gesprub.dto.ComponenteDTO;

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
    
    public List<ComponenteDTO> getComponentes(Long proyectoId) {
        List<Componente> componentes;
        if (proyectoId != null) {
            componentes = componenteRepository.findByProyectoId(proyectoId);
        } else {
            componentes = componenteRepository.findAll();
        }
        

        // Se convierte la lista de Entidades a una lista de DTOs
        return componentes.stream().map(this::convertirADto)
                .collect(Collectors.toList());
                
    }
    
    private ComponenteDTO convertirADto(Componente componente) {
        ComponenteDTO dto = new ComponenteDTO();
        dto.setId_componente(componente.getId_componente());
        dto.setNombre_componente(componente.getNombre_componente());
        dto.setHito_componente(componente.getHito_componente());
        dto.setFecha_limite(componente.getFecha_limite());
        dto.setActivo(componente.getActivo());
        dto.setId_ambito(componente.getIdAmbito());
        
        if (componente.getProyecto() != null) {
            dto.setId_proyecto(componente.getProyecto().getId_proyecto());
            dto.setNombre_proyecto(componente.getProyecto().getNombre_proyecto());
        }
        
        return dto;
    }
    
    /**
     * Obtiene un componente por su ID y lo devuelve como un DTO seguro.
     */
    public ComponenteDTO getComponenteByIdComoDto(Long id_componente) {
        Componente componente = componenteRepository.findById(id_componente)
                .orElseThrow(() -> new RuntimeException("Componente no encontrado con id: " + id_componente));
        return convertirADto(componente);
    }

    /**
     * Actualiza un componente y devuelve el resultado como un DTO seguro.
     */
    public ComponenteDTO updateComponenteComoDto(Long id_componente, Componente componente) {
        // Aseguramos que el ID sea el correcto
    	componente.setId_componente(id_componente);
        
        // Guardamos la entidad actualizada
        Componente componenteGuardado = componenteRepository.save(componente);
        
        // Devolvemos el resultado convertido a DTO
        return convertirADto(componenteGuardado);
    }


}
