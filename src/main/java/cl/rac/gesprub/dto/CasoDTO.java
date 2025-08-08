package cl.rac.gesprub.dto;

import cl.rac.gesprub.Entidad.Caso;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CasoDTO {
    // Copiamos los campos que el frontend necesita de la entidad Caso
    private Long id_caso;
    private String nombre_caso;
    private String descripcion_caso;
    private int activo;
    private Integer num_formulario;
    private String fuente;
    private int id_componente;
    private int id_usuario_creador;
    private int id_estado_modificacion;
    private int anio;
    private String version;
    private String precondiciones;
    private String pasos;
    private String resultado_esperado;
    private String caso_de_uso;
    private String jp_responsable;

    // Un constructor que facilita la conversión desde la entidad
    public CasoDTO(Caso caso) {
        this.id_caso = caso.getId_caso();
        this.nombre_caso = caso.getNombre_caso();
        this.descripcion_caso = caso.getDescripcion_caso();
        this.activo = caso.getActivo();
        this.num_formulario = caso.getNum_formulario();
        this.fuente = caso.getFuente();
        this.id_componente = caso.getIdComponente();
        this.id_usuario_creador = caso.getId_usuario_creador();
        this.id_estado_modificacion = caso.getId_estado_modificacion();
        this.anio = caso.getAnio();
        this.version = caso.getVersion();
        this.precondiciones = caso.getPrecondiciones();
        this.pasos = caso.getPasos();
        this.resultado_esperado = caso.getResultado_esperado();
        this.caso_de_uso = caso.getCaso_de_uso();
        this.jp_responsable = caso.getJp_responsable();
    }
}