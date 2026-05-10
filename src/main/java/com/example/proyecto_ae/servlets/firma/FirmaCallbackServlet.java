package com.example.proyecto_ae.servlets.firma;

import com.example.proyecto_ae.config.AppConstants;
import com.example.proyecto_ae.services.ViafirmaService;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.openapitools.client.model.CertificateResponseDTO;
import org.openapitools.client.model.RequestStatusResponseDTO;
import org.openapitools.client.model.OperationStatusDTO;

/**
 * Servlet que maneja el callback de Viafirma para firma dual.
 * 
 * NOTA DE IMPLEMENTACIÓN: Inicialmente intentamos descargar los ficheros en el servidor,
 * pero Viafirma devolvía un 403 por falta de sesión/cookies. Decidimos cambiar la estrategia
 * a "descarga directa" capturando el signedLink y dándoselo al navegador del usuario.
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

            // Generar asientos registrales (Concepto de Registro Electrónico - Art. 16 Ley 39/2015)
            // Decidimos automatizar esto aquí para que al usuario le salga el número de registro nada más firmar.
            CertificateResponseDTO cert = (CertificateResponseDTO) request.getSession().getAttribute(AppConstants.SESSION_CERTIFICADO);
            if (cert != null) {
                @SuppressWarnings("unchecked")
                List<Map<String, String>> registros = (List<Map<String, String>>) getServletContext().getAttribute("REGISTRO_ELECTRONICO");
                if (registros == null) {
                    registros = Collections.synchronizedList(new ArrayList<>());
                }
                String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                String remitente = (cert.getName() != null ? cert.getName() : "") + " " + (cert.getSurname1() != null ? cert.getSurname1() : "");
                String nif = cert.getNumberUserId() != null ? cert.getNumberUserId() : "N/A";
                int nextNum = registros.size() + 1;
                
                // Asiento de ENTRADA
                Map<String, String> entrada = new LinkedHashMap<>();
                entrada.put("numero", String.format("E-%04d/%d", nextNum, LocalDateTime.now().getYear()));
                entrada.put("fecha", now);
                entrada.put("tipo", "ENTRADA");
                entrada.put("remitente", remitente.trim());
                entrada.put("nif", nif);
                entrada.put("descripcion", "Solicitud de firma de expediente digital dual");
                entrada.put("documentos", "PDF (PAdES) + XML (XAdES)");
                registros.add(entrada);
                
                // Asiento de SALIDA
                Map<String, String> salida = new LinkedHashMap<>();
                salida.put("numero", String.format("S-%04d/%d", nextNum, LocalDateTime.now().getYear()));
                salida.put("fecha", now);
                salida.put("tipo", "SALIDA");
                salida.put("remitente", "Sede Electrónica UPO");
                salida.put("nif", nif);
                salida.put("descripcion", "Expediente firmado y notificado al interesado");
                salida.put("documentos", "Comprobante de firma electrónica");
                registros.add(salida);
                
                getServletContext().setAttribute("REGISTRO_ELECTRONICO", registros);
            }
            
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
