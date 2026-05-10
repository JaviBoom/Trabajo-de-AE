<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Sede Electrónica - Universidad Pablo de Olavide</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/estilos.css">
    <style>
        .sede-container { max-width: 1100px; margin: 0 auto; padding: 30px 20px; }
        .sede-header { background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%); color: white; border-radius: 24px; padding: 45px 40px; text-align: center; margin-bottom: 30px; position: relative; overflow: hidden; }
        .sede-header::before { content: ''; position: absolute; top: -50%; right: -20%; width: 400px; height: 400px; background: radial-gradient(circle, rgba(37,99,235,0.15) 0%, transparent 70%); border-radius: 50%; }
        .sede-header h1 { font-size: 2.2rem; margin: 0 0 8px 0; letter-spacing: -0.03em; position: relative; }
        .sede-header p { opacity: 0.8; font-size: 1rem; margin: 0; position: relative; }
        .sede-badge { display: inline-block; background: rgba(37,99,235,0.2); color: #93c5fd; padding: 6px 16px; border-radius: 20px; font-size: 12px; font-weight: 700; letter-spacing: 0.05em; margin-bottom: 15px; text-transform: uppercase; position: relative; }

        .nav-bar { display: flex; gap: 8px; background: white; padding: 8px; border-radius: 16px; margin-bottom: 30px; box-shadow: 0 4px 15px rgba(0,0,0,0.06); border: 1px solid #e2e8f0; flex-wrap: wrap; }
        .nav-link { padding: 10px 18px; border-radius: 10px; font-size: 13px; font-weight: 600; color: #475569; text-decoration: none; transition: all 0.2s; }
        .nav-link:hover { background: #f1f5f9; color: #1e293b; }
        .nav-link.active { background: #2563eb; color: white; }

        .grid-servicios { display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 20px; margin-bottom: 30px; }
        .servicio-card { background: white; border-radius: 18px; padding: 30px; border: 1px solid #e2e8f0; transition: all 0.3s cubic-bezier(0.4,0,0.2,1); cursor: pointer; text-decoration: none; color: inherit; display: block; }
        .servicio-card:hover { transform: translateY(-6px); box-shadow: 0 12px 30px rgba(0,0,0,0.08); border-color: #3b82f6; }
        .servicio-icon { width: 52px; height: 52px; border-radius: 14px; display: flex; align-items: center; justify-content: center; font-size: 24px; margin-bottom: 18px; }
        .icon-blue { background: #eff6ff; }
        .icon-green { background: #f0fdf4; }
        .icon-amber { background: #fffbeb; }
        .icon-purple { background: #faf5ff; }
        .icon-red { background: #fef2f2; }
        .icon-slate { background: #f8fafc; }
        .servicio-card h3 { font-size: 1.05rem; margin: 0 0 8px 0; color: #1e293b; }
        .servicio-card p { font-size: 13px; color: #64748b; margin: 0; line-height: 1.5; }
        .servicio-tag { display: inline-block; font-size: 11px; font-weight: 600; padding: 3px 8px; border-radius: 6px; margin-top: 12px; }
        .tag-operativo { background: #dcfce7; color: #166534; }
        .tag-info { background: #e0e7ff; color: #3730a3; }

        .info-banner { background: white; border-radius: 18px; padding: 25px 30px; border: 1px solid #e2e8f0; display: flex; align-items: center; gap: 20px; margin-bottom: 20px; }
        .info-banner-icon { font-size: 32px; flex-shrink: 0; }
        .info-banner h4 { margin: 0 0 4px 0; color: #1e293b; font-size: 15px; }
        .info-banner p { margin: 0; color: #64748b; font-size: 13px; }

        .footer-sede { text-align: center; padding: 25px; color: #94a3b8; font-size: 12px; border-top: 1px solid #e2e8f0; margin-top: 20px; }
        .footer-sede a { color: #2563eb; text-decoration: none; }
    </style>
</head>
<body style="background: linear-gradient(135deg, #f8fafc 0%, #eff6ff 50%, #f1f5f9 100%); min-height: 100vh;">
    <div class="sede-container">

        <!-- HEADER INSTITUCIONAL -->
        <div class="sede-header">
            <div class="sede-badge">🏛️ Sede Electrónica</div>
            <h1>Universidad Pablo de Olavide</h1>
            <p>Plataforma de Administración Electrónica · Servicios Digitales al Ciudadano</p>
        </div>

        <!-- BARRA DE NAVEGACIÓN -->
        <div class="nav-bar">
            <a href="index.jsp" class="nav-link active">🏠 Inicio</a>
            <a href="mapa.jsp" class="nav-link">📍 Mapa de Sedes</a>
            <a href="registro.jsp" class="nav-link">📋 Registro Electrónico</a>
            <a href="privacidad.jsp" class="nav-link">🔒 Privacidad</a>
            <% if (session.getAttribute("certificado") != null) { %>
                <a href="datos.jsp" class="nav-link" style="margin-left:auto; background:#dcfce7; color:#166534;">✅ Sesión Activa</a>
            <% } %>
        </div>

        <!-- CATÁLOGO DE TRÁMITES -->
        <h2 style="font-size: 1.3rem; color: #1e293b; margin-bottom: 18px; padding-left: 4px;">📂 Catálogo de Trámites y Servicios</h2>

        <div class="grid-servicios">
            <!-- Trámite Principal: Firma -->
            <a href="LoginServlet" class="servicio-card">
                <div class="servicio-icon icon-blue">🔐</div>
                <h3>Autenticación y Firma Digital</h3>
                <p>Accede con tu certificado digital (FNMT/CERES), completa tu solicitud y firma el expediente dual (PDF + XML).</p>
                <span class="servicio-tag tag-operativo">● Operativo</span>
            </a>

            <!-- Mapa de Sedes -->
            <a href="mapa.jsp" class="servicio-card">
                <div class="servicio-icon icon-green">📍</div>
                <h3>Mapa de Sedes y Oficinas</h3>
                <p>Localiza oficinas de registro, sedes administrativas y puntos de atención al ciudadano en el mapa interactivo.</p>
                <span class="servicio-tag tag-operativo">● Operativo</span>
            </a>

            <!-- Registro Electrónico -->
            <a href="registro.jsp" class="servicio-card">
                <div class="servicio-icon icon-amber">📋</div>
                <h3>Registro Electrónico</h3>
                <p>Consulta el libro de registro de entrada/salida con todos los asientos registrales generados por tus trámites.</p>
                <span class="servicio-tag tag-operativo">● Operativo</span>
            </a>

            <!-- Mi Carpeta -->
            <% if (session.getAttribute("certificado") != null) { %>
            <a href="datos.jsp" class="servicio-card">
                <div class="servicio-icon icon-purple">👤</div>
                <h3>Mi Carpeta Ciudadana</h3>
                <p>Accede a tu expediente personal, revisa tus datos de certificado y gestiona tus solicitudes en curso.</p>
                <span class="servicio-tag tag-operativo">● Operativo</span>
            </a>
            <% } else { %>
            <div class="servicio-card" onclick="alert('Debes autenticarte primero con tu certificado digital.')">
                <div class="servicio-icon icon-purple">👤</div>
                <h3>Mi Carpeta Ciudadana</h3>
                <p>Accede a tu expediente personal. Requiere autenticación con certificado digital.</p>
                <span class="servicio-tag tag-info">🔒 Requiere Certificado</span>
            </div>
            <% } %>

            <!-- Verificación -->
            <a href="https://sandbox.viafirma.com/sign-page/" target="_blank" class="servicio-card">
                <div class="servicio-icon icon-red">✅</div>
                <h3>Verificación de Documentos</h3>
                <p>Valida la autenticidad de un documento firmado electrónicamente mediante su Código Seguro de Verificación (CSV).</p>
                <span class="servicio-tag tag-info">↗ Servicio Externo</span>
            </a>

            <!-- Privacidad -->
            <a href="privacidad.jsp" class="servicio-card">
                <div class="servicio-icon icon-slate">🛡️</div>
                <h3>Protección de Datos (RGPD)</h3>
                <p>Consulta la política de privacidad, tus derechos ARCO y la información sobre el tratamiento de datos personales.</p>
                <span class="servicio-tag tag-info">ℹ Informativo</span>
            </a>
        </div>

        <!-- BANNERS INFORMATIVOS -->
        <div class="info-banner">
            <div class="info-banner-icon">📌</div>
            <div>
                <h4>Horario de Atención Electrónica</h4>
                <p>La Sede Electrónica está disponible 24 horas, 7 días a la semana. Los plazos administrativos se computan según el Art. 31 de la Ley 39/2015.</p>
            </div>
        </div>

        <div class="info-banner">
            <div class="info-banner-icon">⚖️</div>
            <div>
                <h4>Marco Normativo</h4>
                <p>Ley 39/2015 (Procedimiento Administrativo) · Ley 40/2015 (Régimen Jurídico) · Reglamento eIDAS 910/2014 · Ley 6/2020 (Servicios de Confianza)</p>
            </div>
        </div>

        <div class="info-banner" style="background: #f0f9ff; border-color: #bae6fd;">
            <div class="info-banner-icon">🎓</div>
            <div>
                <h4>Notas de Desarrollo Académico</h4>
                <p>Este proyecto ha sido desarrollado como trabajo final para la asignatura de <strong>Administración Electrónica</strong>. Hemos ampliado la funcionalidad básica para incluir conceptos de todo el temario: Registro General (Tema 3), Interoperabilidad SCSP (Tema 4), Protección de Datos RGPD (Tema 5) y Visualización de Datos con Leaflet (EPD 9).</p>
            </div>
        </div>

        <!-- FOOTER -->
        <div class="footer-sede">
            <p>© 2026 Sede Electrónica · Universidad Pablo de Olavide · Administración Electrónica</p>
            <p><a href="privacidad.jsp">Política de Privacidad</a> · <a href="https://www.upo.es" target="_blank">Web Institucional</a> · <a href="registro.jsp">Registro Electrónico</a></p>
        </div>
    </div>
</body>
</html>