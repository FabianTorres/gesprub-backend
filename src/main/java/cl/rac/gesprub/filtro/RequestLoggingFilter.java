package cl.rac.gesprub.filtro;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.springframework.context.annotation.Profile;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Order(1) // Le damos una alta prioridad para que se ejecute antes que otros filtros
@Profile("dev")
@Slf4j // Anotación de Lombok para tener el logger 'log'
public class RequestLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        // Usamos un 'wrapper' para poder leer el cuerpo de la petición varias veces
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper((HttpServletRequest) request);

        // Pasamos el request 'envuelto' al resto de la cadena de filtros
        filterChain.doFilter(requestWrapper, response);

        // Después de que la petición ha sido procesada por el controlador, leemos el cuerpo
        byte[] requestBody = requestWrapper.getContentAsByteArray();

        // Imprimimos la información en la consola
        log.info("========================= INCOMING REQUEST =========================");
        log.info("HTTP Method: {}", requestWrapper.getMethod());
        log.info("Request URI: {}", requestWrapper.getRequestURI());
        if (requestBody.length > 0) {
            log.info("Request Body: {}", new String(requestBody, StandardCharsets.UTF_8));
        }
        log.info("====================================================================");
    }
}