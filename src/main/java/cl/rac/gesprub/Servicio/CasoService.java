package cl.rac.gesprub.Servicio;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.sql.Timestamp;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Comparator;
import java.util.function.Function;
import cl.rac.gesprub.Entidad.EstadoModificacion;
import cl.rac.gesprub.Repositorio.EstadoModificacionRepository;
import cl.rac.gesprub.dto.CasoImportDTO;
import cl.rac.gesprub.dto.ValidationErrorDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.rac.gesprub.Entidad.Caso;
import cl.rac.gesprub.Entidad.Componente;
import cl.rac.gesprub.Entidad.Evidencia;
import cl.rac.gesprub.Entidad.Fuente;
import cl.rac.gesprub.Entidad.Proyecto;
import cl.rac.gesprub.Entidad.Usuario;
import cl.rac.gesprub.Repositorio.CasoRepository;
import cl.rac.gesprub.Repositorio.ComponenteRepository;
import cl.rac.gesprub.Repositorio.EvidenciaRepository;
import cl.rac.gesprub.Repositorio.FuenteRepository;
import cl.rac.gesprub.Repositorio.UsuarioRepository;
import cl.rac.gesprub.dto.CasoConEvidenciaDTO;
import cl.rac.gesprub.dto.CasoDTO;
import cl.rac.gesprub.dto.CasoVersionUpdateDTO;
import cl.rac.gesprub.dto.EvidenciaItemDTO;
import cl.rac.gesprub.dto.FuenteDTO;
import cl.rac.gesprub.dto.HistorialDTO;
import cl.rac.gesprub.dto.KanbanDTO;
import cl.rac.gesprub.dto.MuroDTO;

import org.springframework.transaction.annotation.Transactional;
import cl.rac.gesprub.exception.ImportValidationException;

@Service
public class CasoService {
	
	@Autowired
    private UsuarioRepository usuarioRepository;
	
	@Autowired
    private CasoRepository casoRepository;
	
	@Autowired
    private EvidenciaRepository evidenciaRepository;
	
	@Autowired
    private FuenteRepository fuenteRepository;
	
	@Autowired
    private EstadoModificacionRepository estadoModificacionRepository;
	
	@Autowired
    private ComponenteRepository componenteRepository;
	
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
                                  .filter(e -> e.getActivo() == 1)
                                  // Le decimos al comparador que trate los nulos como los más antiguos
                                  .max(Comparator.comparing(Evidencia::getFechaEvidencia, Comparator.nullsFirst(Comparator.naturalOrder())))
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
        
        // 2. OPTIMIZACIÓN: Obtenemos los nombres de los componentes en una sola consulta.
        Set<Long> idsDeComponentes = casos.stream().map(c -> (long) c.getIdComponente()).collect(Collectors.toSet());
        Map<Integer, String> mapaNombresComponentes = componenteRepository.findAllById(idsDeComponentes).stream()
                .collect(Collectors.toMap(c -> c.getId_componente().intValue(), Componente::getNombre_componente));

        // 3. Extraemos los IDs de los casos para la siguiente consulta.
        List<Integer> idCasos = casos.stream()
                                     .map(caso -> caso.getId_caso().intValue())
                                     .collect(Collectors.toList());
        
        // 4. Obtenemos TODAS las evidencias para TODOS esos casos en UNA SOLA CONSULTA.
        List<Evidencia> todasLasEvidencias = evidenciaRepository.findByIdCasoIn(idCasos);
        
        // 5. Procesamos las evidencias en memoria para agruparlas por caso.
        Map<Integer, List<Evidencia>> evidenciasPorCaso = todasLasEvidencias.stream()
                .collect(Collectors.groupingBy(Evidencia::getIdCaso));
        
