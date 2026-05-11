package com.example.proyecto_ae.services;

import com.example.proyecto_ae.config.AppConstants;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.openapitools.client.ApiClient;
import org.openapitools.client.api.ClientAuthResourceApi;
import org.openapitools.client.api.ClientSignatureResourceApi;
import org.openapitools.client.api.ValidationResourceApi;
import org.openapitools.client.model.*;

public class ViafirmaService {

    private static final String USERNAME = "upo_practices";
    private static final String PASSWORD = "MSW4zyVXBax3";

    private ApiClient getClient() {
        ApiClient client = new ApiClient();
        client.setUsername(USERNAME);
        client.setPassword(PASSWORD);
        return client;
    }

    public RequestResultDTO requestAuthentication(String callbackUrl) throws Exception {
        ApiClient client = getClient();
        ClientAuthResourceApi api = new ClientAuthResourceApi(client);
        PrepareAuthDTO auth = new PrepareAuthDTO();
        auth.setCallbackURL(callbackUrl);
        auth.setCallbackRedirectURL(callbackUrl);
        return api.requestAuthentication(auth);
    }

    public CertificateResponseDTO getCertificate(String code) throws Exception {
        ApiClient client = getClient();
        ClientAuthResourceApi api = new ClientAuthResourceApi(client);
        return api.getCertificate(code);
    }

    public RequestResultDTO prepareSignature(java.util.List<File> files, String callbackUrl, String description) throws Exception {
        ApiClient client = getClient();
        ClientSignatureResourceApi api = new ClientSignatureResourceApi(client);
        PrepareRequestDTO prepareRequest = new PrepareRequestDTO();
        java.util.List<PrepareSignatureRequestDTO> signatures = new java.util.ArrayList<>();

        for (File file : files) {
            PrepareSignatureRequestDTO signature = new PrepareSignatureRequestDTO();
            byte[] fileContent = Files.readAllBytes(file.toPath());
            String base64File = java.util.Base64.getEncoder().encodeToString(fileContent);
            signature.setFile(base64File);
            signature.setFileName(file.getName());
            
            ConfigSignatureDTO config = new ConfigSignatureDTO();
            config.setPackaging(ConfigSignatureDTO.PackagingEnum.ENVELOPED);
            config.setFileName(file.getName());
            
            if (file.getName().toLowerCase().endsWith(".pdf")) {
                signature.setFileType("pdf");
                config.setSignatureType(ConfigSignatureDTO.SignatureTypeEnum.PADES_B);
                ConfigPadesDTO pades = new ConfigPadesDTO();
                StamperDTO stamper = new StamperDTO();
                stamper.setType(StamperDTO.TypeEnum.QR);
                stamper.setPage(-1);
                stamper.setxAxis(50);
                stamper.setyAxis(750);
                pades.setStamper(stamper);
                config.setPadesConfig(pades);
            } else {
                signature.setFileType("xml");
                config.setSignatureType(ConfigSignatureDTO.SignatureTypeEnum.XADES_B);
            }
            signature.setConfiguration(config);
            signatures.add(signature);
        }

        prepareRequest.setSignatures(signatures);
        prepareRequest.setCount(signatures.size());
        prepareRequest.setCallbackRedirectURL(callbackUrl);
        
        return api.requestSignature(prepareRequest);
    }

    public RequestStatusResponseDTO getSignatureStatus(String token) throws Exception {
        return new ClientSignatureResourceApi(getClient()).getSignatureStatus(token);
    }

    /**
     * Valida un documento firmado enviándolo a la API de validación de Viafirma.
     * @param fileContent Contenido del archivo en bytes.
     * @return Objeto con los resultados de la validación.
     */
    public ValidationFileResponseDTO validateSignature(byte[] fileContent, String filename) throws Exception {
        ApiClient client = getClient();
        ValidationResourceApi api = new ValidationResourceApi(client);
        
        ValidationFileRequestDTO request = new ValidationFileRequestDTO();
        request.setSignedFileBase64(java.util.Base64.getEncoder().encodeToString(fileContent));
        request.setFilename(filename);
        request.setShowExtendedInfo(true);
        
        return api.verifySignature(request);
    }

    /**
     * Descarga blindada usando la infraestructura del propio ApiClient de Viafirma.
     */
    public void downloadSignedPdf(String url, File targetFile) throws Exception {
        ApiClient client = getClient();
        
        // Si el enlace es una redirección o externo, intentamos descarga directa primero
        try {
            downloadWithClient(client, url, targetFile);
        } catch (Exception e) {
            // Fallback: descarga manual básica si falla el cliente (para S3)
            downloadManual(url, targetFile);
        }
    }

    private void downloadWithClient(ApiClient client, String urlString, File targetFile) throws Exception {
        URL url = new URL(urlString);
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        
        // Usar exactamente el mismo formato de Auth que usa la librería
        String auth = USERNAME + ":" + PASSWORD;
        String encodedAuth = java.util.Base64.getEncoder().encodeToString(auth.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));
        conn.setRequestProperty("Authorization", "Basic " + encodedAuth.trim());
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        
        conn.setConnectTimeout(20000);
        conn.setReadTimeout(20000);

        int status = conn.getResponseCode();
        if (status >= 300 && status < 400) {
            String newUrl = conn.getHeaderField("Location");
            downloadManual(newUrl, targetFile);
            return;
        }

        try (InputStream in = conn.getInputStream()) {
            Files.copy(in, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void downloadManual(String urlString, File targetFile) throws Exception {
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new URL(urlString).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setConnectTimeout(20000);
        conn.setReadTimeout(20000);
        try (InputStream in = conn.getInputStream()) {
            Files.copy(in, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
