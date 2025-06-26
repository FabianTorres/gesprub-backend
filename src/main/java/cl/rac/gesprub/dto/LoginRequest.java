package cl.rac.gesprub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Anotación de Lombok que genera getters, setters, toString, etc.
@Builder // Patrón de diseño para construir objetos de forma fluida.
@AllArgsConstructor // Genera un constructor con todos los argumentos.
@NoArgsConstructor // Genera un constructor sin argumentos.
public class LoginRequest {

    private String username;
    private String password;

}