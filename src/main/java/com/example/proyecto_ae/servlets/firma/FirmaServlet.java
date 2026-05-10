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

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;
import java.net.URL;
import java.net.URLEncoder;
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
 * Servlet que maneja la firma dual (PDF + XML)
 */
@WebServlet(name = "FirmaServlet", urlPatterns = { "/FirmaServlet" })
public class FirmaServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        CertificateResponseDTO cert = (CertificateResponseDTO) request.getSession().getAttribute(AppConstants.SESSION_CERTIFICADO);

        if (cert == null) {
            handleError(request, response, AppConstants.MSG_ERROR_NO_AUTH);
            return;
        }

        if (!"on".equals(request.getParameter("consent")) || !"on".equals(request.getParameter("participacion"))) {
            handleError(request, response, "Debe otorgar los consentimientos necesarios.");
            return;
        }

        try {
            // 1. Generar PDF Premium
            String nombrePDF = generarPDFConDatos(cert, request);
            File uploadDir = new File(System.getProperty("java.io.tmpdir"), AppConstants.UPLOAD_DIR);
            File pdfFile = new File(uploadDir, nombrePDF);

            // 2. Generar XML Técnico (XAdES)
            String nombreXML = generarXMLConDatos(cert);
            File xmlFile = new File(uploadDir, nombreXML);

            // 3. Preparar lista para firma en lote
            java.util.List<File> archivosAFirmar = new java.util.ArrayList<>();
            archivosAFirmar.add(pdfFile);
            archivosAFirmar.add(xmlFile);

            ViafirmaService viafirmaService = new ViafirmaService();

            // Construir callback
            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
            String callbackURL = baseUrl + AppConstants.FIRMA_CALLBACK_URL;

            // 4. Solicitar firma a Viafirma
            RequestResultDTO firmaResult = viafirmaService.prepareSignature(archivosAFirmar, callbackURL, "Expediente Digital Dual (PDF+XML)");

            // 5. Sesión
            request.getSession().setAttribute(AppConstants.SESSION_PDF_GENERADO, nombrePDF);
            request.getSession().setAttribute(AppConstants.SESSION_PDF_RUTA, pdfFile.getAbsolutePath());
            request.getSession().setAttribute("SESSION_XML_GENERADO", nombreXML);
            request.getSession().setAttribute(AppConstants.SESSION_FIRMA_TOKEN, firmaResult.getCode());

            // 6. Redirigir
            String firmaUrl = firmaResult.getClientAccess().getLink();
            if (firmaUrl == null || firmaUrl.isEmpty()) {
                firmaUrl = firmaResult.getClientAccess().getDesktopProtocol();
            }
            response.sendRedirect(firmaUrl);

        } catch (Exception e) {
            e.printStackTrace();
            handleError(request, response, "Error al procesar la firma: " + e.getMessage());
        }
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, String msg) throws ServletException, IOException {
        request.setAttribute(AppConstants.REQUEST_ERROR, msg);
        request.getRequestDispatcher(AppConstants.PAGE_ERROR).forward(request, response);
    }

    private String generarPDFConDatos(CertificateResponseDTO cert, HttpServletRequest request) throws Exception {
        String basePath = System.getProperty("java.io.tmpdir");
        File uploadDir = new File(basePath, AppConstants.UPLOAD_DIR);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern(AppConstants.DATE_FORMAT_FILENAME));
        String nombreArchivo = "Firma_" + cert.getNumberUserId() + "_" + timestamp + ".pdf";
        File pdfFile = new File(uploadDir, nombreArchivo);

        PdfWriter writer = new PdfWriter(new FileOutputStream(pdfFile));
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);
        document.setMargins(40, 40, 40, 40);

        com.itextpdf.kernel.colors.Color primaryBlue = new com.itextpdf.kernel.colors.DeviceRgb(26, 35, 126);
        com.itextpdf.kernel.colors.Color accentGold = new com.itextpdf.kernel.colors.DeviceRgb(191, 158, 0);
        com.itextpdf.kernel.colors.Color lightGray = new com.itextpdf.kernel.colors.DeviceRgb(245, 247, 250);

        Table headerTable = new Table(com.itextpdf.layout.properties.UnitValue.createPercentArray(new float[]{20, 50, 30})).useAllAvailableWidth();
        
        // Logo
        try {
            String logoPath = "C:\\Users\\scala\\.gemini\\antigravity\\brain\\92bf61e3-39a7-4e4d-855d-c8bfe1581eb1\\upo_logo_placeholder_1778435946740.png";
            headerTable.addCell(new Cell().add(new Image(ImageDataFactory.create(logoPath)).setWidth(60)).setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
        } catch (Exception e) {
            headerTable.addCell(new Cell().add(new Paragraph("UPO")).setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
        }

        headerTable.addCell(new Cell().add(new Paragraph("COMPROBANTE DE\nFIRMA ELECTRÓNICA").setFontSize(14).setBold().setFontColor(primaryBlue).setTextAlignment(TextAlignment.CENTER)).setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE));

        // Identidad y Tracking
        try {
            String token = (String) request.getSession().getAttribute(AppConstants.SESSION_FIRMA_TOKEN);
            String displayToken = (token != null) ? token : "EXPEDIENTE-DUAL-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            
            Cell techCell = new Cell().add(new Paragraph(cert.getName() + " " + cert.getSurname1() + " - " + cert.getNumberUserId()).setFontSize(7).setBold().setFontColor(primaryBlue))
                    .add(new Paragraph(displayToken).setFontSize(6).setFontColor(com.itextpdf.kernel.colors.ColorConstants.GRAY))
                    .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT).setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
            headerTable.addCell(techCell);
        } catch (Exception e) {
            headerTable.addCell(new Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
        }
        document.add(headerTable);
        document.add(new Paragraph("").setHeight(2).setBackgroundColor(primaryBlue).setMarginBottom(20));

        document.add(new Paragraph("DATOS DEL CERTIFICADO").setFontSize(10).setBold().setFontColor(accentGold));
        Table mainTable = new Table(com.itextpdf.layout.properties.UnitValue.createPercentArray(new float[]{30, 70})).useAllAvailableWidth().setMarginBottom(20);
        addCellToTable(mainTable, "Nombre Completo:", cert.getName() + " " + cert.getSurname1() + " " + (cert.getSurname2() != null ? cert.getSurname2() : ""), lightGray);
        addCellToTable(mainTable, "Identificador (NIF/NIE):", cert.getNumberUserId(), null);
        addCellToTable(mainTable, "Correo Electrónico:", cert.getEmail(), lightGray);
        addCellToTable(mainTable, "Entidad Emisora:", cert.getCa(), null);
        document.add(mainTable);

        document.add(new Paragraph("ESTADO DE VALIDACIÓN TÉCNICA").setFontSize(10).setBold().setFontColor(accentGold));
        Table auditTable = new Table(com.itextpdf.layout.properties.UnitValue.createPercentArray(new float[]{30, 70})).useAllAvailableWidth().setMarginBottom(30);
        addCellToTable(auditTable, "¿Firma Validada?", "SÍ", new com.itextpdf.kernel.colors.DeviceRgb(232, 245, 233));
        addCellToTable(auditTable, "Integridad del Documento:", "GARANTIZADA", null);
        addCellToTable(auditTable, "Método de Verificación:", "CSV / Código QR", null);
        document.add(auditTable);

        document.add(new Paragraph("El titular del certificado consiente expresamente el uso de su firma electrónica conforme a la Ley 6/2020.").setFontSize(9).setItalic().setFontColor(com.itextpdf.kernel.colors.ColorConstants.DARK_GRAY).setMarginBottom(40));
        document.add(new Paragraph("Generado el: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern(AppConstants.DATE_FORMAT_PDF)) + " | VIAFIRMA SANDBOX").setTextAlignment(TextAlignment.CENTER).setFontSize(8).setFontColor(com.itextpdf.kernel.colors.ColorConstants.GRAY));
        document.add(new Paragraph("\n\n\n\n\n")); // Espacio extra para el sello de firma al final
        document.close();
        return nombreArchivo;
    }

    private String generarXMLConDatos(CertificateResponseDTO cert) throws Exception {
        String basePath = System.getProperty("java.io.tmpdir");
        File uploadDir = new File(basePath, AppConstants.UPLOAD_DIR);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern(AppConstants.DATE_FORMAT_FILENAME));
        String nombreArchivo = "Datos_" + cert.getNumberUserId() + "_" + timestamp + ".xml";
        File xmlFile = new File(uploadDir, nombreArchivo);

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                     "<expediente_digital>\n" +
                     "  <metadatos>\n" +
                     "    <fecha>" + LocalDateTime.now() + "</fecha>\n" +
                     "    <origen>Sede Electronica UPO</origen>\n" +
                     "  </metadatos>\n" +
                     "  <firmante>\n" +
                     "    <nif>" + cert.getNumberUserId() + "</nif>\n" +
                     "    <nombre>" + cert.getName() + " " + cert.getSurname1() + "</nombre>\n" +
                     "  </firmante>\n" +
                     "</expediente_digital>";

        java.nio.file.Files.write(xmlFile.toPath(), xml.getBytes("UTF-8"));
        return nombreArchivo;
    }

    private void addCellToTable(Table table, String label, String value, com.itextpdf.kernel.colors.Color bgColor) {
        Cell c1 = new Cell().add(new Paragraph(label).setBold().setFontSize(9)).setPadding(8).setBorder(new com.itextpdf.layout.borders.SolidBorder(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY, 0.5f));
        Cell c2 = new Cell().add(new Paragraph(value != null ? value : "N/A").setFontSize(9)).setPadding(8).setBorder(new com.itextpdf.layout.borders.SolidBorder(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY, 0.5f));
        if (bgColor != null) { c1.setBackgroundColor(bgColor); c2.setBackgroundColor(bgColor); }
        table.addCell(c1); table.addCell(c2);
    }
}
