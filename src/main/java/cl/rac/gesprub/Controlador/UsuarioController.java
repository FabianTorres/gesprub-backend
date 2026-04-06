package cl.rac.gesprub.Controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
import cl.rac.gesprub.dto.CambioPasswordDTO;
import java.util.Map;
import java.util.HashMap;

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
	
	@PatchMapping("/cambiar-password")
    public ResponseEntity<Void> cambiarPassword(@RequestBody CambioPasswordDTO dto) {
        usuarioService.cambiarPassword(dto);
        return ResponseEntity.ok().build(); // Devuelve una respuesta 200 OK sin cuerpo
    }
	
	/**
     * Actualiza la fecha del último login para un usuario específico.
     */
    @PatchMapping("/{id}/ultimo-login")
    public ResponseEntity<Void> updateUltimoLogin(@PathVariable Long id) {
        usuarioService.updateUltimoLogin(id);
        // Devolvemos una respuesta 200 OK sin cuerpo para indicar que la operación fue exitosa.
        return ResponseEntity.ok().build();
    }
    
    /**
     * Endpoint para que un Administrador formatee la contraseña de un usuario.
     */
    @PatchMapping("/{id}/reset-password")
    public ResponseEntity<Map<String, String>> resetPasswordAdministrativo(
            @PathVariable Long id, 
            @RequestBody Map<String, String> body) {
        
        String nuevaPassword = body.get("password");
        
        if (nuevaPassword == null || nuevaPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("La nueva contraseña no puede estar vacía.");
        }
        
        usuarioService.resetPasswordAdministrativo(id, nuevaPassword);
        
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Contraseña restablecida exitosamente para el usuario ID: " + id);
        
        return ResponseEntity.ok(response);
    }

}
