package cl.rac.gesprub.Entidad;

import java.sql.Date;
import java.sql.Timestamp;

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
	private Long id_usuario;
	
	private String nombre_usuario;
	
	private String rol_usuario;
	
	private int activo;
	
	private String correo;
	
	private Date fecha_creacion;
	
	private Timestamp ultimo_login;
	
	

	public Long getId_usuario() {
		return id_usuario;
	}

	public void setId_usuario(Long id_usuario) {
		this.id_usuario = id_usuario;
	}

	public String getNombre_usuario() {
		return nombre_usuario;
	}

	public void setNombre_usuario(String nombre_usuario) {
		this.nombre_usuario = nombre_usuario;
	}

	public String getRol_usuario() {
		return rol_usuario;
	}

	public void setRol_usuario(String rol_usuario) {
		this.rol_usuario = rol_usuario;
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

	public Date getFecha_creacion() {
		return fecha_creacion;
	}

	public void setFecha_creacion(Date fecha_creacion) {
		this.fecha_creacion = fecha_creacion;
	}

	public Timestamp getUltimo_login() {
		return ultimo_login;
	}

	public void setUltimo_login(Timestamp ultimo_login) {
		this.ultimo_login = ultimo_login;
	}
	
	
	

}
