package com.example.proyecto_ae.servlets.firma;

import com.example.proyecto_ae.config.AppConstants;
import com.example.proyecto_ae.services.ViafirmaService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.openapitools.client.model.OperationStatusDTO;
import org.openapitools.client.model.RequestStatusResponseDTO;

/**
 * Servlet que gestiona el retorno de Viafirma tras la firma.
 * Optimizado para ofrecer descarga directa al usuario y evitar bloqueos de red (403).
 */
@WebServlet(name = "FirmaCallbackServlet", urlPatterns = { "/FirmaCallbackServlet" })
public class FirmaCallbackServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String code = request.getParameter("code");
        if (code == null) {
            code = (String) request.getSession().getAttribute(AppConstants.SESSION_FIRMA_TOKEN);
        }

        try {
            ViafirmaService viafirmaService = new ViafirmaService();
            RequestStatusResponseDTO status = viafirmaService.getSignatureStatus(code);
            
            // Guardamos los enlaces directos en la sesión para mostrarlos en la página de confirmación
            if (status.getOperations() != null) {
                for (OperationStatusDTO op : status.getOperations()) {
                    if (op.getFileName().toLowerCase().endsWith(".pdf")) {
                        request.getSession().setAttribute("SESSION_PDF_LINK", op.getSignedLink());
                        request.getSession().setAttribute("SESSION_PDF_NAME", op.getFileName());
                    } else if (op.getFileName().toLowerCase().endsWith(".xml")) {
                        request.getSession().setAttribute("SESSION_XML_LINK", op.getSignedLink());
                        request.getSession().setAttribute("SESSION_XML_NAME", op.getFileName());
                    }
                }
            }

            request.getSession().setAttribute(AppConstants.SESSION_FIRMA_COMPLETADA, true);
            
            // AÑADIR AL LIBRO DE REGISTRO GLOBAL
            java.util.List<java.util.Map<String, String>> registros = (java.util.List<java.util.Map<String, String>>) getServletContext().getAttribute("REGISTRO_ELECTRONICO");
            if (registros == null) {
                registros = new java.util.ArrayList<>();
                getServletContext().setAttribute("REGISTRO_ELECTRONICO", registros);
            }
            
            java.util.Map<String, String> nuevoRegistro = new java.util.HashMap<>();
            nuevoRegistro.put("numero", "REG-" + System.currentTimeMillis());
            nuevoRegistro.put("fecha", java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            nuevoRegistro.put("tipo", "TRÁMITE ELECTRÓNICO");
            
            org.openapitools.client.model.CertificateResponseDTO cert = (org.openapitools.client.model.CertificateResponseDTO) request.getSession().getAttribute("certificado");
            nuevoRegistro.put("remitente", cert != null ? cert.getName() + " " + cert.getSurname1() : "Usuario Identificado");
            
            registros.add(0, nuevoRegistro); // Añadir al principio

            response.sendRedirect(response.encodeRedirectURL(AppConstants.PAGE_CONFIRMACION_FIRMA));

        } catch (Exception e) {
            request.setAttribute(AppConstants.REQUEST_ERROR, "Error en el retorno de firma: " + e.getMessage());
            request.getRequestDispatcher(AppConstants.PAGE_ERROR).forward(request, response);
        }
    }
}
