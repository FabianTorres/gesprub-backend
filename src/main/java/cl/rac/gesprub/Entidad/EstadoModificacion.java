package cl.rac.gesprub.Entidad;

import jakarta.persistence.*;

@Entity
@Table(name = "estado_modificacion")
public class EstadoModificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_estado_modificacion;

    private String nombre;
    private int activo;

    // --- Getters y Setters ---



    public String getNombre() {
        return nombre;
    }

    public Long getId_estado_modificacion() {
		return id_estado_modificacion;
	}

	public void setId_estado_modificacion(Long id_estado_modificacion) {
		this.id_estado_modificacion = id_estado_modificacion;
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