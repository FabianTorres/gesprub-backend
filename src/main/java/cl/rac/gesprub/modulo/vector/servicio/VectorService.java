package cl.rac.gesprub.modulo.vector.servicio;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.rac.gesprub.modulo.vector.dto.VectorDTO;
import cl.rac.gesprub.modulo.vector.dto.VectorLogDTO;
import cl.rac.gesprub.modulo.vector.dto.CatVectorDTO;
import cl.rac.gesprub.modulo.vector.entidad.CatVectorEntity;
import cl.rac.gesprub.modulo.vector.entidad.VectorEntity;
import cl.rac.gesprub.modulo.vector.entidad.VectorLogEntity;
import cl.rac.gesprub.modulo.vector.repositorio.CatVectorRepository;
import cl.rac.gesprub.modulo.vector.repositorio.VectorLogRepository;
import cl.rac.gesprub.modulo.vector.repositorio.VectorRepository;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VectorService {

    private final VectorRepository vectorRepository;
    private final VectorLogRepository vectorLogRepository;
    private final CatVectorRepository catVectorRepository;
    
    public List<CatVectorEntity> listarCatalogo() {
        return catVectorRepository.findAll();
    }

    public List<VectorDTO> listarTodos() {
        return vectorRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public VectorDTO guardar(VectorDTO dto) {
    	
    	CatVectorEntity catEntry = catVectorRepository.findById(dto.getVector())
                .orElseThrow(() -> new RuntimeException("Error: El vector " + dto.getVector() + " no existe en el catálogo maestro."));

        
    	VectorEntity entity = mapToEntity(dto);
    	if (entity.getElvcSeq() == null || entity.getElvcSeq().trim().isEmpty()) {
            if ("BIGDATA_INTEGRADO".equals(catEntry.getTipoTecnologia())) {
                entity.setElvcSeq("BD_RAC");
            } else {
                entity.setElvcSeq("NOMCES"); // Default Batch
            }
        }
        String usuario = dto.getUsuarioResponsable() != null ? dto.getUsuarioResponsable() : "SYSTEM";
        entity.setUsuarioModificacion(usuario);
        
        // 3. Guardar Principal
        VectorEntity guardado = vectorRepository.save(entity);

        // 4. INSERTAR LOG (CREACION)
        VectorLogEntity log = new VectorLogEntity(guardado, "CREACION", usuario);
        vectorLogRepository.save(log);

        return mapToDTO(guardado);
    }

    @Transactional
    public VectorDTO actualizar(Long id, VectorDTO dto) {
    	
    	catVectorRepository.findById(dto.getVector())
        .orElseThrow(() -> new RuntimeException("Error: El vector " + dto.getVector() + " no existe en el catálogo maestro."));
        VectorEntity entity = vectorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vector no encontrado"));

        
        String usuario = dto.getUsuarioResponsable() != null ? dto.getUsuarioResponsable() : "SYSTEM";
        
        // Actualizamos campos
        entity.setRut(dto.getRut());
        entity.setDv(dto.getDv());
        entity.setPeriodo(dto.getPeriodo());
        entity.setValor(dto.getValor());
        entity.setVector(dto.getVector());
        entity.setElvcSeq(dto.getElvc_seq());
        entity.setRut2(dto.getRut2());
        entity.setDv2(dto.getDv2());
        
        entity.setUsuarioModificacion(usuario);
        
        
        VectorEntity actualizado = vectorRepository.save(entity);
        
        VectorLogEntity log = new VectorLogEntity(actualizado, "MODIFICACION", usuario);
        vectorLogRepository.save(log);
        
        return mapToDTO(actualizado);
    }
    
    @Transactional
    public void eliminar(Long id, String usuarioResponsable) {
    	VectorEntity entity = vectorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vector no encontrado"));

        String usuario = usuarioResponsable != null ? usuarioResponsable : "SYSTEM";

        // 1. INSERTAR LOG (ELIMINACION) - Guardamos la foto ANTES de borrar
        VectorLogEntity log = new VectorLogEntity(entity, "ELIMINACION", usuario);
        vectorLogRepository.save(log);

        // 2. Borrar Principal
        vectorRepository.delete(entity);
    }

    /**
     * Genera el contenido del archivo .sql
     */
    public ByteArrayInputStream generarArchivoSql() {
    	List<VectorEntity> vectores = vectorRepository.findAllBatchVectors();
        StringBuilder sqlBuilder = new StringBuilder();

        for (VectorEntity v : vectores) {
            // Formatear NULLs correctamente para SQL (sin comillas)
            String valRut2 = (v.getRut2() != null) ? String.valueOf(v.getRut2()) : "null";
            String valDv2 = (v.getDv2() != null) ? "'" + v.getDv2() + "'" : "null";

            // Construir la linea INSERT exacta solicitada
            String linea = String.format(
                "insert into TR_RESUMEN_TRANS_2010 (CNTR_RUT,CNTR_DV,PERIODO_DJ,VALOR_TRANSFERENCIA,TRRT_KEYB,ELVC_SEQ,CNTR_RUT2,CNTR_DV2) Values (%d,'%s',%d,%d,%d,'%s',%s,%s);\n",
                v.getRut(),
                v.getDv(),
                v.getPeriodo(),
                v.getValor(),
                v.getVector(), // TRRT_KEYB
                v.getElvcSeq(),
                valRut2,
                valDv2
            );
            sqlBuilder.append(linea);
        }

        sqlBuilder.append("commit;");

        return new ByteArrayInputStream(sqlBuilder.toString().getBytes(StandardCharsets.UTF_8));
    }
    
    // Generar TXT (solo bigdata)
    public ByteArrayInputStream generarArchivoTxt() {
        // Usamos la query filtrada del repositorio
        List<VectorEntity> vectores = vectorRepository.findAllBigDataVectors();
        StringBuilder txtBuilder = new StringBuilder();
        
        // Formato fecha actual para la última columna: TIMESTAMP
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS"));

        for (VectorEntity v : vectores) {
            String valRut2 = (v.getRut2() != null) ? String.valueOf(v.getRut2()) : ""; // En TXT pipe suele ir vacio, no 'null' string
            String valDv2 = (v.getDv2() != null) ? v.getDv2() : "";

            // Formato: RUT|DV|PERIODO|VECTOR|VALOR|ELVC_SEQ|RUT2|DV2|1.0|TIMESTAMP
            String linea = String.format("%d|%s|%d|%d|%d|%s|%s|%s|1.0|%s\n",
                v.getRut(),
                v.getDv(),
                v.getPeriodo(),
                v.getVector(),
                v.getValor(),
                "BD_RAC", 
                valRut2,
                valDv2,
                timestamp
            );
            txtBuilder.append(linea);
        }

        return new ByteArrayInputStream(txtBuilder.toString().getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Para que el Frontend pregunte antes de intentar guardar
     * Retorna TRUE si ya existe la combinacion Rut + Periodo + Vector
     */
    public boolean existeCombinacion(Long rut, Integer periodo, Integer vector) {
        return vectorRepository.existsByRutAndPeriodoAndVector(rut, periodo, vector);
    }

    // --- Mappers Simples ---
    private VectorDTO mapToDTO(VectorEntity e) {
        VectorDTO dto = new VectorDTO();
        dto.setId(e.getId());
        dto.setRut(e.getRut());
        dto.setDv(e.getDv());
        dto.setPeriodo(e.getPeriodo());
        dto.setValor(e.getValor());
        dto.setVector(e.getVector());
        dto.setElvc_seq(e.getElvcSeq());
        dto.setRut2(e.getRut2());
        dto.setDv2(e.getDv2());
        dto.setUsuarioModificacion(e.getUsuarioModificacion());
        dto.setFechaModificacion(e.getFechaModificacion());
        return dto;
    }

    private VectorEntity mapToEntity(VectorDTO d) {
        VectorEntity e = new VectorEntity();
        e.setRut(d.getRut());
        e.setDv(d.getDv());
        e.setPeriodo(d.getPeriodo());
        e.setValor(d.getValor());
        e.setVector(d.getVector());
        e.setElvcSeq(d.getElvc_seq());
        e.setRut2(d.getRut2());
        e.setDv2(d.getDv2());
        return e;
    }
    
    public List<VectorLogDTO> listarLogs() {
        return vectorLogRepository.findTop100ByOrderByLogFechaDesc().stream()
                .map(this::mapToLogDTO)
                .collect(Collectors.toList());
    }
    
    private VectorLogDTO mapToLogDTO(VectorLogEntity e) {
        VectorLogDTO dto = new VectorLogDTO();
        dto.setLogId(e.getLogId());
        dto.setTipoAccion(e.getTipoAccion());
        dto.setLogFecha(e.getLogFecha());
        dto.setLogUsuario(e.getLogUsuario());
        
        dto.setIdOriginal(e.getIdOriginal());
        dto.setRut(e.getRut());
        dto.setDv(e.getDv());
        dto.setPeriodo(e.getPeriodo());
        dto.setValor(e.getValor());
        dto.setVector(e.getVector());
        dto.setElvc_seq(e.getElvcSeq());
        dto.setRut2(e.getRut2());
        dto.setDv2(e.getDv2());
        
        return dto;
    }
    
    @Transactional
    public CatVectorDTO crearVectorCatalogo(CatVectorDTO dto) {
        // 1. Validar que no exista el ID (PK)
        if (catVectorRepository.existsById(dto.getVectorId())) {
            throw new RuntimeException("Error: Ya existe un vector en el catálogo con el ID " + dto.getVectorId());
            // Nota: En un entorno real, podrias lanzar una excepcion personalizada que retorne 409 Conflict
        }

        // 2. Mapear DTO -> Entidad
        CatVectorEntity entity = new CatVectorEntity();
        entity.setVectorId(dto.getVectorId());
        entity.setNombre(dto.getNombre());
        entity.setTipoTecnologia(dto.getTipoTecnologia());

        // 3. Guardar
        CatVectorEntity guardado = catVectorRepository.save(entity);

        // 4. Retornar DTO
        return new CatVectorDTO(guardado.getVectorId(), guardado.getNombre(), guardado.getTipoTecnologia());
    }

    // GESTION DE CATALOGO: ACTUALIZAR
    @Transactional
    public CatVectorDTO actualizarVectorCatalogo(Integer id, CatVectorDTO dto) {
        // 1. Buscar existencia
        CatVectorEntity entity = catVectorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: No se encontró el vector con ID " + id));

        // 2. Actualizar campos (El ID no se toca porque es PK y PathVariable)
        entity.setNombre(dto.getNombre());
        entity.setTipoTecnologia(dto.getTipoTecnologia());

        // 3. Guardar cambios
        CatVectorEntity actualizado = catVectorRepository.save(entity);

        return new CatVectorDTO(actualizado.getVectorId(), actualizado.getNombre(), actualizado.getTipoTecnologia());
    }
}