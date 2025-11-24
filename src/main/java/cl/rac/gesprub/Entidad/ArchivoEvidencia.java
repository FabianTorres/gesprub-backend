package cl.rac.gesprub.Entidad;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "archivo_evidencia")
public class ArchivoEvidencia {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_archivo;

    private String nombre_archivo;
    
    @Column(name = "ruta_archivo") // Mapeamos a la nueva columna
    private String ruta_archivo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_evidencia")
    @JsonIgnore
    private Evidencia evidencia;

    // --- Getters y Setters ---

	public Long getId_archivo() {
		return id_archivo;
	}

	public void setId_archivo(Long id_archivo) {
		this.id_archivo = id_archivo;
	}

	public String getNombre_archivo() {
		return nombre_archivo;
	}

	public void setNombre_archivo(String nombre_archivo) {
		this.nombre_archivo = nombre_archivo;
	}

	public String getRuta_archivo() {
		return ruta_archivo;
	}

	public void setRuta_archivo(String ruta_archivo) {
		this.ruta_archivo = ruta_archivo;
	}

	public Evidencia getEvidencia() {
		return evidencia;
	}

	public void setEvidencia(Evidencia evidencia) {
		this.evidencia = evidencia;
	}
}