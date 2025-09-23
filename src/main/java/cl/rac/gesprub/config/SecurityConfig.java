package cl.rac.gesprub.config;

import cl.rac.gesprub.Servicio.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration // Le dice a Spring que esta es una clase de configuración
@EnableWebSecurity // Habilita la seguridad web de Spring en nuestro proyecto
@RequiredArgsConstructor // Lombok creará el constructor para inyectar nuestras dependencias final
public class SecurityConfig {

    // Inyectamos las piezas que hemos creado en los pasos anteriores
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Deshabilitar CSRF (Cross-Site Request Forgery) porque usamos JWT, que es inmune a este tipo de ataque.
            .csrf(csrf -> csrf.disable())
            
         // 1. AÑADIR ESTA SECCIÓN COMPLETA PARA CONFIGURAR CORS
            .cors(cors -> cors.configurationSource(request -> {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of(
                		"http://localhost:4200",
                		"http://localhost:8080", 
                		"http://10.32.1.11:8080",
                		"https://nice-coast-0dc38330f.1.azurestaticapps.net",
                		"https://gesprub.cl",
                		"icy-meadow-09dd78d1e.1.azurestaticapps.net"
                		));
                configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                configuration.setAllowedHeaders(List.of("*"));
                // No es estrictamente necesario para este caso, pero es buena práctica
                configuration.setAllowCredentials(true); 
                return configuration;
            }))

            // 2. Definir las reglas de autorización para las peticiones HTTP.
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/autenticacion/login").permitAll() // Permitimos el acceso público a nuestro endpoint de login.
                .requestMatchers(HttpMethod.POST, "/api/usuario").permitAll()      // Puedes añadir aquí otras rutas públicas si las tienes (ej. /api/public/**)
                .requestMatchers(HttpMethod.POST, "/api/proyecto").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/componente").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/caso/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/caso/**").authenticated() 
                .requestMatchers(HttpMethod.PATCH, "/api/evidencia/**").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/api/usuario/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/dashboard").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/caso/**").authenticated() 
                .requestMatchers(HttpMethod.PATCH, "/api/casos/**").authenticated()
                .anyRequest().authenticated() // Para cualquier otra petición, el usuario debe estar autenticado.
            )

            // 3. Configurar la gestión de sesiones.
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Le decimos a Spring que no cree sesiones. Cada petición es independiente.

            // 4. Definir nuestro proveedor de autenticación personalizado.
            .authenticationProvider(authenticationProvider())

            // 5. Añadir nuestro filtro de JWT antes del filtro de autenticación de usuario y contraseña de Spring.
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Define el bean del codificador de contraseñas. Usamos BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Define el proveedor de autenticación (AuthenticationProvider).
     * Es el encargado de conectar nuestro UserDetailsService con el PasswordEncoder.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // Le decimos cuál es nuestro servicio de detalles de usuario.
        authProvider.setPasswordEncoder(passwordEncoder()); // Le decimos cuál es nuestro codificador de contraseñas.
        return authProvider;
    }

    /**
     * Expone el AuthenticationManager como un Bean para que podamos usarlo en nuestro controlador de login.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}