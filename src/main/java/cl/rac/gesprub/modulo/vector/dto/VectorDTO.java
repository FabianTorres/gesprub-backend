package cl.rac.gesprub.modulo.vector.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VectorDTO {
    private Long id;
    private Long rut;
    private String dv;
    private Integer periodo;
    private Long valor;
    private Integer vector; // TRRT_KEYB
    private String elvc_seq;
    private Long rut2;
    private String dv2;
    private String usuarioResponsable;
    
    private String usuarioModificacion;
    private LocalDateTime fechaModificacion;
    
    private String intencionCarga; // "INSERT" o "UPDATE"
    private Boolean procesado;
    
    
    public String getIntencionCarga() { return intencionCarga; }
    public void setIntencionCarga(String intencionCarga) { this.intencionCarga = intencionCarga; }
    
    public Boolean getProcesado() { return procesado; }
    public void setProcesado(Boolean procesado) { this.procesado = procesado; }
}