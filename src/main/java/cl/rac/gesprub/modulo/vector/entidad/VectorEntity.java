package cl.rac.gesprub.modulo.vector.entidad;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TR_RESUMEN_TRANS_2010")
@Getter
@Setter
public class VectorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CNTR_RUT", nullable = false)
    private Long rut;

    @Column(name = "CNTR_DV", nullable = false, length = 1)
    private String dv;

    @Column(name = "PERIODO_DJ", nullable = false)
    private Integer periodo;

    @Column(name = "VALOR_TRANSFERENCIA", nullable = false)
    private Long valor;

    @Column(name = "TRRT_KEYB", nullable = false)
    private Integer vector;

    @Column(name = "ELVC_SEQ")
    private String elvcSeq = "NOMCES"; // Valor por defecto en Java tambien

    @Column(name = "CNTR_RUT2", nullable = true)
    private Long rut2;

    @Column(name = "CNTR_DV2", nullable = true, length = 1)
    private String dv2;
    
    @Column(name = "USUARIO_MODIFICACION")
    private String usuarioModificacion;

    @Column(name = "FECHA_MODIFICACION")
    private LocalDateTime fechaModificacion;
    
    @PrePersist
    @PreUpdate
    public void prePersist() {
        this.fechaModificacion = LocalDateTime.now();
    }
}