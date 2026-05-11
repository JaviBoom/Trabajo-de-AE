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
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import org.openapitools.client.model.CertificateResponseDTO;
import org.openapitools.client.model.RequestResultDTO;

/**
 * Servlet encargado de generar los documentos dinámicos y solicitar la firma Batch.
 */
@WebServlet(name = "FirmaServlet", urlPatterns = { "/FirmaServlet" })
public class FirmaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Si se accede por GET, simplemente redirigimos al formulario de datos
        response.sendRedirect("datos.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        CertificateResponseDTO cert = (CertificateResponseDTO) request.getSession().getAttribute(AppConstants.SESSION_CERTIFICADO);

        if (cert == null) {
            handleError(request, response, "Sesión no válida. Por favor, identifíquese.");
            return;
        }

        // Validación de consentimientos requeridos
        if (!"on".equals(request.getParameter("consent")) || !"on".equals(request.getParameter("participacion"))) {
            handleError(request, response, "Debe otorgar los consentimientos necesarios para proceder.");
            return;
        }

        try {
            // 1. Crear directorio temporal para los archivos
            File uploadDir = new File(System.getProperty("java.io.tmpdir"), AppConstants.UPLOAD_DIR);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            // 2. Generar PDF (Justificante)
            String nombrePDF = generarPDF(cert);
            File pdfFile = new File(uploadDir, nombrePDF);

            // 3. Generar XML (Metadatos)
            String nombreXML = generarXML(cert);
            File xmlFile = new File(uploadDir, nombreXML);

            // 4. Preparar solicitud de firma Batch
            java.util.List<File> archivos = new java.util.ArrayList<>();
            archivos.add(pdfFile);
            archivos.add(xmlFile);

            ViafirmaService viafirmaService = new ViafirmaService();
            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
            String callbackURL = baseUrl + AppConstants.FIRMA_CALLBACK_URL;

            RequestResultDTO firmaResult = viafirmaService.prepareSignature(archivos, callbackURL, "Expediente Digital de Administracion Publica");

            // 5. Almacenar datos en sesión para el retorno
            request.getSession().setAttribute(AppConstants.SESSION_PDF_GENERADO, nombrePDF);
            request.getSession().setAttribute("SESSION_XML_GENERADO", nombreXML);
            request.getSession().setAttribute(AppConstants.SESSION_FIRMA_TOKEN, firmaResult.getCode());

            // 6. Redirigir a la plataforma de firma
            String url = firmaResult.getClientAccess().getLink();
            if (url == null) url = firmaResult.getClientAccess().getDesktopProtocol();
            
            response.sendRedirect(url);

        } catch (Exception e) {
            e.printStackTrace();
            handleError(request, response, "Error al procesar la firma: " + e.getMessage());
        }
    }

    private String generarPDF(CertificateResponseDTO cert) throws Exception {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String nombre = "Justificante_" + cert.getNumberUserId() + "_" + timestamp + ".pdf";
        File file = new File(new File(System.getProperty("java.io.tmpdir"), AppConstants.UPLOAD_DIR), nombre);

        try (PdfWriter writer = new PdfWriter(new FileOutputStream(file));
             PdfDocument pdf = new PdfDocument(writer);
             Document doc = new Document(pdf)) {

            com.itextpdf.kernel.colors.Color primaryBlue = new com.itextpdf.kernel.colors.DeviceRgb(30, 58, 138);
            com.itextpdf.kernel.colors.Color lightGray = new com.itextpdf.kernel.colors.DeviceRgb(248, 250, 252);

            // CABECERA ESTILIZADA
            Table header = new Table(new float[]{3, 1}).useAllAvailableWidth();
            header.setMarginBottom(30);
            
            Cell titleCell = new Cell().add(new Paragraph("SEDE ELECTRÓNICA")
                    .setFontSize(24).setBold().setFontColor(primaryBlue));
            titleCell.add(new Paragraph("JUSTIFICANTE DE FIRMA Y REGISTRO")
                    .setFontSize(10).setBold().setFontColor(com.itextpdf.kernel.colors.ColorConstants.DARK_GRAY));
            titleCell.setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);
            header.addCell(titleCell);

            Cell dateCell = new Cell().add(new Paragraph("FECHA DE EMISIÓN\n" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                    .setFontSize(9).setTextAlignment(TextAlignment.RIGHT).setFontColor(com.itextpdf.kernel.colors.ColorConstants.GRAY));
            dateCell.setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);
            header.addCell(dateCell);
            
            doc.add(header);

            // SECCIÓN: DATOS DEL TITULAR
            doc.add(new Paragraph("DATOS DEL TITULAR DEL CERTIFICADO")
                    .setBold().setFontSize(11).setFontColor(primaryBlue).setBorderBottom(new com.itextpdf.layout.borders.SolidBorder(primaryBlue, 1)));
            
            Table userTable = new Table(new float[]{1, 2}).useAllAvailableWidth().setMarginTop(10).setMarginBottom(30);
            userTable.addCell(new Cell().add(new Paragraph("Nombre y Apellidos:")).setBold().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setBackgroundColor(lightGray));
            userTable.addCell(new Cell().add(new Paragraph(cert.getName() + " " + cert.getSurname1())).setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
            
            userTable.addCell(new Cell().add(new Paragraph("Identificación (NIF):")).setBold().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setBackgroundColor(lightGray));
            userTable.addCell(new Cell().add(new Paragraph(cert.getNumberUserId())).setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
            
            userTable.addCell(new Cell().add(new Paragraph("Correo Electrónico:")).setBold().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setBackgroundColor(lightGray));
            userTable.addCell(new Cell().add(new Paragraph(cert.getEmail() != null ? cert.getEmail() : "No disponible")).setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
            
            doc.add(userTable);

            // SECCIÓN: DETALLES DEL TRÁMITE
            doc.add(new Paragraph("DETALLES DEL TRÁMITE ELECTRÓNICO")
                    .setBold().setFontSize(11).setFontColor(primaryBlue).setBorderBottom(new com.itextpdf.layout.borders.SolidBorder(primaryBlue, 1)));
            
            Table flowTable = new Table(new float[]{1, 2}).useAllAvailableWidth().setMarginTop(10).setMarginBottom(40);
            flowTable.addCell(new Cell().add(new Paragraph("Código de Trámite:")).setBold().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setBackgroundColor(lightGray));
            flowTable.addCell(new Cell().add(new Paragraph("EXP-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase())).setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
            
            flowTable.addCell(new Cell().add(new Paragraph("Estado:")).setBold().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setBackgroundColor(lightGray));
            flowTable.addCell(new Cell().add(new Paragraph("Firmado y Registrado")).setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));

            doc.add(flowTable);

            // NOTA LEGAL Y CSV
            doc.add(new Paragraph("\n\nEste documento es una copia auténtica del original electrónico, firmado mediante el uso de certificados digitales reconocidos.")
                    .setFontSize(8).setItalic().setTextAlignment(TextAlignment.CENTER).setFontColor(com.itextpdf.kernel.colors.ColorConstants.GRAY));
            
            String csv = "CSV: " + java.util.UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");
            doc.add(new Paragraph(csv).setFontSize(7).setTextAlignment(TextAlignment.CENTER).setFontColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY));
        }
        return nombre;
    }

    private String generarXML(CertificateResponseDTO cert) throws Exception {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String nombre = "Metadatos_" + cert.getNumberUserId() + "_" + timestamp + ".xml";
        File file = new File(new File(System.getProperty("java.io.tmpdir"), AppConstants.UPLOAD_DIR), nombre);

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                     "<expediente>\n" +
                     "  <interesado>" + cert.getNumberUserId() + "</interesado>\n" +
                     "  <fecha>" + LocalDateTime.now() + "</fecha>\n" +
                     "  <tipo>XAdES-B Enveloped</tipo>\n" +
                     "</expediente>";

        java.nio.file.Files.write(file.toPath(), xml.getBytes("UTF-8"));
        return nombre;
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, String msg) throws ServletException, IOException {
        request.setAttribute(AppConstants.REQUEST_ERROR, msg);
        request.getRequestDispatcher(AppConstants.PAGE_ERROR).forward(request, response);
    }
}
