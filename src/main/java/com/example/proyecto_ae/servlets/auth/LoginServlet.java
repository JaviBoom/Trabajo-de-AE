package com.example.proyecto_ae.servlets.auth;

import com.example.proyecto_ae.config.AppConstants;
import com.example.proyecto_ae.services.ViafirmaService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet que inicia el flujo de autenticación con Viafirma
 */
@WebServlet(name = "LoginServlet", urlPatterns = { "/LoginServlet" })
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            System.out.println("INFO: Iniciando flujo de autenticación con Viafirma");

            ViafirmaService viafirmaService = new ViafirmaService();

            // URL de callback (construir dinámicamente según el host)
            String scheme = request.getScheme();
            String serverName = request.getServerName();
            int serverPort = request.getServerPort();
            String contextPath = request.getContextPath();

            String callbackURL = scheme + "://" + serverName + ":" + serverPort +
                    contextPath + AppConstants.AUTH_CALLBACK_URL;

            System.out.println("INFO: Callback URL: " + callbackURL);

            org.openapitools.client.model.RequestResultDTO result = viafirmaService.requestAuthentication(callbackURL);

            String authUrl = result.getClientAccess().getLink();
            if (authUrl == null || authUrl.isEmpty()) {
                authUrl = result.getClientAccess().getDesktopProtocol();
            }
            String authCode = result.getCode();
            
            // TRUCO: Si Viafirma no devuelve el código explícitamente, lo extraemos del JWT en la URL
            if (authCode == null || authCode.trim().isEmpty()) {
                try {
                    String jwt = authUrl.substring(authUrl.lastIndexOf('/') + 1);
                    String jwtPayload = jwt.split("\\.")[1];
                    // Añadir padding para evitar IllegalArgumentException en Java 8
                    while (jwtPayload.length() % 4 != 0) {
                        jwtPayload += "=";
                    }
                    String payload = new String(java.util.Base64.getUrlDecoder().decode(jwtPayload));
                    authCode = payload.split("\"operationId\":\"")[1].split("\"")[0];
                } catch (Exception e) {
                    System.err.println("No se pudo extraer el operationId del JWT: " + e.getMessage());
                }
            }

            // Guardar en el contexto global por si se pierde la sesión (SameSite cookies)
            request.getServletContext().setAttribute("GLOBAL_PENDING_CODE", authCode);
            request.getSession().setAttribute("pending_viafirma_code", authCode);

            System.out.println("INFO: Redirigiendo a Viafirma: " + authUrl + " con codigo: " + authCode);
            response.sendRedirect(authUrl);

        } catch (Exception e) {
            System.err.println("ERROR en LoginServlet: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute(AppConstants.REQUEST_ERROR,
                    "Error al iniciar la autenticación: " + e.getMessage());
            request.getRequestDispatcher(AppConstants.PAGE_ERROR).forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
