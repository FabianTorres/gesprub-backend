package cl.rac.gesprub.dto;

import cl.rac.gesprub.Entidad.ArchivoEvidencia;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArchivoEvidenciaDTO {

    private Long id_archivo;
    private String nombre_archivo;
    private String url_archivo;

    // Constructor para facilitar la conversión desde la entidad
    public ArchivoEvidenciaDTO(ArchivoEvidencia entidad) {
        this.id_archivo = entidad.getId_archivo();
        this.nombre_archivo = entidad.getNombre_archivo();
        this.url_archivo = entidad.getRuta_archivo();
    }
}