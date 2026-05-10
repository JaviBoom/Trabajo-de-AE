<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, com.example.proyecto_ae.config.AppConstants"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Registro Electrónico - Sede Electrónica</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/estilos.css">
    <style>
        .reg-container { max-width: 1100px; margin: 0 auto; padding: 30px 20px; }
        .nav-bar { display: flex; gap: 8px; background: white; padding: 8px; border-radius: 16px; margin-bottom: 25px; box-shadow: 0 4px 15px rgba(0,0,0,0.06); border: 1px solid #e2e8f0; flex-wrap: wrap; }
        .nav-link { padding: 10px 18px; border-radius: 10px; font-size: 13px; font-weight: 600; color: #475569; text-decoration: none; transition: all 0.2s; }
        .nav-link:hover { background: #f1f5f9; }
        .nav-link.active { background: #2563eb; color: white; }

        .reg-header { background: white; border-radius: 18px; padding: 30px; border: 1px solid #e2e8f0; margin-bottom: 20px; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 15px; }
        .reg-header h1 { font-size: 1.5rem; color: #1e293b; margin: 0; }
        .reg-stats { display: flex; gap: 15px; }
        .stat-box { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 12px; padding: 12px 18px; text-align: center; }
        .stat-box .num { font-size: 1.4rem; font-weight: 700; color: #2563eb; }
        .stat-box .label { font-size: 11px; color: #64748b; }

        .reg-table-wrap { background: white; border-radius: 18px; border: 1px solid #e2e8f0; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.04); }
        .reg-table { width: 100%; border-collapse: collapse; font-size: 13px; }
        .reg-table thead { background: #1e293b; color: white; }
        .reg-table th { padding: 14px 16px; text-align: left; font-weight: 600; font-size: 12px; text-transform: uppercase; letter-spacing: 0.05em; }
        .reg-table td { padding: 12px 16px; border-bottom: 1px solid #f1f5f9; color: #334155; }
        .reg-table tbody tr:hover { background: #f8fafc; }
        .reg-table tbody tr:last-child td { border-bottom: none; }
        .tipo-badge { display: inline-block; padding: 3px 10px; border-radius: 6px; font-size: 11px; font-weight: 600; }
        .tipo-entrada { background: #dcfce7; color: #166534; }
        .tipo-salida { background: #dbeafe; color: #1e40af; }
        .empty-state { padding: 60px 20px; text-align: center; color: #94a3b8; }
        .empty-state .icon { font-size: 48px; margin-bottom: 15px; }

        .legal-note { background: #fffbeb; border: 1px solid #fde68a; border-radius: 14px; padding: 18px 22px; margin-top: 20px; display: flex; align-items: flex-start; gap: 12px; }
        .legal-note p { margin: 0; font-size: 13px; color: #92400e; line-height: 1.5; }
    </style>
</head>
<body style="background: linear-gradient(135deg, #f8fafc 0%, #eff6ff 50%, #f1f5f9 100%); min-height: 100vh;">
    <div class="reg-container">

        <div class="nav-bar">
            <a href="index.jsp" class="nav-link">🏠 Inicio</a>
            <a href="mapa.jsp" class="nav-link">📍 Mapa de Sedes</a>
            <a href="registro.jsp" class="nav-link active">📋 Registro Electrónico</a>
            <a href="privacidad.jsp" class="nav-link">🔒 Privacidad</a>
        </div>

        <%
            // Obtener registros de la aplicación (almacenados en ServletContext)
            @SuppressWarnings("unchecked")
            List<Map<String, String>> registros = (List<Map<String, String>>) application.getAttribute("REGISTRO_ELECTRONICO");
            if (registros == null) registros = new ArrayList<>();
            int totalEntradas = 0, totalSalidas = 0;
            for (Map<String, String> r : registros) {
                if ("ENTRADA".equals(r.get("tipo"))) totalEntradas++;
                else totalSalidas++;
            }
        %>

        <div class="reg-header">
            <div>
                <h1>📋 Registro Electrónico General</h1>
                <p style="color: #64748b; font-size: 13px; margin: 4px 0 0 0;">Libro de asientos registrales · Art. 16 Ley 39/2015</p>
            </div>
            <div class="reg-stats">
                <div class="stat-box">
                    <div class="num"><%= registros.size() %></div>
                    <div class="label">Total Asientos</div>
                </div>
                <div class="stat-box">
                    <div class="num"><%= totalEntradas %></div>
                    <div class="label">Entradas</div>
                </div>
                <div class="stat-box">
                    <div class="num"><%= totalSalidas %></div>
                    <div class="label">Salidas</div>
                </div>
            </div>
        </div>

        <div class="reg-table-wrap">
            <% if (registros.isEmpty()) { %>
                <div class="empty-state">
                    <div class="icon">📭</div>
                    <h3 style="color: #475569; margin: 0 0 8px 0;">No hay asientos registrales</h3>
                    <p>Los asientos se generan automáticamente al firmar un expediente digital.</p>
                    <a href="index.jsp" style="display: inline-block; margin-top: 15px; background: #2563eb; color: white; padding: 10px 24px; border-radius: 10px; font-weight: 600; font-size: 13px; text-decoration: none;">Iniciar un Trámite</a>
                </div>
            <% } else { %>
                <table class="reg-table">
                    <thead>
                        <tr>
                            <th>Nº Registro</th>
                            <th>Fecha/Hora</th>
                            <th>Tipo</th>
                            <th>Remitente</th>
                            <th>NIF</th>
                            <th>Descripción</th>
                            <th>Documentos</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (int i = registros.size() - 1; i >= 0; i--) {
                            Map<String, String> r = registros.get(i);
                        %>
                        <tr>
                            <td><strong><%= r.get("numero") %></strong></td>
                            <td><%= r.get("fecha") %></td>
                            <td><span class="tipo-badge <%= "ENTRADA".equals(r.get("tipo")) ? "tipo-entrada" : "tipo-salida" %>"><%= r.get("tipo") %></span></td>
                            <td><%= r.get("remitente") %></td>
                            <td><%= r.get("nif") %></td>
                            <td><%= r.get("descripcion") %></td>
                            <td><%= r.get("documentos") %></td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            <% } %>
        </div>

        <div class="legal-note">
            <div style="font-size: 22px;">⚖️</div>
            <div>
                <p><strong>Art. 16 Ley 39/2015:</strong> Cada Administración dispondrá de un Registro Electrónico General en el que se hará el correspondiente asiento de todo documento que sea presentado o que se reciba. Los registros electrónicos de todas las Administraciones deberán ser interoperables entre sí (sistema @ries en la Junta de Andalucía).</p>
            </div>
        </div>

        <div style="text-align: center; margin-top: 20px; color: #94a3b8; font-size: 12px;">
            Registro conforme al Art. 16 Ley 39/2015 · Interoperable con @ries (Junta de Andalucía) · <a href="index.jsp" style="color: #2563eb;">Volver a Inicio</a>
        </div>
    </div>
</body>
</html>