	    // 6. Creamos un mapa que contenga el conjunto de RUTs únicos para cada caso.
	    Map<Integer, Set<String>> rutsUnicosPorCaso = evidenciasPorCaso.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().stream()
                                  .map(Evidencia::getRut)
                                  .filter(rut -> rut != null && !rut.isEmpty())
                                  .collect(Collectors.toSet())
                ));

	    // 7. Encontramos la última evidencia activa para cada caso.
	    Map<Integer, Evidencia> ultimaEvidenciaPorCaso = evidenciasPorCaso.entrySet().stream()
	            .collect(Collectors.toMap(
	                Map.Entry::getKey,
	                entry -> entry.getValue().stream()
	                			  .filter(e -> e.getActivo() == 1)
	                              // Le decimos al comparador que trate los nulos como los más antiguos
	                              .max(Comparator.comparing(Evidencia::getFechaEvidencia, Comparator.nullsFirst(Comparator.naturalOrder())))
	                              .orElse(null)
	            ));

	    // 8. Construimos la respuesta final usando el nuevo constructor de CasoConEvidenciaDTO.
	    return casos.stream()
	        .map(caso -> new CasoConEvidenciaDTO(
	            caso,
	            ultimaEvidenciaPorCaso.get(caso.getId_caso().intValue()),
	            rutsUnicosPorCaso.getOrDefault(caso.getId_caso().intValue(), Collections.emptySet()),
                mapaNombresComponentes // <--- Pasamos el mapa con los nombres
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
        resultado.setId_usuario_asignado(caso.getIdUsuarioAsignado());
        
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
    
	 // Metodo de importacion de casos
    @Transactional(rollbackFor = Exception.class) // Si algo falla, revierte todos los cambios
    public void importarCasos(List<CasoImportDTO> casosDto, int idComponente) {

        List<ValidationErrorDTO> errores = new ArrayList<>();
        
        // --- 1. OPTIMIZACIÓN: Cargar catálogos una sola vez ---
        
        Map<String, EstadoModificacion> estadosPorNombre = estadoModificacionRepository.findAll()
                .stream()
                .collect(Collectors.toMap(EstadoModificacion::getNombre, em -> em));

        Map<String, Fuente> fuentesPorNombre = fuenteRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Fuente::getNombre_fuente, f -> f));
        
        Map<Long, Usuario> usuariosPorId = usuarioRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Usuario::getIdUsuario, u -> u));

        List<Caso> nuevosCasos = new ArrayList<>();

        // --- 2. VALIDACIÓN Y TRANSFORMACIÓN ---
        for (int i = 0; i < casosDto.size(); i++) {
            CasoImportDTO dto = casosDto.get(i);
            int fila = i + 1; // Fila legible para el usuario

            // Validar Estado de Modificación
            EstadoModificacion estado = estadosPorNombre.get(dto.getNombre_estado_modificacion());
            if (estado == null) {
                errores.add(new ValidationErrorDTO(fila, "nombre_estado_modificacion", "El Estado Modificación no es válido.", dto.getNombre_estado_modificacion()));
            }
            // Validar que el usuario creador exista
            if (dto.getId_usuario_creador() == null) {
                 errores.add(new ValidationErrorDTO(fila, "id_usuario_creador", "El ID del usuario creador no puede ser nulo.", null));
            } else if (!usuariosPorId.containsKey(dto.getId_usuario_creador().longValue())) {
                errores.add(new ValidationErrorDTO(fila, "id_usuario_creador", "El usuario con el ID proporcionado no existe.", String.valueOf(dto.getId_usuario_creador())));
            }

            // Procesar Fuentes
            Set<Fuente> fuentesParaElCaso = new HashSet<>();
            if (dto.getNombres_fuentes() != null && !dto.getNombres_fuentes().isBlank()) {
                // Asumimos que las fuentes vienen separadas por comas. Ej: "RF-101, CU-05"
                String[] nombresFuente = dto.getNombres_fuentes().split(",");
                for (String nombre : nombresFuente) {
                    String nombreTrim = nombre.trim();
                    if (!nombreTrim.isEmpty()) {
                        Fuente fuenteExistente = fuentesPorNombre.get(nombreTrim);
                        if (fuenteExistente != null) {
                            fuentesParaElCaso.add(fuenteExistente);
                        } else {
                        	// Si la fuente no existe, registramos un error y no la creamos.
                            errores.add(new ValidationErrorDTO(
                                fila, 
                                "nombres_fuentes", 
                                "La fuente '" + nombreTrim + "' no existe. Debe crearla primero.", 
                                dto.getNombres_fuentes()
                            ));
                        }
                    }
                }
            }

            // Si no hay errores para esta fila, preparamos el nuevo objeto Caso
            if (errores.isEmpty()) {
                Caso nuevoCaso = new Caso();
                nuevoCaso.setNombre_caso(dto.getNombre_caso());
                nuevoCaso.setDescripcion_caso(dto.getDescripcion_caso());
                nuevoCaso.setVersion(dto.getVersion());
                nuevoCaso.setIdComponente(idComponente);
                nuevoCaso.setId_estado_modificacion(estado.getId_estado_modificacion().intValue());
                nuevoCaso.setFuentes(fuentesParaElCaso);
                nuevoCaso.setActivo(1);
                nuevoCaso.setId_usuario_creador(dto.getId_usuario_creador());
                nuevoCaso.setAnio(Year.now().getValue());
                nuevoCaso.setPasos(dto.getPasos());
                nuevoCaso.setPrecondiciones(dto.getPrecondiciones());
                nuevoCaso.setResultado_esperado(dto.getResultado_esperado());
                
                nuevosCasos.add(nuevoCaso);
            }
        }

        // --- 3. DECISIÓN FINAL ---
        if (!errores.isEmpty()) {
            // Si encontramos algún error, lanzamos una excepción con la lista detallada
            // La crearemos en el siguiente paso. Por ahora, puedes usar una RuntimeException
        	throw new ImportValidationException("Error de validación durante la importación. No se guardó ningún caso.", errores); 
            // throw new ImportValidationException("Error de validación durante la importación.", errores);
        } else {
            // Si todo está perfecto, guardamos todos los nuevos casos en la base de datos
            casoRepository.saveAll(nuevosCasos);
        }
    }
    
    /**
     * Obtiene los casos para el muro, separados en backlog y tareas del usuario.
     */
    public MuroDTO getMuroData(int componenteId, Long usuarioLogueadoId) {
        // 1. OBTENER CASOS DEL BACKLOG Y MIS TAREAS
        List<Caso> casosBacklogEntidad = casoRepository.findByIdComponenteAndActivo(componenteId, 1);
        
        Componente componenteActual = componenteRepository.findById((long) componenteId)
                .orElseThrow(() -> new RuntimeException("Componente no encontrado con id: " + componenteId));
        Proyecto proyecto = componenteActual.getProyecto();

        List<Caso> misTareasEntidad = new ArrayList<>();
        if (proyecto != null) {
            List<Integer> idsComponentesDelProyecto = componenteRepository.findComponenteIdsByProyectoId(proyecto.getId_proyecto());
            if (!idsComponentesDelProyecto.isEmpty()) {
                misTareasEntidad = casoRepository.findByActivoAndIdUsuarioAsignadoAndIdComponenteIn(
                                 1, usuarioLogueadoId.intValue(), idsComponentesDelProyecto);
            }
        }

        // Recolectamos todos los IDs de componentes de ambas listas en un solo Set para evitar duplicados.
        Set<Long> idsDeComponentes = new HashSet<>();
        casosBacklogEntidad.forEach(caso -> idsDeComponentes.add((long) caso.getIdComponente()));
        misTareasEntidad.forEach(caso -> idsDeComponentes.add((long) caso.getIdComponente()));

        // Hacemos UNA SOLA consulta a la base de datos para obtener los nombres de todos los componentes necesarios.
        Map<Integer, String> mapaNombresComponentes = componenteRepository.findAllById(idsDeComponentes).stream()
                .collect(Collectors.toMap(c -> c.getId_componente().intValue(), Componente::getNombre_componente));

        // Mapeamos a DTO usando el nuevo constructor y el mapa de nombres.
        List<CasoDTO> backlog = casosBacklogEntidad.stream()
                .filter(caso -> caso.getIdUsuarioAsignado() == null)
                .map(caso -> new CasoDTO(caso, mapaNombresComponentes.get(caso.getIdComponente())))
                .collect(Collectors.toList());

        List<CasoDTO> misTareas = misTareasEntidad.stream()
                .map(caso -> new CasoDTO(caso, mapaNombresComponentes.get(caso.getIdComponente())))
                .collect(Collectors.toList());

        // 4. Devolvemos la respuesta final.
        return new MuroDTO(backlog, misTareas);
    }
    
    /**
     * Asigna un caso a un usuario específico.
     * @param idCaso El ID del caso a modificar.
     * @param usuarioId El ID del usuario al que se le asignará el caso.
     * @return El caso actualizado.
     */
    @Transactional
    public Caso asignarUsuario(Long idCaso, Long usuarioId) {
        // 1. Validamos que el usuario al que se va a asignar exista.
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("El usuario a asignar no fue encontrado con id: " + usuarioId));

        // 2. Buscamos el caso que queremos asignar.
        Caso caso = casoRepository.findById(idCaso)
                .orElseThrow(() -> new RuntimeException("Caso no encontrado con id: " + idCaso));

        // 3. Actualizamos el campo y guardamos.
        caso.setIdUsuarioAsignado(usuarioId.intValue());
        
        caso.setEstadoKanban("Por Hacer");
        caso.setFechaAsignacion(new Timestamp(System.currentTimeMillis()));
        
        return casoRepository.save(caso);
    }
    
    /**
     * Desasigna un caso, estableciendo su id_usuario_asignado a NULL.
     * @param idCaso El ID del caso a desasignar.
     * @return El caso actualizado.
     */
    @Transactional
    public Caso desasignarUsuario(Long idCaso) {
        // 1. Buscamos el caso.
        Caso caso = casoRepository.findById(idCaso)
                .orElseThrow(() -> new RuntimeException("Caso no encontrado con id: " + idCaso));

        // 2. Establecemos el campo a null y guardamos.
        caso.setIdUsuarioAsignado(null);
        
        return casoRepository.save(caso);
    }
    
    
    /**
     * Obtiene y agrupa los casos para el tablero Kanban.
     */
    public KanbanDTO getKanbanData(Long proyectoId, Optional<Long> usuarioId) {
        // 1. Obtener todos los casos activos del proyecto que son relevantes para el Kanban
        List<Integer> idsComponentesDelProyecto = componenteRepository.findComponenteIdsByProyectoId(proyectoId);
        if (idsComponentesDelProyecto.isEmpty()) {
            return new KanbanDTO(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }
        List<Caso> casosActivosDelProyecto = casoRepository.findByActivoAndIdComponenteIn(1, idsComponentesDelProyecto);
        
        List<Caso> casosParaKanban = casosActivosDelProyecto.stream()
            .filter(caso -> caso.getIdUsuarioAsignado() != null || "Completado".equals(caso.getEstadoKanban()))
            .collect(Collectors.toList());

        if (casosParaKanban.isEmpty()) {
            return new KanbanDTO(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }
        
        // 2. Obtener datos adicionales para el filtrado y enriquecimiento
        List<Integer> idsCasosParaKanban = casosParaKanban.stream().map(c -> c.getId_caso().intValue()).collect(Collectors.toList());
        
        List<Evidencia> evidenciasActivas = evidenciaRepository.findByIdCasoIn(idsCasosParaKanban).stream()
                .filter(e -> e.getActivo() == 1).collect(Collectors.toList());
        
        Map<Integer, Evidencia> ultimaEvidenciaPorCaso = evidenciasActivas.stream()
                .collect(Collectors.toMap(
                    Evidencia::getIdCaso,
                    Function.identity(),
                    (e1, e2) -> e1.getFechaEvidencia().after(e2.getFechaEvidencia()) ? e1 : e2
                ));

        Map<Integer, String> mapaNombresComponentes = componenteRepository.findAllById(
            casosParaKanban.stream().map(c -> (long) c.getIdComponente()).collect(Collectors.toSet())
        ).stream().collect(Collectors.toMap(c -> c.getId_componente().intValue(), Componente::getNombre_componente));

        // 3. APLICAR EL FILTRO DE USUARIO CON LA LÓGICA CORREGIDA
        List<Caso> casosFiltrados;
        if (usuarioId.isPresent()) {
            Long uid = usuarioId.get();
            casosFiltrados = casosParaKanban.stream()
                .filter(caso -> {
                    // --- CORRECCIÓN AQUÍ ---
                    // Condición 1: El caso está asignado al usuario (con comprobación de nulidad).
                    boolean estaAsignadoAlUsuario = caso.getIdUsuarioAsignado() != null 
                                                    && uid.equals(Long.valueOf(caso.getIdUsuarioAsignado()));

                    // Condición 2: El caso fue completado por el usuario.
                    boolean fueCompletadoPorUsuario = false;
                    if ("Completado".equals(caso.getEstadoKanban())) {
                        Evidencia ultimaEvidencia = ultimaEvidenciaPorCaso.get(caso.getId_caso().intValue());
                        if (ultimaEvidencia != null && ultimaEvidencia.getUsuarioEjecutante() != null) {
                            fueCompletadoPorUsuario = uid.equals(ultimaEvidencia.getUsuarioEjecutante().getIdUsuario());
                        }
                    }
                    return estaAsignadoAlUsuario || fueCompletadoPorUsuario;
                })
                .collect(Collectors.toList());
        } else {
            // Si no se filtra por usuario, se usan todos los casos del Kanban.
            casosFiltrados = casosParaKanban;
        }

        // 4. Agrupar los casos resultantes y construir la respuesta
        Map<String, List<CasoDTO>> casosAgrupados = casosFiltrados.stream()
            .map(caso -> new CasoDTO(caso, mapaNombresComponentes.get(caso.getIdComponente()), ultimaEvidenciaPorCaso.get(caso.getId_caso().intValue())))
            .collect(Collectors.groupingBy(
                casoDto -> casoDto.getEstadoKanban() != null ? casoDto.getEstadoKanban() : "Desconocido"
            ));

        return new KanbanDTO(
            casosAgrupados.getOrDefault("Por Hacer", new ArrayList<>()),
            casosAgrupados.getOrDefault("Completado", new ArrayList<>()),
            casosAgrupados.getOrDefault("Con Error", new ArrayList<>())
        );
    }
    
    

}
