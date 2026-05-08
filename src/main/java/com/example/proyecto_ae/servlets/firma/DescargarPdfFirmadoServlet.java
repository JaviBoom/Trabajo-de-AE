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

        // 1) Intentar descargar desde la URL de acceso de Viafirma (si existe)
        ClientAccessResponseDTO firmaAcceso = (ClientAccessResponseDTO) request.getSession()
                .getAttribute(AppConstants.SESSION_FIRMA_ACCESO);

        String remoteLink = firmaAcceso != null ? firmaAcceso.getLink() : null;
        if (remoteLink != null && (remoteLink.startsWith("http://") || remoteLink.startsWith("https://"))) {
            try {
                streamRemotePdf(remoteLink, fileName, response);
                return;
            } catch (Exception ex) {
                System.err.println("WARN: No se pudo descargar el PDF remoto de Viafirma, usando fallback local: " + ex.getMessage());
            }
        }

        // 2) Fallback local (útil en modo mock y como respaldo)
        String localPath = (String) request.getSession().getAttribute(AppConstants.SESSION_PDF_RUTA);
        if (localPath == null || localPath.trim().isEmpty()) {
            request.setAttribute(AppConstants.REQUEST_ERROR, "No se encontró la ruta local del documento");
            request.getRequestDispatcher(AppConstants.PAGE_ERROR).forward(request, response);
            return;
        }

        File pdfFile = new File(localPath);
        if (!pdfFile.exists() || !pdfFile.isFile()) {
            request.setAttribute(AppConstants.REQUEST_ERROR, "El archivo PDF no existe en el servidor");
            request.getRequestDispatcher(AppConstants.PAGE_ERROR).forward(request, response);
            return;
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setContentLengthLong(pdfFile.length());

        try (InputStream input = new FileInputStream(pdfFile);
             OutputStream output = response.getOutputStream()) {
            copy(input, output);
        }
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
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

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
