package cl.rac.gesprub.config;

import cl.rac.gesprub.Servicio.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Anotación para que Spring lo detecte como un bean manejado por él.
@RequiredArgsConstructor // Crea un constructor con los campos final (inyección de dependencias).
public class JwtAuthenticationFilter extends OncePerRequestFilter{
	  	private final JwtService jwtService;
	    private final CustomUserDetailsService userDetailsService;

	    @Override
	    protected void doFilterInternal(
	            @NonNull HttpServletRequest request,
	            @NonNull HttpServletResponse response,
	            @NonNull FilterChain filterChain // Mecanismo para pasar la petición al siguiente filtro.
	    ) throws ServletException, IOException {

	        // 1. Obtener la cabecera "Authorization".
	        final String authHeader = request.getHeader("Authorization");

	        // 2. Si la cabecera es nula o no empieza con "Bearer ", no hacemos nada y pasamos al siguiente filtro.
	        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	            filterChain.doFilter(request, response);
	            return;
	        }

	        // 3. Extraer el token (quitando el prefijo "Bearer ").
	        final String jwt = authHeader.substring(7);

	        // 4. Extraer el nombre de usuario del token usando nuestro JwtService.
	        final String username = jwtService.extractUsername(jwt);

	        // 5. Si obtenemos un nombre de usuario Y el usuario aún no ha sido autenticado en esta petición...
	        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
	            
	            // ...cargamos los detalles del usuario desde la base de datos usando nuestro servicio.
	            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

	            // ...validamos si el token es correcto para este usuario.
	            if (jwtService.isTokenValid(jwt, userDetails)) {
	                
	                // Si el token es válido, creamos un objeto de autenticación para Spring Security.
	                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
	                        userDetails,
	                        null, // Las credenciales (password) son null porque ya nos autenticamos con el token.
	                        userDetails.getAuthorities()
	                );
	                
	                // Adjuntamos detalles de la petición web actual.
	                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

	                // ¡El paso clave! Actualizamos el SecurityContextHolder. 
	                // A partir de este momento, Spring Security considera al usuario como autenticado.
	                SecurityContextHolder.getContext().setAuthentication(authToken);
	            }
	        }
	        
	        // Independientemente de lo que pase, pasamos la petición al siguiente filtro de la cadena.
	        filterChain.doFilter(request, response);
	    }
}
