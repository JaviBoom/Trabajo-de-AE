<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Error</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/estilos.css">
</head>
<body class="contenedor-error">
    <div class="error-box">
        <div class="icono-error">⚠</div>
        <h2>Ocurrió un error</h2>
        
        <div class="mensaje-error">
            <%
                String error = (String) request.getAttribute("error");
                if (error != null && !error.isEmpty()) {
            %>
                <p><%= error %></p>
            <%
                } else {
            %>
                <p>Lo sentimos, algo salió mal. Por favor, intenta de nuevo.</p>
            <%
                }
            %>
        </div>

        <div class="botones-error">
            <button class="btn-reintentar" onclick="window.history.back()">
                ← Reintentar
            </button>
            <button class="btn-inicio" onclick="window.location.href='index.jsp'">
                🏠 Volver al Inicio
            </button>
        </div>

        <div class="debug-info" style="margin-top: 30px; padding-top: 20px; border-top: 1px solid #ccc;">
            <p style="font-size: 12px; color: #666;">
                Si el problema persiste, contacta al administrador del sistema.
            </p>
        </div>
    </div>

</body>
</html>