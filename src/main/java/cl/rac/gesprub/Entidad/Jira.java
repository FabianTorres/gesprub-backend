package cl.rac.gesprub.Entidad;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "jira")
public class Jira {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id_jira;
	
	private String clave_jira;
	
	private String descripcion;
	
	private Date fecha_creacion;
	
	private Date fecha_finalizacion;
	
	private String responsable;
	
	private String estado;
	
	private String criticidad;

	public Long getId_jira() {
		return id_jira;
	}

	public void setId_jira(Long id_jira) {
		this.id_jira = id_jira;
	}

	public String getClave_jira() {
		return clave_jira;
	}

	public void setClave_jira(String clave_jira) {
		this.clave_jira = clave_jira;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Date getFecha_creacion() {
		return fecha_creacion;
	}

	public void setFecha_creacion(Date fecha_creacion) {
		this.fecha_creacion = fecha_creacion;
	}

	public Date getFecha_finalizacion() {
		return fecha_finalizacion;
	}

	public void setFecha_finalizacion(Date fecha_finalizacion) {
		this.fecha_finalizacion = fecha_finalizacion;
	}

	public String getResponsable() {
		return responsable;
	}

	public void setResponsable(String responsable) {
		this.responsable = responsable;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getCriticidad() {
		return criticidad;
	}

	public void setCriticidad(String criticidad) {
		this.criticidad = criticidad;
	}
	
	

}
