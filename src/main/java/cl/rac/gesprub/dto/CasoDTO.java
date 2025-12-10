package cl.rac.gesprub.dto;

import cl.rac.gesprub.Entidad.Caso;
import cl.rac.gesprub.Entidad.Evidencia;
import cl.rac.gesprub.Entidad.Usuario;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.sql.Timestamp;
import java.time.Instant;
import cl.rac.gesprub.dto.CicloResumenDTO;

@Getter
@Setter
public class CasoDTO {
    // Copiamos los campos que el frontend necesita de la entidad Caso
    private Long id_caso;
    private String nombre_caso;
    private String descripcion_caso;
    private int activo;
    private Integer num_formulario;
    private String fuente;
    private int id_componente;
    private int id_usuario_creador;
    private int id_estado_modificacion;
    private int anio;
    private String version;
    private String precondiciones;
    private String pasos;
    private String resultado_esperado;
    private String caso_de_uso;
    private String jp_responsable;
    private Set<FuenteDTO> fuentes;
    private Integer idUsuarioAsignado;
    private String estadoKanban;
    private String nombre_componente;
    private Timestamp fechaMovimientoKanban;
    private UsuarioDTO usuarioEjecutante;
    private Integer idCriticidad;
    private Instant fechaUltimaEvidencia;
    private List<CicloResumenDTO> ciclosActivos = new ArrayList<>();
    
    // Un constructor que facilita la conversión desde la entidad
    public CasoDTO(Caso caso) {
        this.id_caso = caso.getId_caso();
        this.nombre_caso = caso.getNombre_caso();
        this.descripcion_caso = caso.getDescripcion_caso();
        this.activo = caso.getActivo();
        this.num_formulario = caso.getNum_formulario();
        this.fuente = caso.getFuente();
        this.id_componente = caso.getIdComponente();
        this.id_usuario_creador = caso.getId_usuario_creador();
        this.id_estado_modificacion = caso.getId_estado_modificacion();
        this.anio = caso.getAnio();
        this.version = caso.getVersion();
        this.precondiciones = caso.getPrecondiciones();
        this.pasos = caso.getPasos();
        this.resultado_esperado = caso.getResultado_esperado();
        this.caso_de_uso = caso.getCaso_de_uso();
        this.jp_responsable = caso.getJp_responsable();
        this.idUsuarioAsignado = caso.getIdUsuarioAsignado();
        this.estadoKanban = caso.getEstadoKanban();
        
        // --- LÓGICA DE CONVERSIÓN PARA LAS FUENTES ---
        if (caso.getFuentes() != null) {
            this.fuentes = caso.getFuentes().stream()
                                .map(FuenteDTO::new) // Convierte cada entidad Fuente a FuenteDTO
                                .collect(Collectors.toSet());
        }
    }
    
    public CasoDTO(Caso caso, String nombreComponente, Evidencia ultimaEvidencia) {
        this(caso, nombreComponente); // Llama al constructor anterior
        
        // Lógica para fechaMovimientoKanban y usuarioEjecutante (ya existente)
        if ("Por Hacer".equals(caso.getEstadoKanban())) {
            this.fechaMovimientoKanban = caso.getFechaAsignacion();
            this.usuarioEjecutante = null;
        } else if ("Completado".equals(caso.getEstadoKanban()) || "Con Error".equals(caso.getEstadoKanban())) {
            if (ultimaEvidencia != null) {
                this.fechaMovimientoKanban = ultimaEvidencia.getFechaEvidencia();
                if (ultimaEvidencia.getUsuarioEjecutante() != null) {
                    this.usuarioEjecutante = new UsuarioDTO(ultimaEvidencia.getUsuarioEjecutante());
                }
            }
        }
        
        // Si existe una última evidencia, extraemos la fecha y la criticidad.
        if (ultimaEvidencia != null) {
            this.idCriticidad = ultimaEvidencia.getId_criticidad();
            if (ultimaEvidencia.getFechaEvidencia() != null) {
                this.fechaUltimaEvidencia = ultimaEvidencia.getFechaEvidencia().toInstant();
            }
        }
    }
    
    public CasoDTO(Caso caso, String nombreComponente) {
        this(caso); // Llama al constructor original para no repetir código
        this.nombre_componente = nombreComponente;
    }
    
    public CasoDTO(Usuario usuario) {
        // Este constructor es solo para que compile el de arriba, puedes ajustarlo si es necesario
    }
}