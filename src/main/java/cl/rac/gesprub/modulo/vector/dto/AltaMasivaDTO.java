package cl.rac.gesprub.modulo.vector.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter @Setter
public class AltaMasivaDTO {
    private Long versionId; // La versión normativa que respalda el alta
    private List<CatVectorDTO> vectores; // Lista de vectores a crear
}