<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="org.openapitools.client.model.CertificateResponseDTO"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Formulario de Datos - Sede Electrónica</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/estilos.css">
    <style>
        .datos-container { max-width: 900px; margin: 0 auto; padding: 30px 20px; }
        .nav-bar { display: flex; gap: 8px; background: white; padding: 8px; border-radius: 16px; margin-bottom: 25px; box-shadow: 0 4px 15px rgba(0,0,0,0.06); border: 1px solid #e2e8f0; flex-wrap: wrap; }
        .nav-link { padding: 10px 18px; border-radius: 10px; font-size: 13px; font-weight: 600; color: #475569; text-decoration: none; transition: all 0.2s; }
        .nav-link:hover { background: #f1f5f9; }
        .nav-link.active { background: #2563eb; color: white; }

        .section-title { font-size: 1rem; color: #1e293b; margin: 28px 0 12px 0; padding-bottom: 8px; border-bottom: 2px solid #f1f5f9; }
        .scsp-box { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 14px; padding: 20px; margin: 15px 0; }
        .scsp-box h4 { margin: 0 0 6px 0; font-size: 13px; color: #1e293b; }
        .scsp-box p { margin: 0 0 12px 0; font-size: 12px; color: #64748b; }
        .scsp-option { display: flex; align-items: flex-start; gap: 8px; margin: 8px 0; font-size: 13px; color: #334155; }
        .scsp-option input[type="radio"] { margin-top: 2px; }
        .rgpd-consent { background: #eff6ff; border: 1px solid #bfdbfe; border-radius: 14px; padding: 18px; margin: 15px 0; }
        .rgpd-consent label { font-size: 13px; color: #1e40af; display: flex; align-items: flex-start; gap: 8px; }
        .legal-ref-mini { font-size: 11px; color: #94a3b8; margin-top: 4px; display: block; }
    </style>
</head>
<body style="background: linear-gradient(135deg, #f8fafc 0%, #eff6ff 50%, #f1f5f9 100%); min-height: 100vh;">
    <div class="datos-container">

        <div class="nav-bar">
            <a href="index.jsp" class="nav-link">🏠 Inicio</a>
            <a href="mapa.jsp" class="nav-link">📍 Mapa de Sedes</a>
            <a href="registro.jsp" class="nav-link">📋 Registro Electrónico</a>
            <a href="privacidad.jsp" class="nav-link">🔒 Privacidad</a>
            <span class="nav-link active" style="margin-left:auto; cursor:default;">📝 Formulario</span>
        </div>

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
        <div class="info-chip">✅ Certificado verificado</div>
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

            <div class="campo">
                <label>Correo Electrónico:</label>
                <input type="text" value="<%= email %>" readonly>
            </div>
        </div>

        <form action="FirmaServlet" method="POST">

            <!-- SECCIÓN SCSP - Sustitución de Certificados en Soporte Papel -->
            <h3 class="section-title">📄 Verificación de Datos (SCSP)</h3>
            <p style="font-size: 13px; color: #64748b; margin-bottom: 15px;">Conforme al RD 522/2006 y RD 523/2006, la Administración puede verificar sus datos sin necesidad de aportar documentación en papel.</p>

            <div class="scsp-box">
                <h4>Datos de Identidad</h4>
                <p>Verificación telemática de datos de identidad del interesado (Decreto 68/2008, Junta de Andalucía).</p>
                <div class="scsp-option">
                    <input type="radio" name="scsp_identidad" value="consiento" id="scsp_id_si" checked>
                    <label for="scsp_id_si">Doy mi consentimiento para la consulta telemática de mis datos de identidad.</label>
                </div>
                <div class="scsp-option">
                    <input type="radio" name="scsp_identidad" value="no_consiento" id="scsp_id_no">
                    <label for="scsp_id_no">No doy mi consentimiento y aporto fotocopia autenticada del DNI/NIE.</label>
                </div>
            </div>

            <div class="scsp-box">
                <h4>Datos de Residencia</h4>
                <p>Verificación telemática del certificado de empadronamiento.</p>
                <div class="scsp-option">
                    <input type="radio" name="scsp_residencia" value="consiento" id="scsp_res_si" checked>
                    <label for="scsp_res_si">Doy mi consentimiento para la consulta de mis datos de residencia.</label>
                </div>
                <div class="scsp-option">
                    <input type="radio" name="scsp_residencia" value="no_consiento" id="scsp_res_no">
                    <label for="scsp_res_no">No doy mi consentimiento y aporto fotocopia autenticada del certificado de empadronamiento.</label>
                </div>
            </div>

            <!-- CONSENTIMIENTO RGPD -->
            <h3 class="section-title">🛡️ Protección de Datos</h3>
            
            <div class="rgpd-consent">
                <label>
                    <input type="checkbox" name="consent_rgpd" value="on" required>
                    He leído y acepto la <a href="privacidad.jsp" target="_blank" style="color:#1d4ed8; font-weight:600;">Política de Privacidad</a> conforme al Reglamento (UE) 2016/679 (RGPD) y la Ley Orgánica 3/2018 (LOPDGDD).
                    <span class="legal-ref-mini">Art. 6.1.a RGPD — Consentimiento del interesado</span>
                </label>
            </div>

            <!-- CONSENTIMIENTO FIRMA -->
            <h3 class="section-title">✍️ Consentimiento de Firma</h3>

            <div class="campo">
                <label>
                    <input type="checkbox" name="consent" value="on" required>
                    Consiente la verificación de identidad y la firma electrónica del expediente.
                </label>
            </div>
            
            <div class="campo">
                <p style="font-size: 14px; color: #475569;">Deseo participar en el procedimiento de PRUEBA. A tal efecto, firmo este documento conforme a la Ley 6/2020, reguladora de los servicios electrónicos de confianza.</p>
                <input type="hidden" name="participacion" value="on">
            </div>

            <div style="text-align: center; margin-top: 30px; display: flex; justify-content: center; gap: 12px;">
                <a href="index.jsp" class="btn-cancelar" style="text-decoration: none; display: inline-flex; align-items: center;">← Volver</a>
                <button type="submit" class="btn-firmar">Generar y Firmar Expediente Digital</button>
            </div>
        </form>
    </div>
    </div>
</body>
</html>