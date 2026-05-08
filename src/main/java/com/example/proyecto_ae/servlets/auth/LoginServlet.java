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

            String authUrl = viafirmaService.requestAuthentication(callbackURL);

            System.out.println("INFO: Redirigiendo a Viafirma: " + authUrl);
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
