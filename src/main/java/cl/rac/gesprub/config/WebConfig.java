package cl.rac.gesprub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Aplica la configuración a todos los endpoints bajo /api/
                .allowedOrigins("http://localhost:4200") // Permite peticiones SÓLO desde esta URL
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Permite estos métodos HTTP
                .allowedHeaders("*") // Permite todas las cabeceras
                .allowCredentials(true); // Permite el envío de credenciales (cookies, etc.)
    }
}