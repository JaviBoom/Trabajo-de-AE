package com.example.proyecto_ae.servlets.firma;

import com.example.proyecto_ae.config.AppConstants;
import com.example.proyecto_ae.services.ViafirmaService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.openapitools.client.model.RequestStatusResponseDTO;
import org.openapitools.client.model.OperationStatusDTO;

/**
 * Servlet que maneja el callback de Viafirma para firma dual.
 * Captura los enlaces de descarga para evitar errores 403 en el servidor.
 */
@WebServlet(name = "FirmaCallbackServlet", urlPatterns = { "/FirmaCallbackServlet" })
public class FirmaCallbackServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            
        String code = request.getParameter("code");
        if (code == null) code = (String) request.getSession().getAttribute(AppConstants.SESSION_FIRMA_TOKEN);
        
        if (code == null) {
            response.sendRedirect(AppConstants.PAGE_INDEX);
            return;
        }

        try {
            ViafirmaService viafirmaService = new ViafirmaService();
            RequestStatusResponseDTO status = viafirmaService.getSignatureStatus(code);
            
            // Guardar los enlaces de descarga en sesión para que el usuario descargue directamente
            if (status.getOperations() != null) {
                for (OperationStatusDTO op : status.getOperations()) {
                    String signedUrl = op.getSignedLink();
                    String fileName = op.getFileName();
                    
                    if (fileName != null && fileName.toLowerCase().endsWith(".pdf")) {
                        request.getSession().setAttribute("SESSION_PDF_LINK", signedUrl);
                        request.getSession().setAttribute(AppConstants.SESSION_PDF_GENERADO, fileName);
                    } else if (fileName != null && fileName.toLowerCase().endsWith(".xml")) {
                        request.getSession().setAttribute("SESSION_XML_LINK", signedUrl);
                        request.getSession().setAttribute("SESSION_XML_GENERADO", fileName);
                    }
                }
            }

            request.getSession().setAttribute(AppConstants.SESSION_FIRMA_COMPLETADA, true);
            response.sendRedirect(AppConstants.PAGE_CONFIRMACION_FIRMA);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute(AppConstants.REQUEST_ERROR, "Error al procesar el retorno de firma: " + e.getMessage());
            request.getRequestDispatcher(AppConstants.PAGE_ERROR).forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
