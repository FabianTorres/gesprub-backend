package cl.rac.gesprub.Servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.rac.gesprub.Entidad.Autenticacion;
import cl.rac.gesprub.Repositorio.AutenticacionRepository;


@Service
public class AutenticacionService {
	
	@Autowired
    private AutenticacionRepository autenticacionRepository;
	
	public Autenticacion createAutenticacion(Autenticacion autenticacion) {
        return autenticacionRepository.save(autenticacion);
    }

    public List<Autenticacion> getAllAutenticaciones() {
        return autenticacionRepository.findAll();
    }

    public Autenticacion getAutenticacionById(Long id_autenticacion) {
        return autenticacionRepository.findById(id_autenticacion).orElse(null);
    }

    public Autenticacion updateAutenticacion(Long id_autenticacion, Autenticacion autenticacion) {
    	autenticacion.setId_autenticacion(id_autenticacion);
        return autenticacionRepository.save(autenticacion);
    }

    public void deleteAutenticacion(Long id_autenticacion) {
    	autenticacionRepository.deleteById(id_autenticacion);
    }

}
