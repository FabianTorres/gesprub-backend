package cl.rac.gesprub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistroRequest {
    private String nombreUsuario;
    private String correo;
    private String password;
    private String rolUsuario; // Podemos añadir otros campos que vengan del formulario
}
