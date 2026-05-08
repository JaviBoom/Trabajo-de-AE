<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Proyecto Autenticación y Firma</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/estilos.css">
</head>
<body class="contenedor-inicio">
    <div class="hero-card">
        <div class="hero-badge">Administración Electrónica · Firma Digital</div>
        <h1 class="hero-title">Proyecto de autenticación y firma</h1>
        <p class="hero-subtitle">
            Accede con tu certificado digital, completa el formulario y genera el PDF firmado siguiendo el flujo del proyecto.
        </p>

        <div class="hero-actions">
            <form action="LoginServlet" method="GET">
                <button type="submit" class="boton btn-acceder">Acceder con Certificado Digital</button>
            </form>

            <% if (session.getAttribute("certificado") != null) { %>
                <form action="datos.jsp" method="GET">
                    <button type="submit" class="boton btn-continuar">Continuar</button>
                </form>
            <% } else { %>
                <button class="boton btn-bloqueado" onclick="alert('Debes autenticarte primero para continuar.')">Continuar</button>
            <% } %>
        </div>

        <div class="hero-note">
            Flujo preparado para certificado real y compatible con pruebas locales.
        </div>
    </div>

</body>
</html>