package cl.rac.gesprub.Entidad;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ambito")
public class Ambito {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id_ambito;
	
	private String nombre_ambito;
	
	private int activo;

	public Long getId_ambito() {
		return id_ambito;
	}

	public void setId_ambito(Long id_ambito) {
		this.id_ambito = id_ambito;
	}

	public String getNombre_ambito() {
		return nombre_ambito;
	}

	public void setNombre_ambito(String nombre_ambito) {
		this.nombre_ambito = nombre_ambito;
	}

	public int getActivo() {
		return activo;
	}

	public void setActivo(int activo) {
		this.activo = activo;
	}
	
	
	

}
