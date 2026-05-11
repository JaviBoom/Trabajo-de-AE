package com.example.proyecto_ae.servlets.auth;

import com.example.proyecto_ae.config.AppConstants;
import com.example.proyecto_ae.services.ViafirmaService;
import java.io.IOException;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.openapitools.client.model.CertificateResponseDTO;

@WebServlet(name = "CallbackServlet", urlPatterns = { "/CallbackServlet" })
public class CallbackServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String code = request.getParameter("code");

        // RECUPERACIÓN INTELIGENTE: Si no viene en la URL, lo buscamos en la sesión o contexto global
        if (code == null || code.trim().isEmpty()) {
            code = (String) request.getSession().getAttribute("pending_viafirma_code");
            if (code == null) {
                code = (String) request.getServletContext().getAttribute("GLOBAL_PENDING_CODE");
            }
        }

        if (code == null || code.trim().isEmpty()) {
            request.setAttribute(AppConstants.REQUEST_ERROR, "No se ha recibido el código de autenticación (vía URL ni Sesión).");
            request.getRequestDispatcher(AppConstants.PAGE_ERROR).forward(request, response);
            return;
        }

        try {
            ViafirmaService viafirmaService = new ViafirmaService();
            
            // Reintento para Sandbox (espera robusta)
            CertificateResponseDTO cert = null;
            for (int i = 0; i < 3; i++) {
                try {
                    cert = viafirmaService.getCertificate(code);
                    if (cert != null) break;
                } catch (Exception e) {
                    if (i == 2) throw e;
                    Thread.sleep(2000); // Esperamos 2 segundos entre reintentos
                }
            }

            // Limpieza de códigos temporales una vez usado
            request.getSession().removeAttribute("pending_viafirma_code");
            request.getServletContext().removeAttribute("GLOBAL_PENDING_CODE");

            request.getSession().setAttribute(AppConstants.SESSION_ID, UUID.randomUUID().toString());
            request.getSession().setAttribute(AppConstants.SESSION_CERTIFICADO, cert);

            response.sendRedirect(response.encodeRedirectURL(AppConstants.PAGE_INDEX));

        } catch (Exception e) {
            request.setAttribute(AppConstants.REQUEST_ERROR, "Error al recuperar el certificado: " + e.getMessage());
            request.getRequestDispatcher(AppConstants.PAGE_ERROR).forward(request, response);
        }
    }
}
