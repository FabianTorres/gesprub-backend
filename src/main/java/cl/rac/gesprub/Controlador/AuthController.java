package cl.rac.gesprub.Controlador;

import cl.rac.gesprub.dto.AuthResponse;
import cl.rac.gesprub.dto.LoginRequest;
import cl.rac.gesprub.config.JwtService; 
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/autenticacion") // La ruta base
@RequiredArgsConstructor
public class AuthController {

    
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService; // Usamos la interfaz, no la implementación concreta
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        // 1. Autenticar al usuario con el AuthenticationManager de Spring Security.
        //    Usa internamente el CustomUserDetailsService y el PasswordEncoder.
        //    Si las credenciales son incorrectas, se lanzará una excepción y el método terminará.
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );

        // 2. Si la autenticación fue exitosa (es decir, no hubo excepción), cargamos los detalles del usuario.
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        // 3. Generamos el token JWT para este usuario.
        final String jwt = jwtService.generateToken(userDetails);

        // 4. Devolvemos una respuesta 200 OK con el token en el cuerpo.
        return ResponseEntity.ok(new AuthResponse(jwt));
    }
}