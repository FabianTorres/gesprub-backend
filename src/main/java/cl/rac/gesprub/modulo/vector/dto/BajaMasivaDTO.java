package cl.rac.gesprub.modulo.vector.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter @Setter
public class BajaMasivaDTO {
    private Long versionId; // La versión normativa que respalda la baja
    private List<Integer> vectorIds; // IDs de negocio (ej: 381, 385) a dar de baja
}