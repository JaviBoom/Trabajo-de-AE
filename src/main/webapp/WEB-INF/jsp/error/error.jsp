<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Error - Sede Electrónica</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/estilos.css">
</head>
<body>
    <header>
        <h1>Universidad Pablo de Olavide</h1>
        <p>Error en el Sistema</p>
    </header>

    <div class="main-container">
        <div class="section-card" style="border-color: red;">
            <div class="section-title" style="background-color: red;">Error Detectado</div>
            <p>Se ha producido un error durante el procesamiento de su solicitud:</p>
            
            <div style="background-color: #ffebee; padding: 15px; border-radius: 4px; border: 1px solid #ffcdd2; margin: 20px 0;">
                <%
                    String error = (String) request.getAttribute("error");
                    if (error != null && !error.isEmpty()) {
                %>
                    <p style="color: #b71c1c; font-weight: bold;"><%= error %></p>
                <%
                    } else {
                %>
                    <p>No se pudo completar la operación. Por favor, inténtelo de nuevo más tarde.</p>
                <%
                    }
                %>
            </div>

            <div style="text-align: center;">
                <button class="btn btn-secondary" onclick="window.history.back()">VOLVER ATRÁS</button>
                <a href="index.jsp" class="btn btn-primary">IR AL INICIO</a>
            </div>
        </div>

        <footer>
            <p>&copy; 2026 Universidad Pablo de Olavide</p>
        </footer>
    </div>
</body>
</html>