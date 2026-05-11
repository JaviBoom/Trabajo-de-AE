<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.example.proyecto_ae.config.AppConstants"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Trámite Finalizado</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/estilos.css?v=2">
    <style>
        .menu-container {
            max-width: 600px;
            margin: 0 auto;
            background: white;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
        }
        .download-item {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 15px;
            border-bottom: 1px solid #edf2f7;
            transition: background 0.2s;
        }
        .download-item:hover { background: #f8fafc; }
        .file-label { font-weight: 600; color: #1e293b; }
        .btn-sm {
            padding: 8px 16px;
            font-size: 0.9em;
            border-radius: 6px;
            text-decoration: none;
            font-weight: 600;
        }
        .btn-pdf { background: #fee2e2; color: #991b1b; }
        .btn-xml { background: #f1f5f9; color: #334155; }
        .btn-valide { background: #dcfce7; color: #166534; }
        .icon { margin-right: 10px; }
    </style>
</head>
<body>
    <header>
        <div class="container">
            <h1>Sede Electrónica</h1>
            <p>Confirmación de Trámite y Descargas</p>
        </div>
    </header>

    <div class="container main-content">
        <div class="menu-container">
            <div style="text-align: center; margin-bottom: 30px;">
                <div style="font-size: 48px; color: #22c55e;">COMPLETADO</div>
                <h2 style="margin: 10px 0;">¡Trámite Finalizado!</h2>
                <p style="color: #64748b;">Su solicitud ha sido firmada y registrada correctamente.</p>
            </div>

            <h3 style="border-bottom: 2px solid #3b82f6; padding-bottom: 10px;">Menú de Documentos</h3>
            
            <% if (session.getAttribute("SESSION_PDF_LINK") != null) { %>
            <div class="download-item">
                <span class="file-label">Copia Auténtica (PDF)</span>
                <a href="<%= session.getAttribute("SESSION_PDF_LINK") %>" target="_blank" class="btn-sm btn-pdf">Descargar</a>
            </div>
            <% } %>

            <% if (session.getAttribute("SESSION_XML_LINK") != null) { %>
            <div class="download-item">
                <span class="file-label">Metadatos Firma (XML)</span>
                <a href="<%= session.getAttribute("SESSION_XML_LINK") %>" target="_blank" class="btn-sm btn-xml">Descargar</a>
            </div>
            <% } %>

            <div class="download-item">
                <span class="file-label">Verificación Externa</span>
                <a href="https://valide.redsara.es/valide/" target="_blank" class="btn-sm btn-valide">Ir a VALIDE</a>
            </div>

            <div style="margin-top: 30px; text-align: center;">
                <a href="index.jsp" class="btn btn-primary" style="width: 100%;">VOLVER AL INICIO</a>
            </div>
        </div>
    </div>

    <footer>
        <div class="container">
            <p>&copy; 2026 Sede Electrónica - Administración Pública</p>
        </div>
    </footer>
</body>
</html>