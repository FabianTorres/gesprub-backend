package cl.rac.gesprub.modulo.vector.entidad;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "TR_RESUMEN_TRANS_LOG")
@Getter
@Setter
public class VectorLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOG_ID")
    private Long logId;

    @Column(name = "TIPO_ACCION")
    private String tipoAccion;

    @Column(name = "LOG_FECHA")
    private LocalDateTime logFecha;

    @Column(name = "LOG_USUARIO")
    private String logUsuario;

    // Datos copiados
    @Column(name = "ID_ORIGINAL") private Long idOriginal;
    @Column(name = "CNTR_RUT") private Long rut;
    @Column(name = "CNTR_DV") private String dv;
    @Column(name = "PERIODO_DJ") private Integer periodo;
    @Column(name = "VALOR_TRANSFERENCIA") private Long valor;
    @Column(name = "TRRT_KEYB") private Integer vector;
    @Column(name = "ELVC_SEQ") private String elvcSeq;
    @Column(name = "CNTR_RUT2") private Long rut2;
    @Column(name = "CNTR_DV2") private String dv2;

    // Constructor helper para copiar datos rápidamente
    public VectorLogEntity() {}
    
    public VectorLogEntity(VectorEntity e, String accion, String usuario) {
        this.tipoAccion = accion;
        this.logUsuario = usuario;
        this.logFecha = LocalDateTime.now();
        
        this.idOriginal = e.getId();
        this.rut = e.getRut();
        this.dv = e.getDv();
        this.periodo = e.getPeriodo();
        this.valor = e.getValor();
        this.vector = e.getVector();
        this.elvcSeq = e.getElvcSeq();
        this.rut2 = e.getRut2();
        this.dv2 = e.getDv2();
    }
}