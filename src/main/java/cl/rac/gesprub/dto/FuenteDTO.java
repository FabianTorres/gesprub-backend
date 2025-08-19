package cl.rac.gesprub.dto;

import cl.rac.gesprub.Entidad.Fuente;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FuenteDTO {

    private Long id_fuente;
    private String nombre_fuente;
    private int activo;

    
    public FuenteDTO(Fuente entidad) {
        this.id_fuente = entidad.getId_fuente();
        this.nombre_fuente = entidad.getNombre_fuente();
        this.activo = entidad.getActivo();
    }
}