package cl.rac.gesprub.Entidad;

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
@Table(name = "ciclos_casos")
@Getter
@Setter
public class CicloCaso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ciclo_caso")
    private Integer idCicloCaso;

    @Column(name = "id_ciclo", nullable = false)
    private Integer idCiclo;

    @Column(name = "id_caso", nullable = false)
    private Long idCaso; 

    // Relaciones para navegación
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciclo", insertable = false, updatable = false)
    private Ciclo ciclo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_caso", insertable = false, updatable = false)
    private Caso caso;
}