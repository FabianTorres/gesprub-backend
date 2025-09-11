package cl.rac.gesprub.Servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.rac.gesprub.Entidad.EstadoEvidencia;
import cl.rac.gesprub.Entidad.Evidencia;
import cl.rac.gesprub.Repositorio.EvidenciaRepository;
import cl.rac.gesprub.Repositorio.CasoRepository;
import cl.rac.gesprub.Repositorio.EstadoEvidenciaRepository;

import java.sql.Timestamp;
import java.util.Optional;


@Service
public class EvidenciaService {
	
	@Autowired
    private EvidenciaRepository evidenciaRepository;
	
	@Autowired
    private CasoRepository casoRepository;
	
	@Autowired
    private EstadoEvidenciaRepository estadoEvidenciaRepository;
	
	
	public Evidencia createEvidencia(Evidencia evidencia) {
		
		// --- LÓGICA DE TRADUCCIÓN AÑADIDA ---
        // 1. Buscamos el objeto EstadoEvidencia usando el ID que nos envió el frontend.
        Optional<EstadoEvidencia> estadoOpt = estadoEvidenciaRepository.findById((long) evidencia.getId_estado_evidencia());

        if (estadoOpt.isPresent()) {
            // 2. Si lo encontramos, extraemos el nombre (ej: "OK") y lo asignamos al campo de texto.
            String nombreEstado = estadoOpt.get().getNombre();
            evidencia.setEstado_evidencia(nombreEstado);
        } else {
            // Opcional: Si el ID no es válido, asignamos un valor por defecto o lanzamos un error.
            // Por ahora, lo dejamos como lo envía el frontend, lo que probablemente resultará en null.
            evidencia.setEstado_evidencia(null); 
        }
		
		evidencia.setFechaEvidencia(new Timestamp(System.currentTimeMillis()));
		Evidencia evidenciaGuardada = evidenciaRepository.save(evidencia);
		

		
		// --- LÓGICA AÑADIDA PARA ACTUALIZAR KANBAN ---
        if (evidenciaGuardada.getIdCaso() > 0) {
            casoRepository.findById((long) evidenciaGuardada.getIdCaso()).ifPresent(caso -> {
            	
                if ("OK".equalsIgnoreCase(evidenciaGuardada.getEstado_evidencia())) {
                    caso.setEstadoKanban("Completado");
                    casoRepository.save(caso);
                 
                } else if ("NK".equalsIgnoreCase(evidenciaGuardada.getEstado_evidencia())) {
                    caso.setEstadoKanban("Con Error");
                    casoRepository.save(caso);
                    
                }
            });
        }
        

        return evidenciaGuardada;
    }

    public List<Evidencia> getAllEvidencias() {
        return evidenciaRepository.findAll();
    }

    public Evidencia getEvidenciaById(Long id_evidencia) {
        return evidenciaRepository.findById(id_evidencia).orElse(null);
    }

    public Evidencia updateEvidencia(Long id_evidencia, Evidencia evidencia) {
    	evidencia.setId_evidencia(id_evidencia);
        return evidenciaRepository.save(evidencia);
    }

    public void deleteEvidencia(Long id_evidencia) {
    	evidenciaRepository.deleteById(id_evidencia);
    }
    
    @Transactional
    public Evidencia moverEvidencia(Long idEvidencia, int nuevoIdCaso) {
        // 1. Validar que el caso de destino exista.
        casoRepository.findById((long) nuevoIdCaso)
                .orElseThrow(() -> new RuntimeException("El caso de destino con id " + nuevoIdCaso + " no fue encontrado."));

        // 2. Buscar la evidencia que queremos mover.
        Evidencia evidencia = evidenciaRepository.findById(idEvidencia)
                .orElseThrow(() -> new RuntimeException("Evidencia no encontrada con id: " + idEvidencia));
        
        // 3. Actualizar el id_caso.
        evidencia.setIdCaso(nuevoIdCaso);
        
        // 4. Guardar y devolver la evidencia actualizada.
        return evidenciaRepository.save(evidencia);
    }
    
    /**
     * Actualiza únicamente el estado 'activo' de una evidencia.
     * @param idEvidencia El ID de la evidencia a modificar.
     * @param activo El nuevo valor para el campo (0 o 1).
     * @return La evidencia actualizada.
     */
    @Transactional
    public Evidencia updateActivo(Long idEvidencia, int activo) {
        Evidencia evidencia = evidenciaRepository.findById(idEvidencia)
                .orElseThrow(() -> new RuntimeException("Evidencia no encontrada con id: " + idEvidencia));

        evidencia.setActivo(activo);
        
        return evidenciaRepository.save(evidencia);
    }
    
 
}
