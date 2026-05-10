<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="org.openapitools.client.model.CertificateResponseDTO"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Formulario de Datos</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/estilos.css">
</head>
<body class="contenedor-datos">
    <%
        CertificateResponseDTO cert = (CertificateResponseDTO) session.getAttribute("certificado");
        if (cert == null) {
            %>
            <div style="padding: 20px; color: red;">
                <h2>Error de Sesión</h2>
                <p>El certificado es nulo en la sesión actual. No se pudo recuperar tras la redirección.</p>
                <p>ID de sesión en datos.jsp: <%= session.getId() %></p>
                <p>Por favor, verifica si las cookies de terceros o SameSite están bloqueando la sesión.</p>
                <a href="index.jsp">Volver al inicio</a>
            </div>
            <%
            return;
        }
        
        // Construir nombre completo correctamente
        String nombre = cert.getName() != null ? cert.getName() : "";
        String apellido1 = cert.getSurname1() != null ? cert.getSurname1() : "";
        String apellido2 = cert.getSurname2() != null ? cert.getSurname2() : "";
        String apellidos = (apellido1 + " " + apellido2).trim();
        String nombreCompleto = (nombre + " " + apellidos).trim();
        String nif = cert.getNumberUserId() != null ? cert.getNumberUserId() : "";
        String email = cert.getEmail() != null ? cert.getEmail() : "No disponible";
        String issuer = cert.getCa() != null ? cert.getCa() : "No disponible";
    %>

    <div class="formulario">
        <div class="info-chip">Certificado verificado</div>
        <h2>Datos del usuario autenticado</h2>
        
        <div class="form-grid">
            <div class="campo">
                <label>Nombre:</label>
                <input type="text" value="<%= nombre %>" readonly>
            </div>

            <div class="campo">
                <label>Apellidos:</label>
                <input type="text" value="<%= apellidos %>" readonly>
            </div>

            <div class="campo">
                <label>NIF:</label>
                <input type="text" value="<%= nif %>" readonly>
            </div>
        </div>

        <form action="FirmaServlet" method="POST">
            <div class="campo" style="margin-top: 20px;">
                <label>
                    <input type="checkbox" name="consent" value="on" required>
                    Consiente la verificación de identidad.
                </label>
            </div>
            
            <div class="campo">
                <p>Deseo participar en el procedimiento de PRUEBA, a tal efecto firmó este documento.</p>
                <!-- Oculto para que valide si es necesario en FirmaServlet, o se puede enviar de otra forma. Wait, the mock does not show a checkbox for this second sentence, it's just text. -->
                <input type="hidden" name="participacion" value="on">
            </div>

            <div style="text-align: center; margin-top: 30px;">
                <button type="submit" class="btn-firmar">Firmar</button>
            </div>
        </form>
    </div>

</body>
</html>