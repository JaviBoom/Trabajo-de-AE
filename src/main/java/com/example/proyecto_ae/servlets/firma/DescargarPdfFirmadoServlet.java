package com.example.proyecto_ae.servlets.firma;

import com.example.proyecto_ae.config.AppConstants;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet genérico para descargar archivos del expediente (PDF o XML)
 */
@WebServlet(name = "DescargarPdfFirmadoServlet", urlPatterns = { "/DescargarPdfFirmadoServlet" })
public class DescargarPdfFirmadoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String type = request.getParameter("type");
        String fileName;
        
        if ("xml".equals(type)) {
            fileName = (String) request.getSession().getAttribute("SESSION_XML_GENERADO");
            response.setContentType("application/xml");
        } else {
            fileName = (String) request.getSession().getAttribute(AppConstants.SESSION_PDF_GENERADO);
            response.setContentType("application/pdf");
        }

        if (fileName == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Archivo no disponible en la sesión");
            return;
        }

        File uploadDir = new File(System.getProperty("java.io.tmpdir"), AppConstants.UPLOAD_DIR);
        File file = new File(uploadDir, fileName);

        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "El archivo físico no existe");
            return;
        }

        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setContentLength((int) file.length());

        try (FileInputStream fis = new FileInputStream(file);
             OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }
}
