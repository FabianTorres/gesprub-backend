package cl.rac.gesprub.Entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ciclos")
@Getter
@Setter
public class Ciclo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ciclo")
    private Integer idCiclo;

    @Column(name = "jira_key", nullable = false, length = 50)
    private String jiraKey;

    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fecha_liberacion")
    private LocalDate fechaLiberacion;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion; 

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Column(name = "id_usuario_creador", nullable = false)
    private Integer idUsuarioCreador;
    
    // Mapeo lazy al usuario creador (para navegación/DTOs)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_creador", insertable = false, updatable = false)
    private Usuario usuarioCreador;

    @Column(name = "id_usuario_cierre")
    private Integer idUsuarioCierre;
    
    // Mapeo lazy al usuario de cierre (para navegación/DTOs)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_cierre", insertable = false, updatable = false)
    private Usuario usuarioCierre;
    
    @Column(name = "activo", nullable = false)
    private Integer activo = 1; // Default a 1, aunque DDL lo fuerza
    
    @Column(name = "id_proyecto", nullable = false)
    private Long idProyecto;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proyecto", insertable = false, updatable = false)
    private Proyecto proyecto;
}