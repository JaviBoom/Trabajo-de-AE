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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            
        System.out.println("DEBUG: Callback recibido (Metodo: " + request.getMethod() + ")");
        System.out.println("DEBUG: Full URL: " + request.getRequestURL().toString() + (request.getQueryString() != null ? "?" + request.getQueryString() : ""));

        System.out.println("DEBUG: Full URL: " + request.getRequestURL().toString() + (request.getQueryString() != null ? "?" + request.getQueryString() : ""));
        
        String code = request.getParameter("code");
        if (code == null || code.isEmpty()) {
            code = request.getParameter("operationId");
        }
        
        // Si sigue siendo nulo, lo buscamos en la sesión (lo guardamos en FirmaServlet)
        if (code == null || code.isEmpty()) {
            code = (String) request.getSession().getAttribute(AppConstants.SESSION_FIRMA_TOKEN);
            System.out.println("INFO: Recuperando código de firma desde la sesión: " + code);
        }
        
        String error = request.getParameter("error");

        System.out.println("INFO: Callback de firma recibido desde Viafirma");
        System.out.println("INFO: Código (code): " + code);
        System.out.println("INFO: Error (error): " + error);
        
        // Log de todos los parámetros para depuración
        java.util.Enumeration<String> params = request.getParameterNames();
        while(params.hasMoreElements()){
            String pName = params.nextElement();
            System.out.println("DEBUG: Param: " + pName + " = " + request.getParameter(pName));
        }

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
            StringBuilder sb = new StringBuilder("Código de firma inválido. Parámetros recibidos: ");
            java.util.Enumeration<String> pNames = request.getParameterNames();
            while(pNames.hasMoreElements()){
                String n = pNames.nextElement();
                sb.append(n).append("=").append(request.getParameter(n)).append("; ");
            }
            System.err.println("ERROR: " + sb.toString());
            request.setAttribute(AppConstants.REQUEST_ERROR, sb.toString());
            request.getRequestDispatcher(AppConstants.PAGE_ERROR).forward(request, response);
            return;
        }

        try {
            System.out.println("INFO: Validando código de firma");
            ViafirmaService viafirmaService = new ViafirmaService();

            // Obtener acceso a la firma (incluye URL del PDF firmado)
            ClientAccessResponseDTO firmaAcceso = viafirmaService.getSignatureAccess(code);
            
            // INTENTO DE DESCARGA FÍSICA DEL PDF FIRMADO
            if (firmaAcceso != null && firmaAcceso.getLink() != null) {
                try {
                    // Consultar el estado para obtener el enlace de descarga real (signedLink)
                    org.openapitools.client.model.RequestStatusResponseDTO status = viafirmaService.getSignatureStatus(code);
                    if (status != null && status.getOperations() != null && !status.getOperations().isEmpty()) {
                        String signedLink = status.getOperations().get(0).getSignedLink();
                        if (signedLink != null) {
                            byte[] pdfFirmadoBytes = viafirmaService.downloadSignedPdf(signedLink);
                            
                            // Sobrescribir el archivo local con el firmado
                            String localPath = (String) request.getSession().getAttribute(AppConstants.SESSION_PDF_RUTA);
                            if (localPath != null) {
                                java.nio.file.Files.write(java.nio.file.Paths.get(localPath), pdfFirmadoBytes);
                                System.out.println("INFO: Archivo local actualizado con la versión FIRMADA digitalmente");
                            }
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("WARN: No se pudo descargar el PDF firmado en el callback: " + ex.getMessage());
                }
            }

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
