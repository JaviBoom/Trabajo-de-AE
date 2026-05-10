podriaspackage com.example.proyecto_ae.services;

import com.example.proyecto_ae.config.AppConstants;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.openapitools.client.ApiClient;
import org.openapitools.client.api.ClientAuthResourceApi;
import org.openapitools.client.api.ClientSignatureResourceApi;
import org.openapitools.client.api.UploadResourceApi;
import org.openapitools.client.model.*;

/**
 * Servicio para interactuar con la API de Viafirma
 * Soporta Autenticación y Firma Dual (Batch)
 */
public class ViafirmaService {

    private static final String USERNAME = "upo_practices";
    private static final String PASSWORD = "MSW4zyVXBax3";

    private ApiClient getClient() {
        ApiClient client = new ApiClient();
        client.setUsername(USERNAME);
        client.setPassword(PASSWORD);
        return client;
    }

    // --- MÉTODOS DE AUTENTICACIÓN (Restaurados) ---

    public RequestResultDTO requestAuthentication(String callbackUrl) throws Exception {
        ApiClient client = getClient();
        ClientAuthResourceApi api = new ClientAuthResourceApi(client);
        PrepareAuthDTO auth = new PrepareAuthDTO();
        // Seteamos ambos por seguridad, pero Redirect es el que dispara el salto en el navegador
        auth.setCallbackURL(callbackUrl);
        auth.setCallbackRedirectURL(callbackUrl);
        return api.requestAuthentication(auth);
    }

    public CertificateResponseDTO getCertificate(String code) throws Exception {
        ApiClient client = getClient();
        ClientAuthResourceApi api = new ClientAuthResourceApi(client);
        return api.getCertificate(code);
    }

    // --- MÉTODOS DE FIRMA DUAL / BATCH ---

    public RequestResultDTO prepareSignature(java.util.List<File> files, String callbackUrl, String description)
            throws Exception {
        
        ApiClient client = getClient();
        ClientSignatureResourceApi api = new ClientSignatureResourceApi(client);
        PrepareRequestDTO prepareRequest = new PrepareRequestDTO();
        java.util.List<PrepareSignatureRequestDTO> signatures = new java.util.ArrayList<>();

        String nifFiltro = null;

        for (File file : files) {
            PrepareSignatureRequestDTO signature = new PrepareSignatureRequestDTO();
            byte[] fileContent = Files.readAllBytes(file.toPath());
            String base64File = java.util.Base64.getEncoder().encodeToString(fileContent);
            
            signature.setFile(base64File);
            signature.setFileName(sanitize(file.getName()));
            
            ConfigSignatureDTO config = new ConfigSignatureDTO();
            config.setPackaging(ConfigSignatureDTO.PackagingEnum.ENVELOPED);
            config.setFileName(sanitize(file.getName()));
            config.setValidSignerIds(new java.util.ArrayList<String>());
            config.setSignatureReason(description != null ? description : "Firma de expediente digital");
            
            if (file.getName().toLowerCase().endsWith(".pdf")) {
                signature.setFileType("pdf");
                config.setSignatureType(ConfigSignatureDTO.SignatureTypeEnum.PADES_B);
                
                if (file.getName().contains("_")) {
                    String[] parts = file.getName().split("_");
                    if (parts.length > 1) nifFiltro = parts[1];
                }

                ConfigPadesDTO pades = new ConfigPadesDTO();
                StamperDTO stamper = new StamperDTO();
                stamper.setType(StamperDTO.TypeEnum.QR);
                stamper.setPage(-1); // Ultima pagina
                stamper.setxAxis(50); // Izquierda, dentro de la hoja
                stamper.setyAxis(750); // Abajo del todo (coordenada desde arriba)
                pades.setStamper(stamper);
                config.setPadesConfig(pades);
            } else if (file.getName().toLowerCase().endsWith(".xml")) {
                signature.setFileType("xml");
                config.setSignatureType(ConfigSignatureDTO.SignatureTypeEnum.XADES_B);
            }

            signature.setConfiguration(config);
            signatures.add(signature);
        }

        prepareRequest.setSignatures(signatures);
        prepareRequest.setCount(signatures.size());
        prepareRequest.setCallbackRedirectURL(callbackUrl);

        if (nifFiltro != null) {
            ConfigCertificateRequestDTO certFilter = new ConfigCertificateRequestDTO();
            CertificateFilterDTO nf = new CertificateFilterDTO();
            nf.addFilterValuesItem(nifFiltro);
            certFilter.setNationalIdFilter(nf);
            prepareRequest.setCertificateFilter(certFilter);
        }
        
        return api.requestSignature(prepareRequest);
    }

    public RequestResultDTO requestSignature(File pdfFile, String description, String callbackUrl) throws Exception {
        java.util.List<File> list = new java.util.ArrayList<>();
        list.add(pdfFile);
        return prepareSignature(list, callbackUrl, description);
    }

    public RequestStatusResponseDTO getSignatureStatus(String token) throws Exception {
        return new ClientSignatureResourceApi(getClient()).getSignatureStatus(token);
    }

    public ClientAccessResponseDTO getSignatureAccess(String code) throws Exception {
        return new ClientSignatureResourceApi(getClient()).getSignatureAccess(code);
    }

    public void downloadSignedPdf(String url, File targetFile) throws Exception {
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new URL(url).openConnection();
        
        // Autenticación Básica robusta
        String auth = USERNAME + ":" + PASSWORD;
        String encodedAuth = java.util.Base64.getEncoder().encodeToString(auth.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        
        conn.setRequestProperty("Authorization", "Basic " + encodedAuth);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Java-Viafirma-Client");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(15000);
        
        try (InputStream in = conn.getInputStream()) {
            Files.copy(in, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } finally {
            conn.disconnect();
        }
    }

    private String sanitize(String input) {
        if (input == null) return "documento";
        return input.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
    }
}
