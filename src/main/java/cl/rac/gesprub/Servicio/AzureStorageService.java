package cl.rac.gesprub.Servicio;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;

import cl.rac.gesprub.dto.FileDownloadDTO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class AzureStorageService {

    // Inyectamos el connection string desde application.properties
    @Value("${azure.storage.connection-string}")
    private String connectionString;

    private BlobServiceClient getBlobServiceClient() {
        return new BlobServiceClientBuilder().connectionString(connectionString).buildClient();
    }

    public String uploadFile(String containerName, MultipartFile file) throws IOException {
        BlobServiceClient blobServiceClient = getBlobServiceClient();
        
        // Obtenemos (o creamos si no existe) el contenedor.
        // Un contenedor es como una carpeta en la nube.
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        if (!containerClient.exists()) {
            containerClient.create();
        }

        // Creamos un nombre de archivo único para evitar colisiones.
        String blobName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        
        // Obtenemos una referencia al "blob" (el archivo en la nube).
        BlobClient blobClient = containerClient.getBlobClient(blobName);

        // Subimos el archivo.
        try (InputStream inputStream = file.getInputStream()) {
            blobClient.upload(inputStream, file.getSize(), true);
        }
        
        return blobName;
    }
    
    /**
     * Genera una URL de descarga segura y temporal para un blob.
     */
    public String generateSasUrl(String containerName, String blobName) {
        BlobServiceClient blobServiceClient = getBlobServiceClient();
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient = containerClient.getBlobClient(blobName);

        // Definimos los permisos (solo lectura) y la duración del enlace (ej: 1 hora)
        BlobSasPermission permissions = new BlobSasPermission().setReadPermission(true);
        OffsetDateTime expiryTime = OffsetDateTime.now().plusHours(1);

        // Generamos el token SAS
        String sasToken = blobClient.generateSas(new BlobServiceSasSignatureValues(expiryTime, permissions));

        // Devolvemos la URL completa con el token SAS adjunto
        return String.format("%s?%s", blobClient.getBlobUrl(), sasToken);
    }
    
    /**
     * NUEVO MÉTODO
     * Descarga un archivo desde Azure Storage como un flujo de datos.
     */
    public FileDownloadDTO downloadFile(String containerName, String blobName, String nombreOriginal) {
        BlobServiceClient blobServiceClient = getBlobServiceClient();
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient = containerClient.getBlobClient(blobName);

        if (!blobClient.exists()) {
            throw new RuntimeException("No se encontró el archivo en Azure Storage: " + blobName);
        }

        // Obtenemos el flujo de datos y el tamaño del archivo
        InputStream dataStream = blobClient.openInputStream();
        long contentLength = blobClient.getProperties().getBlobSize();

        // Devolvemos un DTO con toda la información necesaria para el streaming
        return new FileDownloadDTO(nombreOriginal, contentLength, dataStream);
    }
}