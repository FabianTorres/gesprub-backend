package cl.rac.gesprub.Entidad;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
	
	
	

}
