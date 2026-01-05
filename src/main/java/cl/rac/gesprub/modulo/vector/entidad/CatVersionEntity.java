package cl.rac.gesprub.modulo.vector.entidad;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "CAT_VERSIONES_DOC")
@Getter @Setter
public class CatVersionEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) private Integer periodo;
    @Column(name = "CODIGO_VERSION", nullable = false) private String codigoVersion;
    @Column(name = "FECHA_REGISTRO") private LocalDateTime fechaRegistro = LocalDateTime.now();
    private String descripcion;
}