package cl.rac.gesprub.Servicio;

import java.util.List;

import org.springframework.stereotype.Service;
import cl.rac.gesprub.dto.RegistroRequest; 
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import cl.rac.gesprub.Entidad.Autenticacion;
import cl.rac.gesprub.Entidad.Usuario;
import cl.rac.gesprub.Repositorio.AutenticacionRepository;
import cl.rac.gesprub.Repositorio.UsuarioRepository;

@Service
@RequiredArgsConstructor
public class UsuarioService {
	
	// Inyectamos todas las herramientas que necesitamos
    private final UsuarioRepository usuarioRepository;
    private final AutenticacionRepository autenticacionRepository;
    private final PasswordEncoder passwordEncoder;
	
    @Transactional // ¡CLAVE! Esto hace que todo el método sea una única transacción.
    // Si algo falla (ej. al guardar la contraseña), se deshace la creación del usuario.
	public Usuario createUsuario(RegistroRequest request) {
		// 1. Crear y poblar la entidad Usuario
		Usuario nuevoUsuario = new Usuario();
		nuevoUsuario.setNombreUsuario(request.getNombreUsuario());
		nuevoUsuario.setCorreo(request.getCorreo());
		nuevoUsuario.setRolUsuario(request.getRolUsuario()); // Asignamos el rol
		nuevoUsuario.setActivo(1); // Activamos el usuario por defecto
		
		// 2. Guardar el nuevo usuario para obtener su ID generado
		Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);
		
		// 3. Crear y poblar la entidad Autenticacion
		Autenticacion nuevaAutenticacion = new Autenticacion();
		// Ciframos la contraseña que viene del request ANTES de guardarla
		nuevaAutenticacion.setPassword(passwordEncoder.encode(request.getPassword()));
		// ¡El enlace! Asociamos esta autenticación con el usuario que acabamos de guardar.
		nuevaAutenticacion.setUsuario(usuarioGuardado);
		
		// 4. Guardar la entidad de autenticación
		autenticacionRepository.save(nuevaAutenticacion);
		
		// 5. Devolvemos el usuario que se creó
		return usuarioGuardado;
	}

    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario getUsuarioById(Long id_usuario) {
        return usuarioRepository.findById(id_usuario).orElse(null);
    }

    public Usuario updateUsuario(Long id_usuario, Usuario usuario) {
    	usuario.setIdUsuario(id_usuario);
        return usuarioRepository.save(usuario);
    }

    public void deleteUsuario(Long id_usuario) {
    	usuarioRepository.deleteById(id_usuario);
    }

}
