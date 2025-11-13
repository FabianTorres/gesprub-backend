package cl.rac.gesprub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.io.InputStream;

@Getter
@AllArgsConstructor
public class FileDownloadDTO {
    private String nombreOriginal;
    private Long contentLength;
    private InputStream dataStream;
}