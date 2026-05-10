package com.example.proyecto_ae.servlets.firma;

import com.example.proyecto_ae.config.AppConstants;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.example.proyecto_ae.services.ViafirmaService;
import org.openapitools.client.model.ClientAccessResponseDTO;

/**
 * Descarga el PDF firmado (o el PDF generado localmente como fallback).
 */
@WebServlet(name = "DescargarPdfFirmadoServlet", urlPatterns = { "/DescargarPdfFirmadoServlet" })
public class DescargarPdfFirmadoServlet extends HttpServlet {

    private static final int BUFFER_SIZE = 8192;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String fileName = (String) request.getSession().getAttribute(AppConstants.SESSION_PDF_GENERADO);
        if (fileName == null || fileName.trim().isEmpty()) {
            request.setAttribute(AppConstants.REQUEST_ERROR, "No hay documento disponible para descargar");
            request.getRequestDispatcher(AppConstants.PAGE_ERROR).forward(request, response);
            return;
        }

        // 1) Intentar descargar el archivo LOCAL (que ya ha sido sobrescrito con el firmado en el Callback)
        String localPath = (String) request.getSession().getAttribute(AppConstants.SESSION_PDF_RUTA);
        if (localPath != null) {
            java.io.File file = new java.io.File(localPath);
            if (file.exists()) {
                System.out.println("DEBUG: Descargando archivo local (versión firmada): " + localPath);
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
                response.setContentLength((int) file.length());
                
                try (java.io.FileInputStream fis = new java.io.FileInputStream(file);
                     java.io.OutputStream os = response.getOutputStream()) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                }
                return;
            }
        }

        // 2) Si falla el local, intentar el remoto como último recurso
        try {
            String signedLink = null;
            String token = (String) request.getSession().getAttribute(AppConstants.SESSION_FIRMA_TOKEN);
            if (token != null) {
                ViafirmaService viaService = new ViafirmaService();
                org.openapitools.client.model.RequestStatusResponseDTO status = viaService.getSignatureStatus(token);
                if (status != null && status.getOperations() != null && !status.getOperations().isEmpty()) {
                    signedLink = status.getOperations().get(0).getSignedLink();
                }
            }
            if (signedLink != null) {
                streamRemotePdf(signedLink, fileName, response);
                return;
            }
        } catch (Exception ex) {
            System.err.println("WARN: Fallback remoto falló: " + ex.getMessage());
        }

        request.setAttribute(AppConstants.REQUEST_ERROR, "No se pudo recuperar el documento firmado ni el original");
        request.getRequestDispatcher(AppConstants.PAGE_ERROR).forward(request, response);
    }

    private void streamRemotePdf(String remoteUrl, String fileName, HttpServletResponse response) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(remoteUrl).openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(30000);

        int status = connection.getResponseCode();
        if (status < 200 || status >= 300) {
            throw new IOException("Respuesta HTTP no válida: " + status);
        }

        String contentType = connection.getContentType();
        if (contentType == null || contentType.trim().isEmpty()) {
            contentType = "application/pdf";
        }

        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

        long contentLength = connection.getContentLengthLong();
        if (contentLength > 0) {
            response.setContentLengthLong(contentLength);
        }

        try (InputStream input = connection.getInputStream();
             OutputStream output = response.getOutputStream()) {
            copy(input, output);
        } finally {
            connection.disconnect();
        }
    }

    private void copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }
}
