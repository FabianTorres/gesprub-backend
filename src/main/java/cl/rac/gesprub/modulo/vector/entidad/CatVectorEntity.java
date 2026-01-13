package cl.rac.gesprub.modulo.vector.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "CAT_VECTORES")
@Getter
@Setter
public class CatVectorEntity {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Nueva PK Técnica

    @Column(name = "VECTOR_ID", nullable = false)
    private Integer vectorId;
    
    @Column(nullable = false)
    private Integer periodo;

    @Column(name = "NOMBRE", length = 255)
    private String nombre;

    @Column(name = "TIPO_TECNOLOGIA")
    private String tipoTecnologia; 
    
    @Column(nullable = false)
    private Boolean estado = true;

    // Relaciones con Versiones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VERSION_INGRESO_ID")
    private CatVersionEntity versionIngreso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VERSION_RETIRO_ID")
    private CatVersionEntity versionRetiro;
}