package cl.rac.gesprub.Entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "estado_evidencia")
public class EstadoEvidencia {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_estado_evidencia;
	
	@Column(name = "nombre_estado_evidencia")
    private String nombre;
    private int activo;
	public Long getId_estado_evidencia() {
		return id_estado_evidencia;
	}
	public void setId_estado_evidencia(Long id_estado_evidencia) {
		this.id_estado_evidencia = id_estado_evidencia;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public int getActivo() {
		return activo;
	}
	public void setActivo(int activo) {
		this.activo = activo;
	}
    
    

}
