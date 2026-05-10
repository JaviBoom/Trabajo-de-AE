<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Política de Privacidad - Sede Electrónica</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/estilos.css">
    <style>
        .priv-container { max-width: 900px; margin: 0 auto; padding: 30px 20px; }
        .nav-bar { display: flex; gap: 8px; background: white; padding: 8px; border-radius: 16px; margin-bottom: 25px; box-shadow: 0 4px 15px rgba(0,0,0,0.06); border: 1px solid #e2e8f0; flex-wrap: wrap; }
        .nav-link { padding: 10px 18px; border-radius: 10px; font-size: 13px; font-weight: 600; color: #475569; text-decoration: none; transition: all 0.2s; }
        .nav-link:hover { background: #f1f5f9; }
        .nav-link.active { background: #2563eb; color: white; }

        .priv-card { background: white; border-radius: 18px; padding: 40px; border: 1px solid #e2e8f0; box-shadow: 0 8px 25px rgba(0,0,0,0.06); margin-bottom: 20px; }
        .priv-card h1 { font-size: 1.8rem; color: #1e293b; margin: 0 0 8px 0; }
        .priv-card h2 { font-size: 1.15rem; color: #1e293b; margin: 30px 0 12px 0; padding-bottom: 8px; border-bottom: 2px solid #f1f5f9; }
        .priv-card p, .priv-card li { font-size: 14px; color: #475569; line-height: 1.7; }
        .priv-card ul { padding-left: 20px; }
        .priv-card li { margin-bottom: 6px; }
        .priv-badge { display: inline-block; background: #fef2f2; color: #991b1b; padding: 5px 14px; border-radius: 8px; font-size: 12px; font-weight: 600; margin-bottom: 15px; }
        .legal-ref { background: #f8fafc; border-left: 3px solid #2563eb; padding: 12px 16px; border-radius: 0 10px 10px 0; margin: 15px 0; font-size: 13px; color: #334155; }
        .derechos-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 12px; margin-top: 15px; }
        .derecho-item { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 12px; padding: 16px; text-align: center; }
        .derecho-item .emoji { font-size: 24px; margin-bottom: 8px; }
        .derecho-item h4 { margin: 0 0 4px 0; font-size: 13px; color: #1e293b; }
        .derecho-item p { margin: 0; font-size: 11px; color: #64748b; }
    </style>
</head>
<body style="background: linear-gradient(135deg, #f8fafc 0%, #eff6ff 50%, #f1f5f9 100%); min-height: 100vh;">
    <div class="priv-container">

        <div class="nav-bar">
            <a href="index.jsp" class="nav-link">🏠 Inicio</a>
            <a href="mapa.jsp" class="nav-link">📍 Mapa de Sedes</a>
            <a href="registro.jsp" class="nav-link">📋 Registro Electrónico</a>
            <a href="privacidad.jsp" class="nav-link active">🔒 Privacidad</a>
        </div>

        <div class="priv-card">
            <div class="priv-badge">🛡️ RGPD · LOPDGDD</div>
            <h1>Política de Privacidad y Protección de Datos</h1>
            <p>En cumplimiento del <strong>Reglamento (UE) 2016/679</strong> (RGPD) y la <strong>Ley Orgánica 3/2018</strong> (LOPDGDD), le informamos sobre el tratamiento de sus datos personales.</p>

            <!-- 1. RESPONSABLE -->
            <h2>1. Responsable del Tratamiento</h2>
            <p><strong>Identidad:</strong> Universidad Pablo de Olavide (UPO)</p>
            <p><strong>Dirección:</strong> Ctra. de Utrera, km 1 · 41013 Sevilla</p>
            <p><strong>Contacto DPD:</strong> dpd@upo.es (Delegado de Protección de Datos)</p>
            <div class="legal-ref">
                <strong>Art. 13 RGPD:</strong> El responsable del tratamiento facilitará al interesado la información relativa al tratamiento en el momento de la recogida de los datos.
            </div>

            <!-- 2. FINALIDAD -->
            <h2>2. Finalidad del Tratamiento</h2>
            <p>Los datos obtenidos a través de su certificado digital se utilizan <strong>exclusivamente</strong> para:</p>
            <ul>
                <li>Verificar su identidad mediante certificado digital cualificado (FNMT/CERES).</li>
                <li>Generar el expediente digital (PDF + XML) con firma electrónica avanzada.</li>
                <li>Emitir asientos en el Registro Electrónico de Entrada.</li>
                <li>Cumplir con las obligaciones legales establecidas en la Ley 39/2015.</li>
            </ul>
            <div class="legal-ref">
                <strong>Art. 5.1.b RGPD — Limitación de la finalidad:</strong> Los datos personales serán recogidos con fines determinados, explícitos y legítimos, y no serán tratados ulteriormente de manera incompatible con dichos fines.
            </div>

            <!-- 3. BASE JURÍDICA -->
            <h2>3. Base Jurídica</h2>
            <p>El tratamiento se fundamenta en las siguientes bases legales:</p>
            <ul>
                <li><strong>Art. 6.1.a RGPD:</strong> Consentimiento expreso del interesado (checkbox obligatorio).</li>
                <li><strong>Art. 6.1.c RGPD:</strong> Cumplimiento de obligación legal (Ley 39/2015, Ley 6/2020).</li>
                <li><strong>Art. 6.1.e RGPD:</strong> Misión de interés público (gestión administrativa).</li>
            </ul>

            <!-- 4. DATOS TRATADOS -->
            <h2>4. Categorías de Datos Tratados</h2>
            <p>Se tratan <strong>exclusivamente</strong> los datos contenidos en su certificado digital:</p>
            <ul>
                <li>Nombre y apellidos.</li>
                <li>Número de identificación (NIF/NIE).</li>
                <li>Correo electrónico (si está incluido en el certificado).</li>
                <li>Entidad emisora del certificado.</li>
            </ul>
            <div class="legal-ref">
                <strong>Art. 5.1.c RGPD — Minimización de datos:</strong> Los datos personales serán adecuados, pertinentes y limitados a lo necesario en relación con los fines para los que son tratados.
            </div>

            <!-- 5. CONSERVACIÓN -->
            <h2>5. Periodo de Conservación</h2>
            <p>Los datos de sesión se eliminan al cerrar la sesión del navegador. Los documentos firmados se conservan según los plazos establecidos por la normativa de archivos y patrimonio documental aplicable.</p>

            <!-- 6. DERECHOS -->
            <h2>6. Derechos del Interesado (ARCO+)</h2>
            <p>Conforme al RGPD, usted tiene derecho a:</p>

            <div class="derechos-grid">
                <div class="derecho-item">
                    <div class="emoji">👁️</div>
                    <h4>Acceso</h4>
                    <p>Art. 15 RGPD</p>
                </div>
                <div class="derecho-item">
                    <div class="emoji">✏️</div>
                    <h4>Rectificación</h4>
                    <p>Art. 16 RGPD</p>
                </div>
                <div class="derecho-item">
                    <div class="emoji">🗑️</div>
                    <h4>Supresión</h4>
                    <p>Art. 17 RGPD</p>
                </div>
                <div class="derecho-item">
                    <div class="emoji">⏸️</div>
                    <h4>Limitación</h4>
                    <p>Art. 18 RGPD</p>
                </div>
                <div class="derecho-item">
                    <div class="emoji">📦</div>
                    <h4>Portabilidad</h4>
                    <p>Art. 20 RGPD</p>
                </div>
                <div class="derecho-item">
                    <div class="emoji">🚫</div>
                    <h4>Oposición</h4>
                    <p>Art. 21 RGPD</p>
                </div>
            </div>

            <p style="margin-top: 20px;">Para ejercer estos derechos, dirija su solicitud a <strong>dpd@upo.es</strong> adjuntando copia de su DNI/NIE. Tiene derecho a presentar reclamación ante la <strong>Agencia Española de Protección de Datos</strong> (www.aepd.es).</p>

            <!-- 7. SEGURIDAD -->
            <h2>7. Medidas de Seguridad</h2>
            <p>Se aplican las medidas técnicas y organizativas apropiadas conforme al <strong>Esquema Nacional de Seguridad</strong> (ENS, RD 311/2022):</p>
            <ul>
                <li>Comunicaciones cifradas mediante HTTPS/TLS.</li>
                <li>Autenticación con certificado digital cualificado (eIDAS).</li>
                <li>Firma electrónica avanzada con sello de tiempo (PAdES/XAdES).</li>
                <li>Control de acceso basado en sesión con caducidad automática.</li>
            </ul>

            <!-- 8. NORMATIVA -->
            <h2>8. Normativa Aplicable</h2>
            <ul>
                <li>Reglamento (UE) 2016/679 — Reglamento General de Protección de Datos (RGPD)</li>
                <li>Ley Orgánica 3/2018 — Protección de Datos Personales y Derechos Digitales (LOPDGDD)</li>
                <li>Reglamento (UE) 910/2014 — Identificación electrónica y servicios de confianza (eIDAS)</li>
                <li>Ley 6/2020 — Servicios electrónicos de confianza</li>
                <li>Ley 39/2015 — Procedimiento Administrativo Común</li>
                <li>Ley 40/2015 — Régimen Jurídico del Sector Público</li>
                <li>Real Decreto 311/2022 — Esquema Nacional de Seguridad (ENS)</li>
            </ul>

            <div style="margin-top: 30px; text-align: center; color: #94a3b8; font-size: 12px;">
                Última actualización: 10 de mayo de 2026 · <a href="index.jsp" style="color: #2563eb;">Volver a la Sede Electrónica</a>
            </div>
        </div>
    </div>
</body>
</html>
