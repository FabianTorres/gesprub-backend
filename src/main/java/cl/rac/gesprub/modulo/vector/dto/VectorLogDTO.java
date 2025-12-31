package cl.rac.gesprub.modulo.vector.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class VectorLogDTO {
    // Metadatos del Log
    private Long logId;
    private String tipoAccion;
    private LocalDateTime logFecha;
    private String logUsuario;

    // Snapshot de los datos en ese momento
    private Long idOriginal;
    private Long rut;
    private String dv;
    private Integer periodo;
    private Long valor;
    private Integer vector;
    private String elvc_seq;
    private Long rut2;
    private String dv2;
}