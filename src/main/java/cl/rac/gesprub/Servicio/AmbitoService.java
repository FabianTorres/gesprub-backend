package cl.rac.gesprub.Servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.rac.gesprub.Entidad.Ambito;
import cl.rac.gesprub.Repositorio.AmbitoRepository;

@Service
public class AmbitoService {
	
	@Autowired
    private AmbitoRepository ambitoRepository;
	
	public Ambito createAmbito(Ambito ambito) {
        return ambitoRepository.save(ambito);
    }

    public List<Ambito> getAllAmbitos() {
        return ambitoRepository.findAll();
    }

    public Ambito getAmbitoById(Long id_ambito) {
        return ambitoRepository.findById(id_ambito).orElse(null);
    }

    public Ambito updateAmbito(Long id_ambito, Ambito ambito) {
    	ambito.setId_ambito(id_ambito);
        return ambitoRepository.save(ambito);
    }

}
