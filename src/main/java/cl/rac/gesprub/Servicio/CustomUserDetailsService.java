package cl.rac.gesprub.Servicio;


import cl.rac.gesprub.Entidad.Autenticacion;
import cl.rac.gesprub.Entidad.Usuario;
import cl.rac.gesprub.Repositorio.AutenticacionRepository;
import cl.rac.gesprub.Repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService{
		@Autowired
	    private UsuarioRepository usuarioRepository;

	    @Autowired
	    private AutenticacionRepository autenticacionRepository;

	    /**
	     * Este método es llamado por Spring Security durante el proceso de autenticación.
	     * @param username El nombre de usuario que el usuario final envió en el login.
	     * @return Un objeto UserDetails que contiene la información del usuario.
	     * @throws UsernameNotFoundException Si el usuario no se encuentra en la base de datos.
	     */
	    @Override
	    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	        // Paso 1: Buscar el usuario en nuestra tabla 'usuario' usando el método que creamos en el repositorio.
	        Usuario usuario = usuarioRepository.findByNombreUsuario(username)
	                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el nombre: " + username));

	        // Paso 2: Con el objeto 'usuario' encontrado, buscar su contraseña en la tabla 'autenticacion'.
	        Autenticacion autenticacion = autenticacionRepository.findByUsuario(usuario)
	                .orElseThrow(() -> new UsernameNotFoundException("Credenciales no encontradas para el usuario: " + username));

	        // Paso 3: Crear y devolver un objeto 'User' de Spring Security.
	        // Este objeto contiene la información que Spring necesita:
	        // - El nombre de usuario.
	        // - La contraseña HASHEADA desde la base de datos.
	        // - Una lista de roles/autoridades (por ahora, la dejamos vacía).
	        return new User(usuario.getNombreUsuario(), autenticacion.getPassword(), Collections.emptyList());
	    }
}
