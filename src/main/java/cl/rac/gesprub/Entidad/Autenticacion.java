package cl.rac.gesprub.Entidad;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "autenticacion")
public class Autenticacion {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id_autenticacion;
	
	private String password;
	
    // Le dice a JPA que un registro de Autenticacion está asociado a un único registro de Usuario.
    // La columna 'id_usuario' en la tabla 'autenticacion' se usará para esta unión.
    @OneToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    private Usuario usuario;

	public Long getId_autenticacion() {
		return id_autenticacion;
	}

	public void setId_autenticacion(Long id_autenticacion) {
		this.id_autenticacion = id_autenticacion;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	// --- MÉTODOS AÑADIDOS para manejar la relación a través del objeto ---
		public Usuario getUsuario() {
			return usuario;
		}

		public void setUsuario(Usuario usuario) {
			this.usuario = usuario;
		}
	

}
