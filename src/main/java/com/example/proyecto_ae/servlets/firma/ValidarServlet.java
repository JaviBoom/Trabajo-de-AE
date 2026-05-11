package com.example.proyecto_ae.servlets.firma;

import com.example.proyecto_ae.config.AppConstants;
import com.example.proyecto_ae.services.ViafirmaService;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.openapitools.client.model.ValidationFileResponseDTO;

/**
 * Servlet que gestiona la validación interna de documentos firmados.
 */
@WebServlet(name = "ValidarServlet", urlPatterns = { "/ValidarServlet" })
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 10, maxRequestSize = 1024 * 1024 * 20)
public class ValidarServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("validar.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Part filePart = request.getPart("documento");
        if (filePart == null || filePart.getSize() == 0) {
            request.setAttribute(AppConstants.REQUEST_ERROR, "Por favor, seleccione un archivo para validar.");
            request.getRequestDispatcher("validar.jsp").forward(request, response);
            return;
        }

        try (InputStream is = filePart.getInputStream()) {
            byte[] fileBytes = new byte[is.available()];
            is.read(fileBytes);
            String filename = filePart.getSubmittedFileName();

            ViafirmaService viafirmaService = new ViafirmaService();
            ValidationFileResponseDTO validationResult = viafirmaService.validateSignature(fileBytes, filename);

            request.setAttribute("resultado", validationResult);
            request.getRequestDispatcher("validar.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute(AppConstants.REQUEST_ERROR, "Error al validar el documento: " + e.getMessage());
            request.getRequestDispatcher("validar.jsp").forward(request, response);
        }
    }
}
