package cl.rac.gesprub.Servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.rac.gesprub.Entidad.Usuario;
import cl.rac.gesprub.Repositorio.UsuarioRepository;

@Service
public class UsuarioService {
	@Autowired
    private UsuarioRepository usuarioRepository;
	
	public Usuario createUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario getUsuarioById(Long id_usuario) {
        return usuarioRepository.findById(id_usuario).orElse(null);
    }

    public Usuario updateUsuario(Long id_usuario, Usuario usuario) {
    	usuario.setId_usuario(id_usuario);
        return usuarioRepository.save(usuario);
    }

    public void deleteUsuario(Long id_usuario) {
    	usuarioRepository.deleteById(id_usuario);
    }

}
