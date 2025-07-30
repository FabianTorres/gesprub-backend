package cl.rac.gesprub.dto;

import lombok.Getter;
import lombok.Setter;
import java.sql.Date;
import java.sql.Timestamp;

@Getter
@Setter
public class UsuarioDTO {
    private Long idUsuario;
    private String nombreUsuario;
    private String rolUsuario;
    private String correo;
    private int activo;
    private Date fechaCreacion;
    private Timestamp ultimoLogin;
}