package cl.rac.gesprub.modulo.vector.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
public class CatVersionDTO {
    private Long id;
    private Integer periodo;
    private String codigoVersion;
    private LocalDateTime fechaRegistro;
    private String descripcion;
}