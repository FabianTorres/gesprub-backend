package cl.rac.gesprub.Entidad;

import java.sql.Date;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario")
public class Usuario {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_usuario") // Buena práctica ser explícito
	private Long idUsuario;
	
	@Column(name = "nombre_usuario")
	private String nombreUsuario;
	
	@Column(name = "rol_usuario")
	private String rolUsuario;
	
	private int activo;
	
	private String correo;
	
	@Column(name = "fecha_creacion")
	private Date fechaCreacion;
	
	@Column(name = "ultimo_login")
	private Timestamp ultimoLogin;
	
	

	public Long getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Long idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	public String getRolUsuario() {
		return rolUsuario;
	}

	public void setRolUsuario(String rolUsuario) {
		this.rolUsuario = rolUsuario;
	}

	public int getActivo() {
		return activo;
	}

	public void setActivo(int activo) {
		this.activo = activo;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public Timestamp getUltimoLogin() {
		return ultimoLogin;
	}

	public void setUltimoLogin(Timestamp ultimoLogin) {
		this.ultimoLogin = ultimoLogin;
	}
	
	
	

}
