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
        // Verificar que el usuario está autenticado
        CertificateResponseDTO cert = (CertificateResponseDTO) session.getAttribute("certificado");
        if (cert == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        
        // Construir nombre completo correctamente
        String nombre = cert.getName() != null ? cert.getName() : "";
        String apellido1 = cert.getSurname1() != null ? cert.getSurname1() : "";
        String nombreCompleto = (nombre + " " + apellido1).trim();
        String nif = cert.getNumberUserId() != null ? cert.getNumberUserId() : "";
        String email = cert.getEmail() != null ? cert.getEmail() : "No disponible";
        String issuer = cert.getCa() != null ? cert.getCa() : "No disponible";
    %>

    <div class="formulario">
        <div class="info-chip">Certificado verificado</div>
        <h2>Datos del usuario autenticado</h2>
        <p class="hero-subtitle" style="margin-left:0; margin-bottom: 18px; text-align:left;">
            Revisa los datos recuperados del certificado y completa el motivo de participación.
        </p>

        <div class="form-grid">
            <div class="campo">
                <label>Nombre y Apellidos</label>
                <input type="text" value="<%= nombreCompleto %>" readonly>
            </div>

            <div class="campo">
                <label>NIF/NIE</label>
                <input type="text" value="<%= nif %>" readonly>
            </div>

            <div class="campo">
                <label>Email</label>
                <input type="text" value="<%= email %>" readonly>
            </div>

            <div class="campo">
                <label>Emisor del Certificado</label>
                <input type="text" value="<%= issuer %>" readonly>
            </div>
        </div>

        <h3>Formulario de participación</h3>
        
        <form action="FirmaServlet" method="POST">
            <div class="campo">
                <label for="motivo">Motivo de la solicitud:</label>
                <textarea name="motivo" id="motivo" placeholder="Ej: Participación en el proyecto de modernización AE" required></textarea>
            </div>

            <div class="campo">
                <label>
                    <input type="checkbox" name="consent" value="on" required>
                    Consiento la verificación de identidad.
                </label>
            </div>
            
            <div class="campo">
                <label>
                    <input type="checkbox" name="participacion" value="on" required>
                    Deseo participar en el procedimiento de PRUEBA, a tal efecto firmo este documento.
                </label>
            </div>

            <button type="submit" class="btn-firmar">Generar y firmar documento</button>
            <button type="button" class="btn-cancelar" onclick="window.location.href='index.jsp'">Cancelar</button>
        </form>
    </div>

</body>
</html>