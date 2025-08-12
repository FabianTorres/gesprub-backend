package cl.rac.gesprub.Entidad;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "criticidad")
public class Criticidad {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id_criticidad;
	
	private String nombre_criticidad;
	
	private int activo;

	public Long getId_criticidad() {
		return id_criticidad;
	}

	public void setId_criticidad(Long id_criticidad) {
		this.id_criticidad = id_criticidad;
	}

	public String getNombre_criticidad() {
		return nombre_criticidad;
	}

	public void setNombre_criticidad(String nombre_criticidad) {
		this.nombre_criticidad = nombre_criticidad;
	}

	public int getActivo() {
		return activo;
	}

	public void setActivo(int activo) {
		this.activo = activo;
	}
	
	
	
}
