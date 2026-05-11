<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Libro de Registro - Sede Electrónica</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/estilos.css?v=2">
    <style>
        .registry-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
            background: white;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1);
        }
        .registry-table th {
            background: #1e3a8a;
            color: white;
            padding: 15px;
            text-align: left;
            font-weight: 600;
        }
        .registry-table td {
            padding: 15px;
            border-bottom: 1px solid #e2e8f0;
            color: #475569;
        }
        .registry-table tr:hover { background: #f8fafc; }
        .reg-number { font-family: monospace; font-weight: bold; color: #3b82f6; }
    </style>
</head>
<body>
    <header>
        <div class="container">
            <h1>Libro de Registro General</h1>
            <p>Consulta pública de asientos de entrada y salida electrónica</p>
        </div>
    </header>

    <div class="container main-content">
        <div class="nav-bar">
            <a href="index.jsp" style="text-decoration: none; color: #3b82f6; font-weight: bold;">← VOLVER AL INICIO</a>
        </div>

        <div class="section-card">
            <h2>Asientos Registrados</h2>
            <p>A continuación se muestran los últimos trámites realizados y registrados oficialmente en esta sede electrónica.</p>
            
            <%
                List<Map<String, String>> registros = (List<Map<String, String>>) application.getAttribute("REGISTRO_ELECTRONICO");
                if (registros == null || registros.isEmpty()) {
            %>
                <div style="text-align: center; padding: 40px; color: #94a3b8;">
                    <p>Actualmente no existen asientos en el libro de registro.</p>
                </div>
            <% } else { %>
                <table class="registry-table">
                    <thead>
                        <tr>
                            <th>Número de Asiento</th>
                            <th>Fecha y Hora</th>
                            <th>Tipo de Trámite</th>
                            <th>Interesado / Remitente</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Map<String, String> r : registros) { %>
                        <tr>
                            <td><span class="reg-number"><%= r.get("numero") %></span></td>
                            <td><%= r.get("fecha") %></td>
                            <td><span style="font-size: 0.85em; background: #e2e8f0; padding: 2px 8px; border-radius: 4px;"><%= r.get("tipo") %></span></td>
                            <td><%= r.get("remitente") %></td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            <% } %>
        </div>
    </div>

    <footer>
        <div class="container">
            <p>&copy; 2026 Sede Electrónica - Registro Oficial</p>
        </div>
    </footer>
</body>
</html>
