package com.example.proyecto_ae.services;

import com.example.proyecto_ae.config.AppConstants;
import java.io.File;
import org.openapitools.client.ApiClient;
import org.openapitools.client.api.ClientAuthResourceApi;
import org.openapitools.client.api.ClientSignatureResourceApi;
import org.openapitools.client.api.UploadResourceApi;
import org.openapitools.client.model.CertificateResponseDTO;
import org.openapitools.client.model.FileReferenceResponseDTO;
import org.openapitools.client.model.PrepareAuthDTO;
import org.openapitools.client.model.PrepareRequestDTO;
import org.openapitools.client.model.PrepareSignatureRequestDTO;
import org.openapitools.client.model.RequestResultDTO;
import org.openapitools.client.model.ClientAccessResponseDTO;
import org.openapitools.client.model.RequestStatusResponseDTO;

/**
 * Servicio centralizado para la integración con Viafirma
 * Encapsula toda la lógica de autenticación, firma y obtención de certificados
 */
public class ViafirmaService {

    private static final String USERNAME = AppConstants.VIAFIRMA_USERNAME;
    private static final String PASSWORD = AppConstants.VIAFIRMA_PASSWORD;

    /**
     * Solicita autenticación a Viafirma
     * 
     * @param callbackUrl URL donde Viafirma enviará el código de autenticación
     * @return URL del protocolo de escritorio para redirigir al usuario
     * @throws Exception Si hay error en la comunicación con Viafirma
     */
    public String requestAuthentication(String callbackUrl) throws Exception {
        ApiClient client = new ApiClient();
        client.setUsername(USERNAME);
        client.setPassword(PASSWORD);

        ClientAuthResourceApi api = new ClientAuthResourceApi(client);
        PrepareAuthDTO auth = new PrepareAuthDTO();
        auth.setCallbackURL(callbackUrl);

        RequestResultDTO result = api.requestAuthentication(auth);

        if (result == null || result.getClientAccess() == null) {
            throw new Exception("Respuesta inválida de Viafirma");
        }

        String desktopProtocol = result.getClientAccess().getDesktopProtocol();
        if (desktopProtocol == null || desktopProtocol.isEmpty()) {
            throw new Exception("No se pudo obtener la URL de autenticación");
        }

        return desktopProtocol;
    }

    /**
     * Obtiene el certificado del usuario después de la autenticación
     * 
     * @param code Código de autenticación devuelto por Viafirma
     * @return Datos del certificado del usuario
     * @throws Exception Si hay error en la comunicación con Viafirma
     */
    public CertificateResponseDTO getCertificate(String code) throws Exception {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("El código de autenticación es requerido");
        }

        // Validar formato básico (alfanuméricos y guiones)
        if (!code.matches("^[a-zA-Z0-9\\-]+$")) {
            throw new IllegalArgumentException("Formato de código inválido");
        }

        ApiClient client = new ApiClient();
        client.setUsername(USERNAME);
        client.setPassword(PASSWORD);

        ClientAuthResourceApi api = new ClientAuthResourceApi(client);
        CertificateResponseDTO cert = api.getCertificate(code);

        if (cert == null) {
            throw new Exception("No se pudo obtener el certificado");
        }

        return cert;
    }

    /**
     * Solicita la firma de un documento a Viafirma
     * 
     * @param pdfFile     Archivo PDF a firmar
     * @param description Descripción del documento
     * @param callbackUrl URL donde Viafirma enviará el resultado
     * @return Objeto con la URL de firma o token
     * @throws Exception Si hay error en la comunicación con Viafirma
     */
    public RequestResultDTO requestSignature(File pdfFile, String description, String callbackUrl) throws Exception {
        if (pdfFile == null || !pdfFile.exists() || !pdfFile.isFile()) {
            throw new IllegalArgumentException("El archivo PDF para firmar es requerido");
        }

        ApiClient client = new ApiClient();
        client.setUsername(USERNAME);
        client.setPassword(PASSWORD);

        // 1) Solicitar token de subida y subir el archivo a Viafirma
        UploadResourceApi uploadApi = new UploadResourceApi(client);
        RequestResultDTO uploadLinkResult = uploadApi.requestLink();
        if (uploadLinkResult == null || uploadLinkResult.getCode() == null || uploadLinkResult.getCode().isEmpty()) {
            throw new Exception("No se pudo obtener el token de subida de Viafirma");
        }

        FileReferenceResponseDTO uploadResult = uploadApi.uploadFile(uploadLinkResult.getCode(), pdfFile);
        if (uploadResult == null || uploadResult.getReference() == null || uploadResult.getReference().isEmpty()) {
            throw new Exception("No se pudo subir el PDF a Viafirma");
        }

        // 2) Preparar solicitud de firma usando la referencia de archivo subida
        ClientSignatureResourceApi api = new ClientSignatureResourceApi(client);
        PrepareRequestDTO prepareRequest = new PrepareRequestDTO();
        PrepareSignatureRequestDTO signature = new PrepareSignatureRequestDTO();
        signature.setSourceId(uploadResult.getReference());
        signature.setFileName(pdfFile.getName());
        signature.setFileType("pdf");
        if (description != null && !description.trim().isEmpty()) {
            signature.setRequestCode(description);
        }

        prepareRequest.setCount(1);
        prepareRequest.setCallbackURL(callbackUrl);
        prepareRequest.addSignaturesItem(signature);

        System.out.println("INFO: Solicitando firma para archivo: " + pdfFile.getName());

        RequestResultDTO result = api.requestSignature(prepareRequest);

        if (result == null || result.getClientAccess() == null) {
            throw new Exception("Respuesta inválida de Viafirma para firma");
        }

        return result;
    }

    /**
     * Obtiene el estado de una solicitud de firma
     * 
     * @param token Token de la solicitud
     * @return Estado actual de la firma
     * @throws Exception Si hay error en la comunicación con Viafirma
     */
    public RequestStatusResponseDTO getSignatureStatus(String token) throws Exception {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("El token de firma es requerido");
        }

        ApiClient client = new ApiClient();
        client.setUsername(USERNAME);
        client.setPassword(PASSWORD);

        ClientSignatureResourceApi api = new ClientSignatureResourceApi(client);
        RequestStatusResponseDTO status = api.getSignatureStatus(token);

        if (status == null) {
            throw new Exception("No se pudo obtener el estado de la firma");
        }

        return status;
    }

    /**
     * Obtiene el acceso a la firma (resultado final)
     * 
     * @param code Código devuelto por Viafirma después de la firma
     * @return Datos con la URL del PDF firmado
     * @throws Exception Si hay error en la comunicación con Viafirma
     */
    public ClientAccessResponseDTO getSignatureAccess(String code) throws Exception {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("El código de firma es requerido");
        }

        // Validar formato básico
        if (!code.matches("^[a-zA-Z0-9\\-]+$")) {
            throw new IllegalArgumentException("Formato de código inválido");
        }

        ApiClient client = new ApiClient();
        client.setUsername(USERNAME);
        client.setPassword(PASSWORD);

        ClientSignatureResourceApi api = new ClientSignatureResourceApi(client);
        ClientAccessResponseDTO access = api.getSignatureAccess(code);

        if (access == null) {
            throw new Exception("No se pudo obtener el acceso a la firma");
        }

        return access;
    }
}
