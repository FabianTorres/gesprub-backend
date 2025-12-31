package cl.rac.gesprub.modulo.vector.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "CAT_VECTORES")
@Getter
@Setter
public class CatVectorEntity {

    @Id
    @Column(name = "VECTOR_ID")
    private Integer vectorId;

    @Column(name = "NOMBRE")
    private String nombre;

    @Column(name = "TIPO_TECNOLOGIA")
    private String tipoTecnologia; // 'BATCH' o 'BIGDATA_INTEGRADO'
}