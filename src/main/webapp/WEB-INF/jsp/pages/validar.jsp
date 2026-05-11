<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="org.openapitools.client.model.ValidationFileResponseDTO"%>
<%@page import="org.openapitools.client.model.VerifierSignatureDTO"%>
<%@page import="com.example.proyecto_ae.config.AppConstants"%>
<%@page import="java.util.List"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Validador de Firmas - Sede Electrónica</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/estilos.css?v=2">
    <style>
        .validation-form {
            background: #f1f5f9;
            padding: 30px;
            border-radius: 12px;
            text-align: center;
            border: 2px dashed #cbd5e1;
            margin-bottom: 30px;
        }
        .result-card {
            background: white;
            border-radius: 12px;
            padding: 20px;
            box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1);
            margin-top: 20px;
        }
        .status-badge {
            display: inline-block;
            padding: 5px 15px;
            border-radius: 20px;
            font-weight: bold;
            font-size: 0.9em;
        }
        .status-ok { background: #dcfce7; color: #166534; }
        .status-error { background: #fee2e2; color: #991b1b; }
        .signer-info {
            background: #f8fafc;
            padding: 15px;
            border-radius: 8px;
            margin: 10px 0;
            border-left: 4px solid #3b82f6;
        }
    </style>
</head>
<body>
    <header>
        <div class="container">
            <h1>Validador de Documentos</h1>
            <p>Verifique la integridad y validez de sus archivos firmados</p>
        </div>
    </header>

    <div class="container main-content">
        <div class="nav-bar">
            <a href="index.jsp" style="text-decoration: none; color: #3b82f6; font-weight: bold;">← VOLVER AL INICIO</a>
        </div>

        <div class="section-card">
            <h2>Subir archivo para validar</h2>
            <p>Seleccione el archivo PDF o XML que desea verificar. El sistema consultará con la autoridad de firma la validez de los certificados y la integridad del contenido.</p>
            
            <form action="ValidarServlet" method="POST" enctype="multipart/form-data" class="validation-form">
                <input type="file" name="documento" accept=".pdf,.xml" required style="margin-bottom: 20px; display: block; width: 100%;">
                <button type="submit" class="btn btn-primary">VALIDAR FIRMA</button>
            </form>

            <% 
                String error = (String) request.getAttribute(AppConstants.REQUEST_ERROR);
                if (error != null) { %>
                    <div class="status-badge status-error" style="width: 100%; text-align: center; margin-bottom: 20px; padding: 15px; border-radius: 8px;">
                        <%= error %>
                    </div>
            <% } %>

            <% 
                ValidationFileResponseDTO res = (ValidationFileResponseDTO) request.getAttribute("resultado");
                if (res != null) { 
            %>
                <div class="result-card">
                    <h3 style="margin-top: 0; color: #1e3a8a;">Resultado de la Validación</h3>
                    <p>
                        Estado General: 
                        <% if (Boolean.TRUE.equals(res.getSignatureValid())) { %>
                            <span class="status-badge status-ok">VÁLIDO</span>
                        <% } else { %>
                            <span class="status-badge status-error">NO VÁLIDO</span>
                        <% } %>
                    </p>
                    <p>Nombre del Archivo: <b><%= res.getFilename() %></b></p>
                    <p>Código de Operación: <b><%= res.getOperationCode() %></b></p>
                    
                    <% 
                        List<VerifierSignatureDTO> signatures = res.getVerifiedSignatures();
                        if (signatures != null && !signatures.isEmpty()) { 
                    %>
                        <h4 style="margin-top: 25px; border-bottom: 1px solid #e2e8f0; padding-bottom: 5px;">Firmas Detectadas (<%= signatures.size() %>)</h4>
                        <% for (VerifierSignatureDTO sig : signatures) { %>
                            <div class="signer-info">
                                <strong>Firmante:</strong> <%= sig.getSignedBy() != null ? sig.getSignedBy() : "Desconocido" %><br>
                                <strong>Fecha de Firma:</strong> <%= sig.getSigningTime() %><br>
                                <strong>Formato:</strong> <%= sig.getFormat() %><br>
                                <strong>Estado de Firma:</strong> 
                                <span style="color: <%= "VALID".equals(String.valueOf(sig.getValidationStatus())) ? "#166534" : "#991b1b" %>; font-weight: bold;">
                                    <%= sig.getValidationStatus() %>
                                </span>
                            </div>
                        <% } %>
                    <% } else { %>
                        <p style="color: #64748b; font-style: italic;">No se han detectado firmas electrónicas válidas en este documento.</p>
                    <% } %>
                </div>
            <% } %>
        </div>
    </div>

    <footer>
        <div class="container">
            <p>&copy; 2026 Sede Electrónica - Validador Oficial</p>
        </div>
    </footer>
</body>
</html>
