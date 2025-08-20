package cl.rac.gesprub.Servicio;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.rac.gesprub.Entidad.Caso;
import cl.rac.gesprub.Entidad.Evidencia;
import cl.rac.gesprub.Entidad.Fuente;
import cl.rac.gesprub.Repositorio.CasoRepository;
import cl.rac.gesprub.Repositorio.EvidenciaRepository;
import cl.rac.gesprub.Repositorio.FuenteRepository;
import cl.rac.gesprub.dto.CasoConEvidenciaDTO;
import cl.rac.gesprub.dto.CasoVersionUpdateDTO;
import cl.rac.gesprub.dto.EvidenciaItemDTO;
import cl.rac.gesprub.dto.FuenteDTO;
import cl.rac.gesprub.dto.HistorialDTO;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CasoService {
	
	@Autowired
    private CasoRepository casoRepository;
	
	@Autowired
    private EvidenciaRepository evidenciaRepository;
	
	@Autowired
    private FuenteRepository fuenteRepository;
	
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
    
    public List<CasoConEvidenciaDTO> getCasosConUltimaEvidencia() {
    	
    	// 1. Obtenemos todos los casos.
        List<Caso> casos = casoRepository.findAll();
        if (casos.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 2. Extraemos los IDs de los casos.
        List<Integer> idCasos = casos.stream()
                                     .map(caso -> caso.getId_caso().intValue())
                                     .collect(Collectors.toList());

        // 3. Obtenemos TODAS las evidencias para esos casos en UNA SOLA CONSULTA.
        List<Evidencia> todasLasEvidencias = evidenciaRepository.findByIdCasoIn(idCasos);

        // 4. Agrupamos las evidencias por id_caso.
        Map<Integer, List<Evidencia>> evidenciasPorCaso = todasLasEvidencias.stream()
                .collect(Collectors.groupingBy(Evidencia::getIdCaso));

        // 5. Creamos un mapa con el conjunto de RUTs únicos para cada caso.
        Map<Integer, Set<String>> rutsUnicosPorCaso = evidenciasPorCaso.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().stream()
                                  .map(Evidencia::getRut)
                                  .filter(rut -> rut != null && !rut.isEmpty())
                                  .collect(Collectors.toSet())
                ));

        // 6. Encontramos la última evidencia para cada caso.
        Map<Integer, Evidencia> ultimaEvidenciaPorCaso = evidenciasPorCaso.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().stream()
                                  .max((e1, e2) -> e1.getFechaEvidencia().compareTo(e2.getFechaEvidencia()))
                                  .orElse(null)
                ));

        // 7. Construimos la respuesta final, pasando los 3 parámetros al constructor del DTO.
        return casos.stream()
            .map(caso -> new CasoConEvidenciaDTO(
                caso,
                ultimaEvidenciaPorCaso.get(caso.getId_caso().intValue()),
                rutsUnicosPorCaso.getOrDefault(caso.getId_caso().intValue(), Collections.emptySet())
            ))
            .collect(Collectors.toList());
        

    }
    
    
    //Se obtiene las evidencias de un caso por su id
    public List<Evidencia> getEvidenciasPorCaso(int idCaso) {
        return evidenciaRepository.findByIdCasoOrderByFechaEvidenciaDesc(idCaso);
    }
    
    public List<CasoConEvidenciaDTO> getCasosConUltimaEvidenciaPorComponente(int componenteId) {
    	// 1. Obtenemos todos los casos para el componente.
        List<Caso> casos = casoRepository.findByIdComponente(componenteId);
        if (casos.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 2. Extraemos los IDs de los casos para la siguiente consulta.
        List<Integer> idCasos = casos.stream()
                                     .map(caso -> caso.getId_caso().intValue())
                                     .collect(Collectors.toList());
        
        
        // 3. Obtenemos TODAS las evidencias para TODOS esos casos en UNA SOLA CONSULTA.
        List<Evidencia> todasLasEvidencias = evidenciaRepository.findByIdCasoIn(idCasos);
        
        // 4. Procesamos las evidencias en memoria para agruparlas por caso.
        Map<Integer, List<Evidencia>> evidenciasPorCaso = todasLasEvidencias.stream()
                .collect(Collectors.groupingBy(Evidencia::getIdCaso));
        
	     // 5. Creamos un mapa que contenga el conjunto de RUTs únicos para cada caso.
	      Map<Integer, Set<String>> rutsUnicosPorCaso = evidenciasPorCaso.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().stream()
                                  .map(Evidencia::getRut)
                                  .filter(rut -> rut != null && !rut.isEmpty())
                                  .collect(Collectors.toSet())
                ));
	      // 6. Encontramos la última evidencia para cada caso (esto es más rápido en memoria).
	        Map<Integer, Evidencia> ultimaEvidenciaPorCaso = evidenciasPorCaso.entrySet().stream()
	                .collect(Collectors.toMap(
	                    Map.Entry::getKey,
	                    entry -> entry.getValue().stream()
	                                  .max((e1, e2) -> e1.getFechaEvidencia().compareTo(e2.getFechaEvidencia()))
	                                  .orElse(null)
	                ));
        

	        // 7. Construimos la respuesta final.
	        return casos.stream()
	            .map(caso -> new CasoConEvidenciaDTO(
	                caso,
	                ultimaEvidenciaPorCaso.get(caso.getId_caso().intValue()),
	                rutsUnicosPorCaso.getOrDefault(caso.getId_caso().intValue(), Collections.emptySet())
	            ))
	            .collect(Collectors.toList());
    }
    
    public HistorialDTO getHistorialPorCaso(int idCaso) {
        // 1. Buscamos la entidad Caso. Si no existe, lanzamos un error.
        Caso caso = casoRepository.findById((long) idCaso)
                .orElseThrow(() -> new RuntimeException("Caso no encontrado con id: " + idCaso));

        // 2. Buscamos todas las evidencias asociadas a ese caso.
        // Spring Data JPA crea este método automáticamente a partir del nombre.
        List<Evidencia> evidencias = evidenciaRepository.findByIdCasoOrderByFechaEvidenciaDesc(idCaso);

        // 3. Transformamos la lista de Entidades a una lista de DTOs.
        List<EvidenciaItemDTO> historialItems = evidencias.stream()
                .map(evidencia -> {
                    EvidenciaItemDTO item = new EvidenciaItemDTO();
                    item.setId_evidencia(evidencia.getId_evidencia());
                    item.setDescripcion_evidencia(evidencia.getDescripcion_evidencia());
                    item.setEstado_evidencia(evidencia.getEstado_evidencia());
                    item.setId_estado_evidencia(evidencia.getId_estado_evidencia());
                    item.setVersion_ejecucion(evidencia.getVersion_ejecucion());
                    
                    item.setFecha_evidencia(evidencia.getFechaEvidencia());
                    item.setCriticidad(evidencia.getCriticidad());
                    item.setId_criticidad(evidencia.getId_criticidad());
                    item.setUrl_evidencia(evidencia.getUrl_evidencia());
                    item.setId_jira(evidencia.getId_jira());
                    item.setRut(evidencia.getRut());
                    item.setActivo(evidencia.getActivo());
                    

                    // Obtenemos el nombre del usuario relacionado.
                    if (evidencia.getUsuarioEjecutante() != null) {
                        item.setNombreUsuarioEjecutante(evidencia.getUsuarioEjecutante().getNombreUsuario());
                    }

                    return item;
                })
                .collect(Collectors.toList());

        // 4. Creamos el DTO final de respuesta.
        HistorialDTO resultado = new HistorialDTO();
        resultado.setId_caso(caso.getId_caso());
        resultado.setNombre_caso(caso.getNombre_caso());
        resultado.setDescripcion_caso(caso.getDescripcion_caso());
        resultado.setNum_formulario(caso.getNum_formulario());
        resultado.setId_estado_modificacion(caso.getId_estado_modificacion());
        resultado.setHistorial(historialItems);
        resultado.setFuente(caso.getFuente());
        
        // --- LÓGICA DE CONVERSIÓN AÑADIDA ---
        if (caso.getFuentes() != null) {
            resultado.setFuentes(caso.getFuentes().stream()
                .map(FuenteDTO::new)
                .collect(Collectors.toSet()));
        }
        

        return resultado;
    }
    
    public List<Integer> getNumerosDeFormularioUnicos() {
        return casoRepository.findDistinctNumFormulario();
    }
    
    public Caso updateCasoVersion(Long id_caso, CasoVersionUpdateDTO versionDto) {
        // 1. Buscamos la entidad Caso completa en la base de datos.
        //    Si no la encontramos, lanzamos una excepción.
        Caso casoExistente = casoRepository.findById(id_caso)
                .orElseThrow(() -> new RuntimeException("Caso no encontrado con id: " + id_caso));

        // 2. Actualizamos únicamente el campo 'version'.
        casoExistente.setVersion(versionDto.getVersion());

        // 3. Guardamos la entidad actualizada y la devolvemos.
        return casoRepository.save(casoExistente);
    }
    
    @Transactional
    public Caso asignarFuenteACaso(Long idCaso, Long idFuente) {
        // 1. Buscamos el caso.
        Caso caso = casoRepository.findById(idCaso)
                .orElseThrow(() -> new RuntimeException("Caso no encontrado con id: " + idCaso));

        // 2. Buscamos la fuente.
        Fuente fuente = fuenteRepository.findById(idFuente)
                .orElseThrow(() -> new RuntimeException("Fuente no encontrada con id: " + idFuente));
        
        // 3. Añadimos la fuente al conjunto de fuentes del caso.
        caso.getFuentes().add(fuente);

        // 4. Guardamos el caso. JPA se encargará de actualizar la tabla intermedia.
        return casoRepository.save(caso);
    }
    
    @Transactional
    public Caso quitarFuenteDeCaso(Long idCaso, Long idFuente) {
        // 1. Buscamos el caso.
        Caso caso = casoRepository.findById(idCaso)
                .orElseThrow(() -> new RuntimeException("Caso no encontrado con id: " + idCaso));

        // 2. Buscamos la fuente.
        Fuente fuente = fuenteRepository.findById(idFuente)
                .orElseThrow(() -> new RuntimeException("Fuente no encontrada con id: " + idFuente));
        
        // 3. Quitamos la fuente del conjunto de fuentes del caso.
        caso.getFuentes().remove(fuente);

        // 4. Guardamos el caso para que JPA actualice la tabla intermedia.
        return casoRepository.save(caso);
    }
    
    @Transactional(readOnly = true) // readOnly = true es una optimización para consultas
    public Set<FuenteDTO> getFuentesPorCaso(Long idCaso) {
        Caso caso = casoRepository.findById(idCaso)
                .orElseThrow(() -> new RuntimeException("Caso no encontrado con id: " + idCaso));

        return caso.getFuentes().stream()
                .map(FuenteDTO::new)
                .collect(Collectors.toSet());
    }
    
    @Transactional
    public Caso sincronizarFuentesParaCaso(Long idCaso, List<Long> idsFuente) {
        // 1. Buscamos el caso.
        Caso caso = casoRepository.findById(idCaso)
                .orElseThrow(() -> new RuntimeException("Caso no encontrado con id: " + idCaso));

        // 2. Buscamos todas las entidades Fuente correspondientes a los IDs.
        List<Fuente> fuentes = fuenteRepository.findAllById(idsFuente);
        
        // 3. Reemplazamos las fuentes antiguas por las nuevas.
        // La anotación @ManyToMany se encarga de la magia en la tabla intermedia.
        caso.getFuentes().clear();
        caso.getFuentes().addAll(fuentes);

        // 4. Guardamos el caso actualizado.
        return casoRepository.save(caso);
    }
    
    public List<String> getRutsUnicosPorCaso(int idCaso) {
		return evidenciaRepository.findDistinctRutByIdCaso(idCaso);
	}

}
