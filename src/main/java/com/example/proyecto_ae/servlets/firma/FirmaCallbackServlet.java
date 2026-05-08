package com.example.proyecto_ae.servlets.firma;

import com.example.proyecto_ae.config.AppConstants;
import com.example.proyecto_ae.services.ViafirmaService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openapitools.client.model.ClientAccessResponseDTO;

/**
 * Servlet que maneja el callback de Viafirma después de la firma
 */
@WebServlet(name = "FirmaCallbackServlet", urlPatterns = { "/FirmaCallbackServlet" })
public class FirmaCallbackServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String code = request.getParameter("code");
        String error = request.getParameter("error");

        System.out.println("INFO: Callback de firma recibido desde Viafirma");
        System.out.println("INFO: Código: " + code);

        // Validar que no haya error de Viafirma
        if (error != null && !error.isEmpty()) {
            System.err.println("ERROR: Viafirma retornó error en firma: " + error);
            request.setAttribute(AppConstants.REQUEST_ERROR,
                    "Viafirma retornó un error al firmar: " + error);
            request.getRequestDispatcher(AppConstants.PAGE_ERROR).forward(request, response);
            return;
        }

        // Validar que el código no sea nulo o vacío
        if (code == null || code.trim().isEmpty()) {
            System.err.println("ERROR: Código de firma nulo o vacío");
            request.setAttribute(AppConstants.REQUEST_ERROR,
                    "Código de firma inválido");
            request.getRequestDispatcher(AppConstants.PAGE_ERROR).forward(request, response);
            return;
        }

        try {
            System.out.println("INFO: Validando código de firma");
            ViafirmaService viafirmaService = new ViafirmaService();

            // Obtener acceso a la firma (incluye URL del PDF firmado)
            ClientAccessResponseDTO firmaAcceso = viafirmaService.getSignatureAccess(code);

            // Guardar en sesión para confirmación
            request.getSession().setAttribute(AppConstants.SESSION_FIRMA_COMPLETADA, true);
            request.getSession().setAttribute(AppConstants.SESSION_FIRMA_ACCESO, firmaAcceso);

            String sessionId = (String) request.getSession()
                    .getAttribute(AppConstants.SESSION_ID);
            System.out.println("INFO: [" + sessionId + "] Firma completada exitosamente");

            // Redirigir a página de confirmación
            response.sendRedirect(AppConstants.PAGE_CONFIRMACION_FIRMA);

        } catch (IllegalArgumentException e) {
            System.err.println("ERROR: Argumento inválido: " + e.getMessage());
            request.setAttribute(AppConstants.REQUEST_ERROR,
                    "Datos inválidos: " + e.getMessage());
            request.getRequestDispatcher(AppConstants.PAGE_ERROR).forward(request, response);

        } catch (Exception e) {
            System.err.println("ERROR en FirmaCallbackServlet: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute(AppConstants.REQUEST_ERROR,
                    "Error al procesar la firma: " + e.getMessage());
            request.getRequestDispatcher(AppConstants.PAGE_ERROR).forward(request, response);
        }
    }
}
