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
     * @return Objeto con los datos de acceso
     * @throws Exception Si hay error en la comunicación con Viafirma
     */
    public RequestResultDTO requestAuthentication(String callbackUrl) throws Exception {
        ApiClient client = new ApiClient();
        client.setUsername(USERNAME);
        client.setPassword(PASSWORD);

        ClientAuthResourceApi api = new ClientAuthResourceApi(client);

        PrepareAuthDTO auth = new PrepareAuthDTO();
        auth.setCallbackRedirectURL(callbackUrl);
        // CRÍTICO: NO ESTABLECER callbackURL (webhook)!! 
        // Si establecemos webhook, Viafirma manda el código al webhook y NO lo añade a la redirección del navegador.
        // Al dejar el webhook vacío, forzamos a Viafirma a enviar ?code= directamente por la URL al navegador.

        RequestResultDTO result = api.requestAuthentication(auth);

        if (result == null || result.getClientAccess() == null) {
            throw new Exception("Respuesta inválida de Viafirma");
        }

        return result;
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
        System.out.println("DEBUG: Archivo PDF para firma: " + pdfFile.getAbsolutePath() + " (" + pdfFile.length() + " bytes)");

        // 1) Solicitar token de subida y subir el archivo a Viafirma
        UploadResourceApi uploadApi = new UploadResourceApi(client);
        RequestResultDTO uploadLinkResult = null;
        try {
            System.out.println("DEBUG: PASO 1 - Solicitando requestLink...");
            uploadLinkResult = uploadApi.requestLink();
        } catch (org.openapitools.client.ApiException e) {
            throw new Exception("Error en PASO 1 (requestLink): " + e.getCode() + " - " + e.getResponseBody());
        }

        if (uploadLinkResult == null || uploadLinkResult.getCode() == null) {
            throw new Exception("Error en PASO 1: Respuesta nula de Viafirma");
        }

        FileReferenceResponseDTO uploadResult = null;
        try {
            System.out.println("DEBUG: PASO 2 - Subiendo archivo a Viafirma...");
            uploadResult = uploadApi.uploadFile(uploadLinkResult.getCode(), pdfFile);
        } catch (org.openapitools.client.ApiException e) {
            throw new Exception("Error en PASO 2 (uploadFile): " + e.getCode() + " - " + e.getResponseBody());
        }

        if (uploadResult == null || uploadResult.getReference() == null) {
            throw new Exception("Error en PASO 2: Error al obtener referencia del archivo subido");
        }

        // 2) Preparar solicitud de firma
        ClientSignatureResourceApi api = new ClientSignatureResourceApi(client);
        PrepareRequestDTO prepareRequest = new PrepareRequestDTO();
        PrepareSignatureRequestDTO signature = new PrepareSignatureRequestDTO();
        
        // Enviamos el archivo directamente en Base64 para evitar problemas con referencias
        byte[] fileContent = java.nio.file.Files.readAllBytes(pdfFile.toPath());
        String base64File = java.util.Base64.getEncoder().encodeToString(fileContent);
        
        signature.setFile(base64File);
        signature.setFileName(sanitize(pdfFile.getName()));
        signature.setFileType("pdf");
        if (description != null && !description.trim().isEmpty()) {
            signature.setRequestCode(sanitize(description));
        }

        // Configuración de firma (obligatoria por lo visto)
        org.openapitools.client.model.ConfigSignatureDTO config = new org.openapitools.client.model.ConfigSignatureDTO();
        config.setSignatureType(org.openapitools.client.model.ConfigSignatureDTO.SignatureTypeEnum.PADES_B);
        config.setPackaging(org.openapitools.client.model.ConfigSignatureDTO.PackagingEnum.ENVELOPED); // Obligatorio
        config.setFileName(sanitize(pdfFile.getName())); // Lo pide explícitamente el servidor
        config.setValidSignerIds(new java.util.ArrayList<String>()); // Evita el error de "array length is null"
        
        // Configuración de sello visual (Stamper)
        org.openapitools.client.model.ConfigPadesDTO pades = new org.openapitools.client.model.ConfigPadesDTO();
        org.openapitools.client.model.StamperDTO stamper = new org.openapitools.client.model.StamperDTO();
        stamper.setType(org.openapitools.client.model.StamperDTO.TypeEnum.QR);
        stamper.setPage(-1); 
        stamper.setxAxis(400);
        stamper.setyAxis(50);
        pades.setStamper(stamper);
        config.setPadesConfig(pades);

        // Añadir metadatos para mejorar la auditoría
        config.setSignatureReason("Aceptacion de participacion y firma digital");
        config.setCountry("Espana");
        
        signature.setConfiguration(config);

        prepareRequest.setCount(1);
        prepareRequest.setCallbackRedirectURL(callbackUrl);
        
        // Filtro de certificado para obligar a usar el mismo que en el login
        org.openapitools.client.model.ConfigCertificateRequestDTO certFilter = new org.openapitools.client.model.ConfigCertificateRequestDTO();
        if (pdfFile.getName().contains("_")) {
            // Intentamos extraer el NIF del nombre del archivo si no lo pasamos por parámetro
            // En una refactorización ideal, pasaríamos el NIF directamente al método
            String[] parts = pdfFile.getName().split("_");
            if (parts.length > 1) {
                String nif = parts[1];
                org.openapitools.client.model.CertificateFilterDTO nifFilter = new org.openapitools.client.model.CertificateFilterDTO();
                nifFilter.addFilterValuesItem(nif);
                certFilter.setNationalIdFilter(nifFilter);
                System.out.println("DEBUG: Aplicando filtro de certificado para NIF: " + nif);
            }
        }
        prepareRequest.setCertificateFilter(certFilter);
        
        java.util.List<org.openapitools.client.model.PrepareSignatureRequestDTO> signatureList = new java.util.ArrayList<>();
        signatureList.add(signature);
        prepareRequest.setSignatures(signatureList);

        RequestResultDTO result = null;
        try {
            System.out.println("DEBUG: PASO 3 - Solicitando firma final...");
            result = api.requestSignature(prepareRequest);
        } catch (org.openapitools.client.ApiException e) {
            throw new Exception("Error en PASO 3 (requestSignature): " + e.getCode() + " - " + e.getResponseBody());
        }

        if (result == null || result.getClientAccess() == null) {
            throw new Exception("Error en PASO 3: Respuesta de firma inválida");
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

    /**
     * Elimina acentos y caracteres especiales que puedan dar problemas con UTF-8
     */
    /**
     * Descarga el PDF firmado desde la URL de Viafirma
     */
    public byte[] downloadSignedPdf(String signedLink) throws Exception {
        System.out.println("INFO: Descargando PDF firmado desde: " + signedLink);
        java.net.URL url = new java.net.URL(signedLink);
        try (java.io.InputStream in = url.openStream();
             java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            return out.toByteArray();
        }
    }

    private String sanitize(String name) {
        if (name == null) return "documento.pdf";
        return name.replaceAll("[^a-zA-Z0-9.\\-_]", "_");
    }
}
