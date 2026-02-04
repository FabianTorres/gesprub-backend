package cl.rac.gesprub.modulo.vector.servicio;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.rac.gesprub.modulo.vector.dto.VectorDTO;
import cl.rac.gesprub.modulo.vector.dto.VectorLogDTO;
import cl.rac.gesprub.modulo.vector.dto.AltaMasivaDTO;
import cl.rac.gesprub.modulo.vector.dto.BajaMasivaDTO;
import cl.rac.gesprub.modulo.vector.dto.CatVectorDTO;
import cl.rac.gesprub.modulo.vector.dto.CatVersionDTO;
import cl.rac.gesprub.modulo.vector.entidad.CatVectorEntity;
import cl.rac.gesprub.modulo.vector.entidad.CatVersionEntity;
import cl.rac.gesprub.modulo.vector.entidad.VectorEntity;
import cl.rac.gesprub.modulo.vector.entidad.VectorLogEntity;
import cl.rac.gesprub.modulo.vector.repositorio.CatVectorRepository;
import cl.rac.gesprub.modulo.vector.repositorio.CatVersionRepository;
import cl.rac.gesprub.modulo.vector.repositorio.VectorLogRepository;
import cl.rac.gesprub.modulo.vector.repositorio.VectorRepository;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

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
    @Autowired 
    private CatVersionRepository catVersionRepository;
    
    public List<CatVectorEntity> listarCatalogo() {
        return catVectorRepository.findAll();
    }
    
    
    public List<CatVersionDTO> listarVersiones(Integer periodo) {
        return catVersionRepository.findByPeriodoOrderByFechaRegistroDesc(periodo).stream()
                .map(e -> {
                    CatVersionDTO dto = new CatVersionDTO();
                    dto.setId(e.getId());
                    dto.setPeriodo(e.getPeriodo());
                    dto.setCodigoVersion(e.getCodigoVersion());
                    dto.setFechaRegistro(e.getFechaRegistro());
                    dto.setDescripcion(e.getDescripcion());
                    return dto;
                }).collect(Collectors.toList());
    }
    
    public List<VectorDTO> listarDatosCargados(Integer periodo) {
        List<VectorEntity> resultados;

        if (periodo != null) {
            // Si hay filtro, buscamos solo ese periodo
            resultados = vectorRepository.findByPeriodo(periodo);
        } else {
            // Si es null, traemos todo (comportamiento default)
            resultados = vectorRepository.findAll();
        }

        return resultados.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public CatVersionDTO crearVersion(CatVersionDTO dto) {
        if (catVersionRepository.existsByPeriodoAndCodigoVersion(dto.getPeriodo(), dto.getCodigoVersion())) {
            throw new RuntimeException("La versión " + dto.getCodigoVersion() + " ya existe para el periodo " + dto.getPeriodo());
        }
        CatVersionEntity entity = new CatVersionEntity();
        entity.setPeriodo(dto.getPeriodo());
        entity.setCodigoVersion(dto.getCodigoVersion());
        entity.setDescripcion(dto.getDescripcion());
        entity.setFechaRegistro(LocalDateTime.now());
        
        CatVersionEntity saved = catVersionRepository.save(entity);
        dto.setId(saved.getId());
        dto.setFechaRegistro(saved.getFechaRegistro());
        return dto;
    }
    
    public List<CatVectorDTO> listarCatalogoPorPeriodo(Integer periodo, Boolean incluirEliminados) {
        List<CatVectorEntity> entities;
        if (Boolean.TRUE.equals(incluirEliminados)) {
            entities = catVectorRepository.findByPeriodo(periodo);
        } else {
            entities = catVectorRepository.findByPeriodoAndEstadoTrue(periodo);
        }

        return entities.stream().map(e -> {
            CatVectorDTO dto = new CatVectorDTO();
            dto.setId(e.getId());
            dto.setVectorId(e.getVectorId());
            dto.setPeriodo(e.getPeriodo());
            dto.setNombre(e.getNombre());
            dto.setTipoTecnologia(e.getTipoTecnologia());
            dto.setEstado(e.getEstado());
            if (e.getVersionIngreso() != null) dto.setVersionIngreso(e.getVersionIngreso().getCodigoVersion());
            if (e.getVersionRetiro() != null) dto.setVersionRetiro(e.getVersionRetiro().getCodigoVersion());
            return dto;
        }).collect(Collectors.toList());
    }
    
    @Transactional
    public void altaMasivaVectores(AltaMasivaDTO dto) {
        CatVersionEntity version = catVersionRepository.findById(dto.getVersionId())
                .orElseThrow(() -> new RuntimeException("Versión no encontrada"));

        for (CatVectorDTO vDto : dto.getVectores()) {
            // Validar unicidad en el periodo
            if (catVectorRepository.existsByVectorIdAndPeriodo(vDto.getVectorId(), version.getPeriodo())) {
                continue; // O lanzar error, depende de la regla. Aquí omitimos duplicados.
            }
            
            CatVectorEntity entity = new CatVectorEntity();
            entity.setVectorId(vDto.getVectorId());
            entity.setNombre(vDto.getNombre());
            entity.setTipoTecnologia(vDto.getTipoTecnologia());
            entity.setPeriodo(version.getPeriodo()); 
            entity.setEstado(true);
            entity.setVersionIngreso(version); 
            
            catVectorRepository.save(entity);
        }
    }
    
    @Transactional
    public void bajaMasivaVectores(BajaMasivaDTO dto) {
        CatVersionEntity version = catVersionRepository.findById(dto.getVersionId())
                .orElseThrow(() -> new RuntimeException("Versión no encontrada"));

        for (Integer vecIdNegocio : dto.getVectorIds()) {
            CatVectorEntity entity = catVectorRepository.findByVectorIdAndPeriodo(vecIdNegocio, version.getPeriodo());
            if (entity != null && entity.getEstado()) {
                entity.setEstado(false); // Soft Delete
                entity.setVersionRetiro(version); // Link a la versión de baja
                catVectorRepository.save(entity);
            }
        }
    }
    
    
    @Transactional
    public void ejecutarRollover(Integer periodoOrigen, Integer periodoDestino) {
        // 1. Verificar si ya existen datos en destino (Seguridad)
        if (!catVectorRepository.findByPeriodo(periodoDestino).isEmpty()) {
            throw new RuntimeException("El periodo destino " + periodoDestino + " ya contiene datos. No se puede realizar Rollover.");
        }

        // 2. Crear o buscar Versión 1.0 para el destino
        CatVersionEntity v1Destino = catVersionRepository.findByPeriodoAndCodigoVersion(periodoDestino, "1.0");
        if (v1Destino == null) {
            v1Destino = new CatVersionEntity();
            v1Destino.setPeriodo(periodoDestino);
            v1Destino.setCodigoVersion("1.0");
            v1Destino.setDescripcion("Generado por Rollover desde " + periodoOrigen);
            v1Destino.setFechaRegistro(LocalDateTime.now());
            v1Destino = catVersionRepository.save(v1Destino);
        }

        // 3. Obtener vectores activos del origen
        List<CatVectorEntity> origenList = catVectorRepository.findByPeriodoAndEstadoTrue(periodoOrigen);

        // 4. Clonar
        for (CatVectorEntity origen : origenList) {
            CatVectorEntity destino = new CatVectorEntity();
            destino.setVectorId(origen.getVectorId()); // Mismo ID de negocio
            destino.setNombre(origen.getNombre());
            destino.setTipoTecnologia(origen.getTipoTecnologia());
            destino.setPeriodo(periodoDestino); // Nuevo periodo
            destino.setEstado(true);
            destino.setVersionIngreso(v1Destino); // Nacen en la v1.0 del nuevo año
            
            catVectorRepository.save(destino);
        }
    }

    @Transactional
    public VectorDTO guardar(VectorDTO dto) {
    	
    	if (dto.getDv() != null) {
            dto.setDv(dto.getDv().toUpperCase());
        }
        if (dto.getDv2() != null) {
            dto.setDv2(dto.getDv2().toUpperCase());
        }
    	
    	if (vectorRepository.existsByRutAndPeriodoAndVector(dto.getRut(), dto.getPeriodo(), dto.getVector())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                String.format("Registro duplicado: Ya existe un vector %d para el RUT %d en el periodo %d", 
                dto.getVector(), dto.getRut(), dto.getPeriodo()));
        }
    	
    	CatVectorEntity catEntry = catVectorRepository.findByVectorIdAndPeriodo(dto.getVector(), dto.getPeriodo());
        
    	if (catEntry == null) {
            // Si retorna null es que no existe esa combinación en el catálogo
            throw new RuntimeException("Error: El vector " + dto.getVector() + " no está catalogado para el periodo " + dto.getPeriodo());
        }
    	
    	if (!catEntry.getEstado()) {
            throw new RuntimeException("Error: El vector " + dto.getVector() + " existe pero está INACTIVO (Eliminado) para el periodo " + dto.getPeriodo());
       }
    	
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
    	
    	if (dto.getDv() != null) {
            dto.setDv(dto.getDv().toUpperCase());
        }
        if (dto.getDv2() != null) {
            dto.setDv2(dto.getDv2().toUpperCase());
        }
    	
    	CatVectorEntity catEntry = catVectorRepository.findByVectorIdAndPeriodo(dto.getVector(), dto.getPeriodo());
        if (catEntry == null) {
             throw new RuntimeException("Error: El vector " + dto.getVector() + " no está catalogado para el periodo " + dto.getPeriodo());
        }
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
        
        if (dto.getIntencionCarga() != null) {
            entity.setIntencionCarga(dto.getIntencionCarga());
        }
        entity.setProcesado(false);
        
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
    public ByteArrayInputStream generarArchivoSql(Integer periodo) {
    	List<VectorEntity> vectores = vectorRepository.findForBatchExport(periodo);
        StringBuilder sqlBuilder = new StringBuilder();

        for (VectorEntity v : vectores) {
        	
        	String dvNormalizado = (v.getDv() != null) ? v.getDv().toUpperCase() : "K";
            // Formatear NULLs correctamente para SQL (sin comillas)
            String valRut2 = (v.getRut2() != null) ? String.valueOf(v.getRut2()) : "null";
            String valDv2 = (v.getDv2() != null) ? "'" + v.getDv2().toUpperCase() + "'" : "null";

            // Construir la linea INSERT exacta solicitada
            String linea = String.format(
                "insert into TR_RESUMEN_TRANS_2010 (CNTR_RUT,CNTR_DV,PERIODO_DJ,VALOR_TRANSFERENCIA,TRRT_KEYB,ELVC_SEQ,CNTR_RUT2,CNTR_DV2) Values (%d,'%s',%d,%d,%d,'%s',%s,%s);\n",
                v.getRut(),
                dvNormalizado,
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
    public ByteArrayInputStream generarArchivoTxtBigData(Integer periodo) {
        // Usamos la query filtrada del repositorio
        List<VectorEntity> vectores = vectorRepository.findForBigDataExport(periodo);
        StringBuilder txtBuilder = new StringBuilder();
        
        // Formato fecha actual para la última columna: TIMESTAMP
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS"));

        for (VectorEntity v : vectores) {
        	String dvNormalizado = (v.getDv() != null) ? v.getDv().toUpperCase() : "";
            String valRut2 = (v.getRut2() != null) ? String.valueOf(v.getRut2()) : ""; // En TXT pipe suele ir vacio, no 'null' string
            String valDv2 = (v.getDv2() != null) ? v.getDv2().toUpperCase() : "";

            // Formato: RUT|DV|PERIODO|VECTOR|VALOR|ELVC_SEQ|RUT2|DV2|1.0|TIMESTAMP
            String linea = String.format("%d|%s|%d|%d|%d|%s|%s|%s|1.0|%s\n",
                v.getRut(),
                dvNormalizado,
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
    
    // Generar CSV de Modificaciones 599 
    public ByteArrayInputStream generarReporteModificaciones599(Integer periodo) {
        List<VectorEntity> modificaciones = vectorRepository.findModificacionesPendientes(periodo);
        
        StringBuilder sb = new StringBuilder();
        // Cabecera CSV
        sb.append("RUT;DV;VECTOR;VALOR;INTENCION\n");
        
        for (VectorEntity v : modificaciones) {
        	
        	String dvNormalizado = (v.getDv() != null) ? v.getDv().toUpperCase() : "";
            sb.append(v.getRut()).append(";")  
              .append(dvNormalizado).append(";")     
              .append(v.getPeriodo()).append(";")   
              .append(v.getVector()).append(";")    
              .append(v.getValor()).append(";")     
              .append(v.getIntencionCarga()).append("\\n");
        
        }
        
        return new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    // Marcar como Enviados 
    @Transactional
    public void marcarModificacionesComoEnviadas(Integer periodo) {
        vectorRepository.marcarModificacionesComoProcesadas(periodo);
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
        dto.setIntencionCarga(e.getIntencionCarga());
        dto.setProcesado(e.getProcesado());
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
        e.setIntencionCarga(d.getIntencionCarga() != null ? d.getIntencionCarga() : "INSERT");
        // Al crear, siempre nace como NO procesado
        e.setProcesado(false);
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
        //Validar unicidad (VectorID + Periodo)
        if (catVectorRepository.existsByVectorIdAndPeriodo(dto.getVectorId(), dto.getPeriodo())) {
            throw new RuntimeException("El vector " + dto.getVectorId() + " ya existe en el periodo " + dto.getPeriodo());
        }

        CatVectorEntity entity = new CatVectorEntity();
        entity.setVectorId(dto.getVectorId());
        entity.setPeriodo(dto.getPeriodo());
        entity.setNombre(dto.getNombre());
        entity.setTipoTecnologia(dto.getTipoTecnologia());
        entity.setEstado(true); // Nace activo

        //Logica de Version de Ingreso (Manual o Default)
        String codigoVer = (dto.getVersionIngreso() != null && !dto.getVersionIngreso().isEmpty()) 
                           ? dto.getVersionIngreso() 
                           : "1.0"; 
        
        CatVersionEntity versionObj = resolverVersion(dto.getPeriodo(), codigoVer, true);
        entity.setVersionIngreso(versionObj);

        CatVectorEntity guardado = catVectorRepository.save(entity);
        return mapToCatalogoDTO(guardado);
    }

    // GESTION DE CATALOGO: ACTUALIZAR
    @Transactional
    public CatVectorDTO actualizarVectorCatalogo(Long id, CatVectorDTO dto) {
        CatVectorEntity entity = catVectorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vector no encontrado con ID: " + id));

        // Actualizamos campos básicos
        entity.setNombre(dto.getNombre());
        entity.setTipoTecnologia(dto.getTipoTecnologia());

        // Si el usuario quiere corregir la versión de ingreso
        if (dto.getVersionIngreso() != null && !dto.getVersionIngreso().isEmpty()) {
        	CatVersionEntity nuevaVersion = resolverVersion(entity.getPeriodo(), dto.getVersionIngreso(), true);
            entity.setVersionIngreso(nuevaVersion);
        }

        CatVectorEntity actualizado = catVectorRepository.save(entity);
        return mapToCatalogoDTO(actualizado);
    }
    
    @Transactional
    public void darBajaVector(Long id, String codigoVersionRetiro) {
        CatVectorEntity entity = catVectorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vector no encontrado con ID: " + id));

        //Validar que la versión de retiro exista en el periodo del vector
        CatVersionEntity versionRetiro = resolverVersion(entity.getPeriodo(), codigoVersionRetiro, true);

        //Aplicar Baja Lógica
        entity.setEstado(false);
        entity.setVersionRetiro(versionRetiro);

        catVectorRepository.save(entity);
    }
    
    private CatVectorDTO mapToCatalogoDTO(CatVectorEntity e) {
        CatVectorDTO dto = new CatVectorDTO();
        dto.setId(e.getId());
        dto.setVectorId(e.getVectorId());
        dto.setPeriodo(e.getPeriodo());
        dto.setNombre(e.getNombre());
        dto.setTipoTecnologia(e.getTipoTecnologia());
        dto.setEstado(e.getEstado());
        if (e.getVersionIngreso() != null) dto.setVersionIngreso(e.getVersionIngreso().getCodigoVersion());
        if (e.getVersionRetiro() != null) dto.setVersionRetiro(e.getVersionRetiro().getCodigoVersion());
        return dto;
    }
    
    private CatVersionEntity resolverVersion(Integer periodo, String codigoVersion, boolean crearSiNoExiste) {
        // 1. Buscamos la versión
        CatVersionEntity version = catVersionRepository.findByPeriodoAndCodigoVersion(periodo, codigoVersion);
        
        // 2. Si existe, la devolvemos
        if (version != null) {
            return version;
        }

        // 3. Si no existe...
        if (crearSiNoExiste) {
            // ... La creamos al vuelo
            CatVersionEntity nueva = new CatVersionEntity();
            nueva.setPeriodo(periodo);
            nueva.setCodigoVersion(codigoVersion);
            nueva.setDescripcion("Generada automáticamente por baja de vector");
            nueva.setFechaRegistro(LocalDateTime.now());
            
            return catVersionRepository.save(nueva);
        } else {
            // ... O lanzamos error (comportamiento estricto antiguo)
            throw new RuntimeException("Error: La versión normativa '" + codigoVersion + "' no existe para el periodo " + periodo);
        }
    }
}