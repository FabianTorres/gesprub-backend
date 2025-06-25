package cl.rac.gesprub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
	
	@SuppressWarnings("removal")
	@Bean // Marca un método que instanciará, configurará e inicializará un nuevo objeto manejado por el IoC container de Spring.
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().permitAll() // Permite que todas las solicitudes HTTP pasen sin autenticación.
            )
            .csrf().disable(); // Deshabilita la protección CSRF. Es común deshabilitarla para APIs RESTful,
                               // especialmente durante el desarrollo, ya que Angular gestionará sus propias protecciones.
        return http.build(); // Construye y devuelve el SecurityFilterChain.
    }

}
