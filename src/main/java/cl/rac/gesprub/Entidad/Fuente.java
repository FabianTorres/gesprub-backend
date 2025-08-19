package cl.rac.gesprub.Entidad;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Entity
@Table(name = "fuente")
@Getter
@Setter
public class Fuente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_fuente;

    @Column(name = "nombre_fuente", unique = true, nullable = false)
    private String nombre_fuente;

    private int activo;

    @ManyToMany(mappedBy = "fuentes")
    @JsonIgnore // Evita problemas de serialización en las respuestas JSON
    private Set<Caso> casos;
}