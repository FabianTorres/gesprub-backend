package cl.rac.gesprub.dto;

import lombok.Getter;
import lombok.Setter;
import java.sql.Date;
import java.sql.Timestamp;

import cl.rac.gesprub.Entidad.Usuario;

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
    
    // Constructor vacío (necesario para new UsuarioDTO())
    public UsuarioDTO() {
    }

    
    // Este constructor facilita la conversión desde la entidad Usuario
    public UsuarioDTO(Usuario usuario) {
        this.idUsuario = usuario.getIdUsuario();
        this.nombreUsuario = usuario.getNombreUsuario();
        this.rolUsuario = usuario.getRolUsuario();
        this.correo = usuario.getCorreo();
        this.activo = usuario.getActivo();
        this.fechaCreacion = usuario.getFechaCreacion();
        this.ultimoLogin = usuario.getUltimoLogin();
    }
}