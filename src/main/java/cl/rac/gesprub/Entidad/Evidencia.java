package cl.rac.gesprub.Entidad;

import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "evidencia")
public class Evidencia {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id_evidencia;
	
	private String descripcion_evidencia;
	
	private String resultado_evidencia;
	
	private Timestamp fecha_evidencia;
	
	private String url_evidencia;
	
	private int id_jira;
	
	private int id_usuario_ejecutante;
	
	private int id_caso;
	
	

	public Long getId_evidencia() {
		return id_evidencia;
	}

	public void setId_evidencia(Long id_evidencia) {
		this.id_evidencia = id_evidencia;
	}

	public String getDescripcion_evidencia() {
		return descripcion_evidencia;
	}

	public void setDescripcion_evidencia(String descripcion_evidencia) {
		this.descripcion_evidencia = descripcion_evidencia;
	}

	public String getResultado_evidencia() {
		return resultado_evidencia;
	}

	public void setResultado_evidencia(String resultado_evidencia) {
		this.resultado_evidencia = resultado_evidencia;
	}

	public Timestamp getFecha_evidencia() {
		return fecha_evidencia;
	}

	public void setFecha_evidencia(Timestamp fecha_evidencia) {
		this.fecha_evidencia = fecha_evidencia;
	}

	public String getUrl_evidencia() {
		return url_evidencia;
	}

	public void setUrl_evidencia(String url_evidencia) {
		this.url_evidencia = url_evidencia;
	}

	public int getId_jira() {
		return id_jira;
	}

	public void setId_jira(int id_jira) {
		this.id_jira = id_jira;
	}

	public int getId_usuario_ejecutante() {
		return id_usuario_ejecutante;
	}

	public void setId_usuario_ejecutante(int id_usuario_ejecutante) {
		this.id_usuario_ejecutante = id_usuario_ejecutante;
	}

	public int getId_caso() {
		return id_caso;
	}

	public void setId_caso(int id_caso) {
		this.id_caso = id_caso;
	}
	
	
	

}
