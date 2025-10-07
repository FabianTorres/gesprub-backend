package cl.rac.gesprub.dto;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ProcesarLoteRequestDTO {
	@Valid
    private List<CasoCrearLoteDTO> casosParaCrear;
    
    @Valid
    private List<CasoActualizarLoteDTO> casosParaActualizar;
}