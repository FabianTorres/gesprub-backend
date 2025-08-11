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
    private String url_archivo;
    
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

	public String getUrl_archivo() {
		return url_archivo;
	}

	public void setUrl_archivo(String url_archivo) {
		this.url_archivo = url_archivo;
	}

	public Evidencia getEvidencia() {
		return evidencia;
	}

	public void setEvidencia(Evidencia evidencia) {
		this.evidencia = evidencia;
	}
}