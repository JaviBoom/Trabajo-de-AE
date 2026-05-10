<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="com.example.proyecto_ae.config.AppConstants"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Expediente Digital Dual</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/estilos.css">
    <style>
        .firma-resultado {
            background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
            min-height: 100vh; padding: 40px 20px; font-family: 'Inter', sans-serif;
        }
        .container { max-width: 950px; margin: 0 auto; background: white; border-radius: 20px; box-shadow: 0 15px 40px rgba(0,0,0,0.12); overflow: hidden; border: 1px solid #e2e8f0; }
        .header { background: #1e293b; color: white; padding: 35px; text-align: center; }
        .success-badge { background: #10b981; color: white; padding: 6px 18px; border-radius: 20px; font-size: 13px; display: inline-block; margin-bottom: 12px; font-weight: bold; }
        .content { padding: 45px; }
        .dual-box { display: flex; gap: 25px; margin-top: 35px; }
        .file-card { flex: 1; border: 1px solid #e2e8f0; border-radius: 18px; padding: 30px; text-align: center; transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); background: #fcfdfe; }
        .file-card:hover { transform: translateY(-8px); border-color: #3b82f6; box-shadow: 0 10px 25px rgba(59, 130, 246, 0.1); }
        .file-icon { font-size: 45px; margin-bottom: 20px; }
        .btn-download { display: block; background: #2563eb; color: white !important; text-decoration: none; padding: 14px; border-radius: 12px; margin-top: 20px; font-weight: 600; font-size: 15px; box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2); }
        .btn-xml { background: #475569; }
        .footer-actions { margin-top: 45px; border-top: 1px solid #f1f5f9; padding-top: 35px; text-align: center; display: flex; justify-content: center; gap: 20px; }
        .btn-secondary { background: #f8fafc; color: #1e293b; text-decoration: none; padding: 14px 30px; border-radius: 12px; font-weight: 600; border: 1px solid #e2e8f0; transition: background 0.2s; }
        .btn-secondary:hover { background: #f1f5f9; }
        .preview-box { margin-top: 40px; border-radius: 15px; overflow: hidden; border: 1px solid #e2e8f0; }
    </style>
</head>
<body class="firma-resultado">
    <%
        Boolean completado = (Boolean) session.getAttribute(AppConstants.SESSION_FIRMA_COMPLETADA);
        String pdfName = (String) session.getAttribute(AppConstants.SESSION_PDF_GENERADO);
        String xmlName = (String) session.getAttribute("SESSION_XML_GENERADO");
        String pdfLink = (String) session.getAttribute("SESSION_PDF_LINK");
        String xmlLink = (String) session.getAttribute("SESSION_XML_LINK");
        String token = (String) session.getAttribute(AppConstants.SESSION_FIRMA_TOKEN);
        
        if (completado == null || !completado) { response.sendRedirect("index.jsp"); return; }
        
        String validationUrl = "https://sandbox.viafirma.com/sign-page/v/" + token;
    %>
    <div class="container">
        <div class="header">
            <div class="success-badge">PROCESO FINALIZADO CON ÉXITO</div>
            <h1 style="font-size: 32px; margin-bottom: 10px;">Expediente Digital Dual</h1>
            <p style="opacity: 0.9; font-weight: 300;">Los documentos han sido firmados y validados por la plataforma Viafirma.</p>
        </div>
        
        <div class="content">
            <h3 style="color: #334155; border-bottom: 2px solid #f1f5f9; padding-bottom: 15px; margin-bottom: 30px;">📄 Documentos del Expediente</h3>
            
            <div class="dual-box">
                <!-- Tarjeta PDF -->
                <div class="file-card">
                    <div class="file-icon">📕</div>
                    <h4 style="margin-bottom: 8px;">Certificado PDF</h4>
                    <p style="font-size: 13px; color: #64748b; margin-bottom: 15px;"><%= pdfName %></p>
                    <div style="background: #eff6ff; color: #1d4ed8; font-size: 11px; padding: 4px 10px; border-radius: 10px; display: inline-block; font-weight: 600;">ESTÁNDAR PAdES</div>
                    <a href="<%= pdfLink %>" target="_blank" class="btn-download">Descargar PDF Firmado</a>
                </div>
                
                <!-- Tarjeta XML -->
                <div class="file-card">
                    <div class="file-icon">📑</div>
                    <h4 style="margin-bottom: 8px;">Metadatos XML</h4>
                    <p style="font-size: 13px; color: #64748b; margin-bottom: 15px;"><%= xmlName %></p>
                    <div style="background: #f1f5f9; color: #475569; font-size: 11px; padding: 4px 10px; border-radius: 10px; display: inline-block; font-weight: 600;">ESTÁNDAR XAdES</div>
                    <a href="<%= xmlLink %>" target="_blank" class="btn-download btn-xml">Descargar XML Firmado</a>
                </div>
            </div>

            <div class="preview-box">
                <div style="background: #f8fafc; padding: 12px 20px; border-bottom: 1px solid #e2e8f0; display: flex; justify-content: space-between; align-items: center;">
                    <span style="font-size: 14px; font-weight: 600; color: #475569;">📋 Vista Previa del Documento</span>
                </div>
                <div style="padding: 50px; text-align: center; background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);">
                    <div style="font-size: 64px; margin-bottom: 20px;">📄</div>
                    <p style="font-size: 15px; color: #475569; margin-bottom: 8px; font-weight: 600;"><%= pdfName %></p>
                    <p style="font-size: 13px; color: #94a3b8; margin-bottom: 25px;">Documento firmado digitalmente con certificado cualificado</p>
                    <a href="<%= pdfLink %>" target="_blank" style="display: inline-block; background: #2563eb; color: white; text-decoration: none; padding: 14px 35px; border-radius: 12px; font-weight: 600; font-size: 15px; box-shadow: 0 4px 15px rgba(37, 99, 235, 0.3); transition: transform 0.2s;">
                        Abrir Vista Previa ↗
                    </a>
                </div>
            </div>

            <div style="margin-top: 40px; background: #fffbeb; padding: 25px; border-radius: 18px; border: 1px solid #fde68a; display: flex; gap: 20px; align-items: center;">
                <div style="font-size: 30px;">🛡️</div>
                <div>
                    <h4 style="color: #92400e; margin: 0 0 5px 0;">Seguridad y Garantía Técnica</h4>
                    <p style="font-size: 14px; color: #b45309; line-height: 1.5; margin: 0;">
                        Ambos archivos han sido sellados digitalmente con un sello de tiempo cualificado. 
                        La integridad de los datos está garantizada por la Autoridad de Certificación.
                    </p>
                </div>
            </div>

            <div class="footer-actions">
                <a href="<%= validationUrl %>" target="_blank" class="btn-secondary" style="background: #1e293b; color: white; border: none;">✓ Validar en Viafirma</a>
                <a href="index.jsp" class="btn-secondary">🏠 Finalizar y Salir</a>
            </div>
        </div>
    </div>
</body>
</html>