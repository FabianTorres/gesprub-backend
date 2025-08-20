package cl.rac.gesprub.Entidad;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;

@Entity
@Table(name = "evidencia")
public class Evidencia {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id_evidencia;
	
	private String descripcion_evidencia;
	
	@Column(name = "estado_evidencia")
	private String estado_evidencia;
	
	@Column(name = "id_estado_evidencia")
	private int id_estado_evidencia;
	
	@Column(name = "version_ejecucion")
	private String version_ejecucion;
	
	@JsonProperty("fecha_evidencia")
	@Column(name = "fecha_evidencia")
	private Timestamp fechaEvidencia;
	
	private String url_evidencia;
	
	private int id_jira;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_ejecutante")
    private Usuario usuarioEjecutante;
	
	@JsonProperty("id_caso")
	@Column(name = "id_caso")
	private int idCaso;
	
	@Column(name = "criticidad")
	private String criticidad;
	
	@Column(name = "id_criticidad")
	private Integer id_criticidad;
	
	@OneToMany(mappedBy = "evidencia")
    private List<ArchivoEvidencia> archivos;
	
	@Column(name = "rut")
    private String rut;
	
	@Column(name = "activo", columnDefinition = "int default 1")
    private int activo = 1;
	
	

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


	public String getEstado_evidencia() {
		return estado_evidencia;
	}

	public void setEstado_evidencia(String estado_evidencia) {
		this.estado_evidencia = estado_evidencia;
	}

	public String getCriticidad() {
		return criticidad;
	}

	public void setCriticidad(String criticidad) {
		this.criticidad = criticidad;
	}



	public Timestamp getFechaEvidencia() {
		return fechaEvidencia;
	}

	public void setFechaEvidencia(Timestamp fechaEvidencia) {
		this.fechaEvidencia = fechaEvidencia;
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


	public int getIdCaso() {
		return idCaso;
	}

	public void setIdCaso(int idCaso) {
		this.idCaso = idCaso;
	}

	public Usuario getUsuarioEjecutante() {
		return usuarioEjecutante;
	}

	public void setUsuarioEjecutante(Usuario usuarioEjecutante) {
		this.usuarioEjecutante = usuarioEjecutante;
	}

	public String getVersion_ejecucion() {
		return version_ejecucion;
	}

	public void setVersion_ejecucion(String version_ejecucion) {
		this.version_ejecucion = version_ejecucion;
	}

	public List<ArchivoEvidencia> getArchivos() {
		return archivos;
	}

	public void setArchivos(List<ArchivoEvidencia> archivos) {
		this.archivos = archivos;
	}
	
	public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

	public int getId_estado_evidencia() {
		return id_estado_evidencia;
	}

	public void setId_estado_evidencia(int id_estado_evidencia) {
		this.id_estado_evidencia = id_estado_evidencia;
	}

	public Integer getId_criticidad() {
		return id_criticidad;
	}

	public void setId_criticidad(Integer id_criticidad) {
		this.id_criticidad = id_criticidad;
	}
	
	public int getActivo() {
        return activo;
    }

    public void setActivo(int activo) {
        this.activo = activo;
    }

	
	
	

	
	
	

}
