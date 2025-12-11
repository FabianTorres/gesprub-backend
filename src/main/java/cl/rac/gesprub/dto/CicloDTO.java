package cl.rac.gesprub.dto;

import cl.rac.gesprub.Entidad.Ciclo;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CicloDTO {
    private Integer idCiclo;
    private String jiraKey;
    private String nombre;
    private String descripcion;
    private LocalDate fechaLiberacion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaCierre;
    private Integer idUsuarioCreador;
    private String nombreUsuarioCreador;
    private Integer idUsuarioCierre;
    private String nombreUsuarioCierre;
    private Integer activo;
    
    private Long idProyecto;
    private String nombreProyecto;

    // Campos de reporte (se poblarán en el servicio)
    private Integer totalCasosAsignados = 0;
    private Integer casosCertificados = 0; // Casos con última evidencia OK
    private Integer casosError = 0;      // Casos con última evidencia NK
    private Integer casosSinEjecutar = 0; // Casos sin evidencia o con evidencia antigua no OK/NK
    private List<String> componentesInvolucrados = new ArrayList<>();

    public CicloDTO() {}

    public CicloDTO(Ciclo ciclo) {
        this.idCiclo = ciclo.getIdCiclo();
        this.jiraKey = ciclo.getJiraKey();
        this.nombre = ciclo.getNombre();
        this.descripcion = ciclo.getDescripcion();
        this.fechaLiberacion = ciclo.getFechaLiberacion();
        this.fechaCreacion = ciclo.getFechaCreacion();
        this.fechaCierre = ciclo.getFechaCierre();
        this.idUsuarioCreador = ciclo.getIdUsuarioCreador();
        this.idUsuarioCierre = ciclo.getIdUsuarioCierre();
        this.activo = ciclo.getActivo();
        this.idProyecto = ciclo.getIdProyecto();
        
        
        if (ciclo.getProyecto() != null) {
            this.nombreProyecto = ciclo.getProyecto().getNombre_proyecto();
        }
        
        if (ciclo.getUsuarioCreador() != null) {
            this.nombreUsuarioCreador = ciclo.getUsuarioCreador().getNombreUsuario();
        }
        if (ciclo.getUsuarioCierre() != null) {
            this.nombreUsuarioCierre = ciclo.getUsuarioCierre().getNombreUsuario();
        }
    }
}