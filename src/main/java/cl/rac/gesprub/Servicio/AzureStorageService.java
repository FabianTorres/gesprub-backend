package cl.rac.gesprub.Servicio;

import cl.rac.gesprub.dto.FileDownloadDTO;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;

@Service
public class AzureStorageService {

    @Value("${azure.storage.connection-string}")
    private String connectionString;

    private BlobServiceClient getBlobServiceClient() {
        return new BlobServiceClientBuilder().connectionString(connectionString).buildClient();
    }

    /**
     * Sube un archivo a una ruta específica (blobName puede contener carpetas).
     */
    public void uploadFile(String containerName, String blobPath, MultipartFile file) throws IOException {
        BlobServiceClient blobServiceClient = getBlobServiceClient();
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        
        if (!containerClient.exists()) {
            containerClient.create();
        }

        // Obtenemos el cliente para la ruta específica (ej: "10/20/30/archivo.png")
        BlobClient blobClient = containerClient.getBlobClient(blobPath);

        try (InputStream inputStream = file.getInputStream()) {
            blobClient.upload(inputStream, file.getSize(), true); // true = sobreescribir si existe
        }
    }
    
    public FileDownloadDTO downloadFile(String containerName, String blobName, String nombreOriginal) {
        BlobServiceClient blobServiceClient = getBlobServiceClient();
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient = containerClient.getBlobClient(blobName);

        if (!blobClient.exists()) {
            throw new RuntimeException("No se encontró el archivo en Azure Storage: " + blobName);
        }

        InputStream dataStream = blobClient.openInputStream();
        long contentLength = blobClient.getProperties().getBlobSize();

        return new FileDownloadDTO(nombreOriginal, contentLength, dataStream);
    }

    // Mantenemos este método por compatibilidad si se usa en otro lado, aunque ahora usamos downloadFile
    public String generateSasUrl(String containerName, String blobName) {
        BlobServiceClient blobServiceClient = getBlobServiceClient();
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient = containerClient.getBlobClient(blobName);

        BlobSasPermission permissions = new BlobSasPermission().setReadPermission(true);
        OffsetDateTime expiryTime = OffsetDateTime.now().plusHours(1);

        String sasToken = blobClient.generateSas(new BlobServiceSasSignatureValues(expiryTime, permissions));
        return String.format("%s?%s", blobClient.getBlobUrl(), sasToken);
    }
}