package com.example.proyecto_ae.servlets.firma;

import com.example.proyecto_ae.config.AppConstants;
import com.example.proyecto_ae.services.ViafirmaService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import org.openapitools.client.model.CertificateResponseDTO;
import org.openapitools.client.model.RequestResultDTO;

/**
 * Servlet que maneja la firma de documentos
 * Genera un PDF y lo envía a Viafirma para firma digital
 */
@WebServlet(name = "FirmaServlet", urlPatterns = { "/FirmaServlet" })
public class FirmaServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Validar que el usuario está autenticado
        CertificateResponseDTO cert = (CertificateResponseDTO) request.getSession()
                .getAttribute(AppConstants.SESSION_CERTIFICADO);

        if (cert == null) {
            System.err.println("ERROR: Intento de firma sin autenticación");
            request.setAttribute(AppConstants.REQUEST_ERROR,
                    AppConstants.MSG_ERROR_NO_AUTH);
            request.getRequestDispatcher(AppConstants.PAGE_ERROR).forward(request, response);
            return;
        }

        String motivo = request.getParameter("motivo");
        String consent = request.getParameter("consent");
        String participacion = request.getParameter("participacion");

        // Validar parámetros
        if (motivo == null || motivo.trim().isEmpty()) {
            System.err.println("ERROR: Motivo vacío");
            request.setAttribute(AppConstants.REQUEST_ERROR,
                    AppConstants.MSG_ERROR_MOTIVO_VACIO);
            request.getRequestDispatcher(AppConstants.PAGE_ERROR).forward(request, response);
            return;
        }

        if (consent == null || !consent.equals("on")) {
            System.err.println("ERROR: Consentimiento no otorgado");
            request.setAttribute(AppConstants.REQUEST_ERROR,
                    AppConstants.MSG_ERROR_CONSENT_REQUERIDO);
            request.getRequestDispatcher(AppConstants.PAGE_ERROR).forward(request, response);
            return;
        }

        if (participacion == null || !participacion.equals("on")) {
            System.err.println("ERROR: Participación no confirmada");
            request.setAttribute(AppConstants.REQUEST_ERROR,
                    AppConstants.MSG_ERROR_PARTICIPACION_REQUERIDA);
            request.getRequestDispatcher(AppConstants.PAGE_ERROR).forward(request, response);
            return;
        }

        try {
            System.out.println("INFO: Generando PDF para firma");

            // 1. Generar PDF con los datos
            String nombrePDF = generarPDFConDatos(cert, motivo);
            File pdfFile = new File(System.getProperty("java.io.tmpdir"),
                    AppConstants.UPLOAD_DIR + "/" + nombrePDF);
            String rutaPDF = pdfFile.getAbsolutePath();

            String sessionId = (String) request.getSession()
                    .getAttribute(AppConstants.SESSION_ID);
            System.out.println("INFO: [" + sessionId + "] PDF generado: " + nombrePDF);

            // 2. Preparar solicitud de firma
            System.out.println("INFO: Preparando solicitud de firma con Viafirma");
            ViafirmaService viafirmaService = new ViafirmaService();

            // Construir callback URL para resultado de firma
            String scheme = request.getScheme();
            String serverName = request.getServerName();
            int serverPort = request.getServerPort();
            String contextPath = request.getContextPath();

            String firmaCallbackURL = scheme + "://" + serverName + ":" + serverPort +
                    contextPath + AppConstants.FIRMA_CALLBACK_URL;

            System.out.println("INFO: Callback de firma: " + firmaCallbackURL);

            // 3. Solicitar firma a Viafirma
            RequestResultDTO firmaResult = viafirmaService.requestSignature(
                    pdfFile,
                    "Documento de Participación - " + motivo,
                    firmaCallbackURL);

            // 4. Guardar datos en sesión para usar después
            request.getSession().setAttribute(AppConstants.SESSION_PDF_GENERADO, nombrePDF);
            request.getSession().setAttribute(AppConstants.SESSION_PDF_RUTA, rutaPDF);
            request.getSession().setAttribute(AppConstants.SESSION_MOTIVO_FIRMA, motivo);
            request.getSession().setAttribute(AppConstants.SESSION_FIRMA_TOKEN,
                    firmaResult.getCode());

            // 5. Redirigir a Viafirma para que el usuario firme
            String firmaUrl = firmaResult.getClientAccess().getDesktopProtocol();
            System.out.println("INFO: [" + sessionId + "] Redirigiendo a Viafirma para firma: "
                    + firmaUrl);

            response.sendRedirect(firmaUrl);

        } catch (Exception e) {
            System.err.println("ERROR en FirmaServlet: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute(AppConstants.REQUEST_ERROR,
                    "Error al preparar la firma: " + e.getMessage());
            request.getRequestDispatcher(AppConstants.PAGE_ERROR).forward(request, response);
        }
    }

    /**
     * Genera un PDF con los datos del certificado y motivo de la solicitud
     * 
     * @param cert   Datos del certificado del usuario
     * @param motivo Motivo de la solicitud
     * @return Nombre del archivo PDF generado
     * @throws Exception Si hay error al generar el PDF
     */
    private String generarPDFConDatos(CertificateResponseDTO cert, String motivo)
            throws Exception {

        // Crear directorio si no existe
        String basePath = System.getProperty("java.io.tmpdir");
        File uploadDir = new File(basePath, AppConstants.UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Generar nombre de archivo único
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern(AppConstants.DATE_FORMAT_FILENAME));
        String nombreArchivo = "Firma_" + cert.getNumberUserId() + "_" + timestamp + ".pdf";
        File pdfFile = new File(uploadDir, nombreArchivo);

        // Crear el documento PDF
        PdfWriter writer = new PdfWriter(new FileOutputStream(pdfFile));
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Título
        Paragraph titulo = new Paragraph("DOCUMENTO DE PARTICIPACIÓN Y FIRMA DIGITAL")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(16)
                .setBold();
        document.add(titulo);

        // Separador
        document.add(new Paragraph("\n"));

        // Información del usuario (tabla)
        Table tablaUsuario = new Table(2);
        tablaUsuario.addCell("Nombre y Apellidos:");
        tablaUsuario.addCell(cert.getName() + " " + cert.getSurname1());

        tablaUsuario.addCell("NIF/NIE:");
        tablaUsuario.addCell(cert.getNumberUserId());

        tablaUsuario.addCell("Email:");
        tablaUsuario.addCell(cert.getEmail() != null ? cert.getEmail() : "No disponible");

        tablaUsuario.addCell("Emisor del Certificado:");
        tablaUsuario.addCell(cert.getCa() != null ? cert.getCa() : "No disponible");

        document.add(tablaUsuario);

        // Separador
        document.add(new Paragraph("\n"));

        // Datos de la solicitud
        Paragraph seccionSolicitud = new Paragraph("DATOS DE LA SOLICITUD")
                .setBold()
                .setFontSize(12);
        document.add(seccionSolicitud);

        document.add(new Paragraph("Motivo de la solicitud:"));
        document.add(new Paragraph(motivo)
                .setTextAlignment(TextAlignment.JUSTIFIED));

        // Separador
        document.add(new Paragraph("\n"));

        // Consentimientos
        Paragraph seccionConsentimiento = new Paragraph("CONSENTIMIENTOS")
                .setBold()
                .setFontSize(12);
        document.add(seccionConsentimiento);

        document.add(new Paragraph("☑ El usuario consiente la verificación de identidad."));
        document.add(new Paragraph("☑ El usuario desea participar en el procedimiento de PRUEBA."));

        // Separador
        document.add(new Paragraph("\n"));

        // Fecha y firma
        String fechaHora = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern(AppConstants.DATE_FORMAT_PDF));

        Paragraph fechaFirma = new Paragraph("Generado el: " + fechaHora)
                .setTextAlignment(TextAlignment.RIGHT)
                .setItalic();
        document.add(fechaFirma);

        Paragraph firma = new Paragraph("Firmado digitalmente por: " + cert.getNumberUserId())
                .setTextAlignment(TextAlignment.RIGHT)
                .setItalic();
        document.add(firma);

        // Cerrar documento
        document.close();

        System.out.println("INFO: PDF generado exitosamente en: " + pdfFile.getAbsolutePath());

        return nombreArchivo;
    }
}
