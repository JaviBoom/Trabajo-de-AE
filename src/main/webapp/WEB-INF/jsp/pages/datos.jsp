<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="org.openapitools.client.model.CertificateResponseDTO"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Formulario de Trámite</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/estilos.css?v=2">
</head>
<body>
    <header>
        <h1>Sede Electrónica</h1>
    </header>

    <div class="main-container">
        <div class="nav-bar">
            <a href="index.jsp">INICIO</a>
        </div>

    <%
        CertificateResponseDTO cert = (CertificateResponseDTO) session.getAttribute("certificado");
        if (cert == null) {
            %>
            <p style="color: red;">Error: No hay sesión activa. <a href="index.jsp">Volver</a></p>
            <%
            return;
        }
        String nombre = cert.getName() + " " + cert.getSurname1() + " " + (cert.getSurname2() != null ? cert.getSurname2() : "");
    %>

    <h2>Datos del Solicitante</h2>
    <p>Nombre: <%= nombre %></p>
    <p>NIF: <%= cert.getNumberUserId() %></p>

    <form action="FirmaServlet" method="POST">
        <h3>Opciones de Trámite</h3>
        <p>
            <input type="radio" name="scsp_identidad" value="consiento" checked> Consiento consulta de datos<br>
            <input type="radio" name="scsp_identidad" value="no_consiento"> No consiento
        </p>

        <h3>Consentimiento de Firma</h3>
        <p>
            <input type="checkbox" name="consent_rgpd" required> Acepto privacidad<br>
            <input type="checkbox" name="consent" required> Acepto la firma electrónica
        </p>
        
        <input type="hidden" name="participacion" value="on">
        
        <button type="submit" class="btn btn-orange">FIRMAR DOCUMENTO</button>
    </form>

    <footer>
        <p>Administración Electrónica - 2026</p>
    </footer>
    </div>
</body>
</html>