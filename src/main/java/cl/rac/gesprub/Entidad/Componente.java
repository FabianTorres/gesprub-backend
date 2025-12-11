package cl.rac.gesprub.Entidad;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "componente")
public class Componente {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id_componente;
	
	private String nombre_componente;
	
	private int hito_componente;
	
	private LocalDate fecha_limite;
	
	private int activo;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_proyecto")
	private Proyecto proyecto;
	
	@JsonProperty("id_ambito")
	@Column(name = "id_ambito")
	private int idAmbito;
	
	private String nombre_corto;
	
	
	

	public Long getId_componente() {
		return id_componente;
	}

	public void setId_componente(Long id_componente) {
		this.id_componente = id_componente;
	}

	public String getNombre_componente() {
		return nombre_componente;
	}

	public void setNombre_componente(String nombre_componente) {
		this.nombre_componente = nombre_componente;
	}

	public int getHito_componente() {
		return hito_componente;
	}

	public void setHito_componente(int hito_componente) {
		this.hito_componente = hito_componente;
	}

	public LocalDate getFecha_limite() {
		return fecha_limite;
	}

	public void setFecha_limite(LocalDate fecha_limite) {
		this.fecha_limite = fecha_limite;
	}

	public int getActivo() {
		return activo;
	}

	public void setActivo(int activo) {
		this.activo = activo;
	}
	
	public Proyecto getProyecto() {
		return proyecto;
	}

	public void setProyecto(Proyecto proyecto) {
		this.proyecto = proyecto;
	}

	public int getIdAmbito() {
		return idAmbito;
	}

	public void setIdAmbito(int idAmbito) {
		this.idAmbito = idAmbito;
	}
	
	public String getNombre_corto() {
		return nombre_corto;
	}

	public void setNombre_corto(String nombre_corto) {
		this.nombre_corto = nombre_corto;
	}
	
	
	

}
