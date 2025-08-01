package cl.rac.gesprub.Entidad;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "caso")
public class Caso {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id_caso;

	private String nombre_caso;

	private String descripcion_caso;

	private int activo;
	
	@JsonProperty("id_componente")
	@Column(name = "id_componente")
	private int idComponente;

	private int id_usuario_creador;

	private String estado_modificacion;
	
	private int anio;
	
	private String version;
	
	private String precondiciones;
	
	private String pasos;
	
	private String resultado_esperado;
	
	private String caso_de_uso;
	
	private String jp_responsable;

	// Getters and setters

	public Long getId_caso() {
		return id_caso;
	}

	public void setId_caso(Long id_caso) {
		this.id_caso = id_caso;
	}

	public String getNombre_caso() {
		return nombre_caso;
	}

	public void setNombre_caso(String nombre_caso) {
		this.nombre_caso = nombre_caso;
	}

	public String getDescripcion_caso() {
		return descripcion_caso;
	}

	public void setDescripcion_caso(String descripcion_caso) {
		this.descripcion_caso = descripcion_caso;
	}

	public int getActivo() {
		return activo;
	}

	public void setActivo(int activo) {
		this.activo = activo;
	}



	public int getIdComponente() {
		return idComponente;
	}

	public void setIdComponente(int idComponente) {
		this.idComponente = idComponente;
	}

	public int getId_usuario_creador() {
		return id_usuario_creador;
	}

	public void setId_usuario_creador(int id_usuario_creador) {
		this.id_usuario_creador = id_usuario_creador;
	}

	public String getEstado_modificacion() {
		return estado_modificacion;
	}

	public void setEstado_modificacion(String estado_modificacion) {
		this.estado_modificacion = estado_modificacion;
	}

	public int getAnio() {
		return anio;
	}

	public void setAnio(int anio) {
		this.anio = anio;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getPrecondiciones() {
		return precondiciones;
	}

	public void setPrecondiciones(String precondiciones) {
		this.precondiciones = precondiciones;
	}

	public String getPasos() {
		return pasos;
	}

	public void setPasos(String pasos) {
		this.pasos = pasos;
	}

	public String getResultado_esperado() {
		return resultado_esperado;
	}

	public void setResultado_esperado(String resultado_esperado) {
		this.resultado_esperado = resultado_esperado;
	}

	public String getCaso_de_uso() {
		return caso_de_uso;
	}

	public void setCaso_de_uso(String caso_de_uso) {
		this.caso_de_uso = caso_de_uso;
	}

	public String getJp_responsable() {
		return jp_responsable;
	}

	public void setJp_responsable(String jp_responsable) {
		this.jp_responsable = jp_responsable;
	}

}
