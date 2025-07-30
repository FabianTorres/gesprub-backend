package cl.rac.gesprub.Controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.rac.gesprub.Entidad.Usuario;
import cl.rac.gesprub.Servicio.UsuarioService;
import cl.rac.gesprub.dto.RegistroRequest;
import cl.rac.gesprub.dto.UsuarioDTO;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {
	
	@Autowired
    private UsuarioService usuarioService;
	
	@PostMapping

    public Usuario createUsuario(@RequestBody RegistroRequest request) { 
        return usuarioService.createUsuario(request);
    }
	
	@GetMapping
    public List<Usuario> getAllUsuarios() {
        return usuarioService.getAllUsuarios();
    }
	
	@GetMapping("/nombreusuario/{username}")
    public ResponseEntity<UsuarioDTO> getUsuarioByUsername(@PathVariable String username) {
        UsuarioDTO usuarioDto = usuarioService.getUsuarioByNombreUsuario(username);
        return ResponseEntity.ok(usuarioDto);
    }
	
	@GetMapping("/{id}")
    public UsuarioDTO getUsuarioById(@PathVariable Long id) {
        return usuarioService.getUsuarioByIdComoDto(id);
    }
	
	@PutMapping("/{id}")
    public Usuario updateUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        return usuarioService.updateUsuario(id, usuario);
    }
	
	@DeleteMapping("/{id}")
    public void deleteUsuario(@PathVariable Long id) {
		usuarioService.deleteUsuario(id);
    }

}
