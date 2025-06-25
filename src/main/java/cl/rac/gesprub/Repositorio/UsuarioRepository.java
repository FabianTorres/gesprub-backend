package cl.rac.gesprub.Repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.rac.gesprub.Entidad.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
	
	// --- AÑADIR ESTE MÉTODO ---
    // Permite buscar un usuario por la columna 'nombre_usuario'
    // Devuelve un Optional para manejar de forma segura el caso de que no se encuentre.
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

}
