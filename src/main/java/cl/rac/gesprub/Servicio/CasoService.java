package cl.rac.gesprub.Servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.rac.gesprub.Entidad.Caso;
import cl.rac.gesprub.Repositorio.CasoRepository;

@Service
public class CasoService {
	
	@Autowired
    private CasoRepository casoRepository;
	
	public Caso createCaso(Caso caso) {
        return casoRepository.save(caso);
    }

    public List<Caso> getAllCasos() {
        return casoRepository.findAll();
    }

    public Caso getCasoById(Long id_caso) {
        return casoRepository.findById(id_caso).orElse(null);
    }

    public Caso updateCaso(Long id_caso, Caso caso) {
        caso.setId_caso(id_caso);
        return casoRepository.save(caso);
    }

    public void deleteCaso(Long id_caso) {
        casoRepository.deleteById(id_caso);
    }

}
