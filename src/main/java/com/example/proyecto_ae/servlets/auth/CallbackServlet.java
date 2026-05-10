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

/**
 * Servlet que maneja el callback de Viafirma después de la autenticación
 */
@WebServlet(name = "CallbackServlet", urlPatterns = { "/CallbackServlet" })
public class CallbackServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String code = request.getParameter("code");
        String error = request.getParameter("error");

        // TRUCO: Si Viafirma no envía el código por URL, lo recuperamos del HACK del LoginServlet
        if (code == null || code.trim().isEmpty()) {
            code = (String) request.getSession().getAttribute("pending_viafirma_code");
            if (code == null || code.trim().isEmpty()) {
                code = (String) request.getServletContext().getAttribute("GLOBAL_PENDING_CODE");
            }
        }

        System.out.println("INFO: Callback recibido desde Viafirma");
        System.out.println("INFO: Código: " + code);

        // Limpiar las variables temporales
        request.getSession().removeAttribute("pending_viafirma_code");
        request.getServletContext().removeAttribute("GLOBAL_PENDING_CODE");

        // Validar que no haya error de Viafirma
        if (error != null && !error.isEmpty()) {
            System.err.println("ERROR: Viafirma retornó error: " + error);
            request.setAttribute(AppConstants.REQUEST_ERROR,
                    "Viafirma retornó un error: " + error);
            request.getRequestDispatcher(AppConstants.PAGE_ERROR).forward(request, response);
            return;
        }

        // Validar que el código no sea nulo o vacío
        if (code == null || code.trim().isEmpty()) {
            System.err.println("ERROR: Código de autenticación nulo o vacío");
            request.setAttribute(AppConstants.REQUEST_ERROR,
                    "Código de autenticación inválido");
            request.getRequestDispatcher(AppConstants.PAGE_ERROR).forward(request, response);
            return;
        }

        try {
            System.out.println("INFO: Validando código de autenticación");
            ViafirmaService viafirmaService = new ViafirmaService();

            // Obtener certificado
            CertificateResponseDTO cert = viafirmaService.getCertificate(code);

            // Generar ID de sesión único para trazabilidad
            String sessionId = UUID.randomUUID().toString();
            request.getSession().setAttribute(AppConstants.SESSION_ID, sessionId);
            request.getSession().setAttribute(AppConstants.SESSION_CERTIFICADO, cert);

            System.out.println("INFO: [" + sessionId + "] Autenticación exitosa para: " +
                    cert.getName() + " " + cert.getSurname1());

            response.sendRedirect(response.encodeRedirectURL(AppConstants.PAGE_INDEX));

        } catch (IllegalArgumentException e) {
            System.err.println("ERROR: Argumento inválido: " + e.getMessage());
            request.setAttribute(AppConstants.REQUEST_ERROR,
                    "Datos inválidos: " + e.getMessage());
            request.getRequestDispatcher(AppConstants.PAGE_ERROR).forward(request, response);

        } catch (Exception e) {
            System.err.println("ERROR en CallbackServlet: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute(AppConstants.REQUEST_ERROR,
                    "Error al procesar la autenticación: " + e.getMessage());
            request.getRequestDispatcher(AppConstants.PAGE_ERROR).forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
