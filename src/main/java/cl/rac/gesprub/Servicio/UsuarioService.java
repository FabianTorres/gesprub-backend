package cl.rac.gesprub.Servicio;

import java.util.List;

import org.springframework.stereotype.Service;
import cl.rac.gesprub.dto.RegistroRequest; 
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import cl.rac.gesprub.Entidad.Autenticacion;
import cl.rac.gesprub.Entidad.Usuario;
import cl.rac.gesprub.Repositorio.AutenticacionRepository;
import cl.rac.gesprub.Repositorio.UsuarioRepository;
import cl.rac.gesprub.dto.UsuarioDTO;

@Service
@RequiredArgsConstructor
public class UsuarioService {
	
	// Inyectamos todas las herramientas que necesitamos
    private final UsuarioRepository usuarioRepository;
    private final AutenticacionRepository autenticacionRepository;
    private final PasswordEncoder passwordEncoder;
	
    @Transactional 
	public Usuario createUsuario(RegistroRequest request) {
    	
    	if (usuarioRepository.findByNombreUsuario(request.getNombreUsuario()).isPresent()) {
            
            throw new IllegalArgumentException("El nombre de usuario '" + request.getNombreUsuario() + "' ya está en uso.");
        }
        
    	
    	
		//Crear y poblar la entidad Usuario
		Usuario nuevoUsuario = new Usuario();
		nuevoUsuario.setNombreUsuario(request.getNombreUsuario());
		nuevoUsuario.setCorreo(request.getCorreo());
		nuevoUsuario.setRolUsuario(request.getRolUsuario()); // Asignamos el rol
		nuevoUsuario.setActivo(1); // Activamos el usuario por defecto
		
		// Guardar el nuevo usuario para obtener su ID generado
		Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);
		
		// Crear y poblar la entidad Autenticacion
		Autenticacion nuevaAutenticacion = new Autenticacion();
		// Ciframos la contraseña que viene del request ANTES de guardarla
		nuevaAutenticacion.setPassword(passwordEncoder.encode(request.getPassword()));
		// Asociamos esta autenticación con el usuario que acabamos de guardar.
		nuevaAutenticacion.setUsuario(usuarioGuardado);
		
		// Guardar la entidad de autenticación
		autenticacionRepository.save(nuevaAutenticacion);
		
		// Devolvemos el usuario que se creó
		return usuarioGuardado;
	}
    
    
    /**
     * Busca un usuario por su nombre de usuario y lo convierte a un DTO.
     * @param nombreUsuario El nombre del usuario a buscar.
     * @return Un UsuarioDTO con la información pública del usuario.
     */
    public UsuarioDTO getUsuarioByNombreUsuario(String nombreUsuario) {
        // 1. Buscamos la entidad Usuario en la base de datos.
        Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró un usuario con el nombre: " + nombreUsuario));

        // 2. Convertimos la entidad a un DTO para devolver solo los datos necesarios.
        return convertirADto(usuario);
    }
    
    private UsuarioDTO convertirADto(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setNombreUsuario(usuario.getNombreUsuario());
        dto.setRolUsuario(usuario.getRolUsuario());
        dto.setCorreo(usuario.getCorreo());
        dto.setActivo(usuario.getActivo());
        dto.setFechaCreacion(usuario.getFechaCreacion());
        return dto;
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
