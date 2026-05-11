<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.example.proyecto_ae.config.AppConstants"%>
<%@page import="org.openapitools.client.model.CertificateResponseDTO"%>
<%
    CertificateResponseDTO userCert = (CertificateResponseDTO) session.getAttribute("certificado");
    boolean isAuthenticated = (userCert != null);
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Sede Electrónica - Portal de Servicios</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/estilos.css?v=3">
</head>
<body>
    <div class="hero">
        <div class="container">
            <h1>Sede Electrónica</h1>
            <p>Gestión segura de trámites y verificación de documentos electrónicos</p>
            
            <% if (isAuthenticated) { %>
                <div class="user-welcome">
                    Conectado como: <b><%= userCert.getName() %> <%= userCert.getSurname1() %></b> 
                    (<%= userCert.getNumberUserId() %>)
                </div>
                <div style="margin-top: 10px;">
                    <a href="LogoutServlet" style="color: white; font-size: 0.8em; opacity: 0.8; text-decoration: none;">Cerrar Sesión</a>
                </div>
            <% } %>
        </div>
    </div>

    <div class="services-grid">
        
        <% if (!isAuthenticated) { %>
            <!-- BLOQUE IDENTIFICACIÓN -->
            <div class="service-card" style="grid-column: 1 / -1; text-align: center;">
                <div class="content">
                    <div class="service-title">Identificación Ciudadana</div>
                    <p class="service-desc">Para acceder a los trámites de firma y registro, debe identificarse primero mediante su Certificado Digital.</p>
                </div>
                <div style="text-align: center;">
                    <a href="LoginServlet" class="btn btn-primary" style="min-width: 250px;">IDENTIFICARSE AHORA</a>
                </div>
            </div>
        <% } else { %>
            <!-- BLOQUE FIRMA -->
            <div class="service-card" style="border-top: 4px solid var(--accent);">
                <div class="content">
                    <div class="service-title">Firmar Documento</div>
                    <p class="service-desc">Inicie el proceso de firma de su solicitud oficial y obtenga el recibo de registro electrónico.</p>
                </div>
                <a href="datos.jsp" class="btn btn-primary">FIRMAR AHORA</a>
            </div>
        <% } %>

        <!-- VALIDADOR -->
        <div class="service-card">
            <div class="content">
                <div class="service-title">Validador de Firmas</div>
                <p class="service-desc">Verifique si un documento firmado es auténtico y consulte la validez de los certificados y la integridad del archivo.</p>
            </div>
            <a href="validar.jsp" class="btn btn-orange">ABRIR VALIDADOR</a>
        </div>
        
        <!-- REGISTRO -->
        <div class="service-card">
            <div class="content">
                <div class="service-title">Libro de Registro</div>
                <p class="service-desc">Consulte públicamente los asientos registrados en la sede de forma transparente y oficial.</p>
            </div>
            <a href="registro.jsp" class="btn btn-secondary" style="background: #10b981;">VER REGISTROS</a>
        </div>

        <!-- MAPA -->
        <div class="service-card">
            <div class="content">
                <div class="service-title">Puntos de Atención</div>
                <p class="service-desc">Localización de oficinas físicas para trámites presenciales y registro oficial en nuestras sedes.</p>
            </div>
            <a href="mapa.jsp" class="btn btn-secondary" style="background: #64748b;">VER MAPA</a>
        </div>
    </div>

    <div style="text-align: center; color: #94a3b8; font-size: 0.9em; margin-bottom: 40px;">
        <p>Este portal cumple con el Esquema Nacional de Seguridad (ENS).</p>
        <p><a href="privacidad.jsp" style="color: #3b82f6; text-decoration: none;">Privacidad</a> | <a href="mapa.jsp" style="color: #3b82f6; text-decoration: none;">Sedes</a></p>
    </div>

    <footer>
        <div class="container">
            <p>&copy; 2026 Sede Electrónica - Administración Pública. Todos los derechos reservados.</p>
        </div>
    </footer>
</body>
</html>