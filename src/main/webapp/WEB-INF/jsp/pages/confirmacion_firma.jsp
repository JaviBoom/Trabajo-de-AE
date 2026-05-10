<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="org.openapitools.client.model.ClientAccessResponseDTO"%>
<%@page import="java.net.URLEncoder"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Documento Firmado</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/estilos.css">
    <style>
        .firma-resultado {
            background:
                radial-gradient(circle at top left, rgba(37, 99, 235, 0.18), transparent 35%),
                linear-gradient(135deg, #e0e7ff 0%, #f8fafc 46%, #ecfeff 100%);
            min-height: 100vh;
            padding: 32px 16px;
            display: flex;
            justify-content: center;
            align-items: center;
        }
        
        .firma-box {
            background: rgba(255, 255, 255, 0.92);
            backdrop-filter: blur(14px);
            border: 1px solid rgba(148, 163, 184, 0.22);
            border-radius: 28px;
            box-shadow: 0 24px 60px rgba(15, 23, 42, 0.18);
            padding: 34px;
            max-width: 1040px;
            width: 100%;
        }
        
        .firma-header {
            text-align: center;
            margin-bottom: 40px;
        }
        
        .firma-icon-success {
            width: 92px;
            height: 92px;
            margin: 0 auto 20px;
            display: grid;
            place-items: center;
            border-radius: 50%;
            font-size: 52px;
            color: white;
            background: linear-gradient(135deg, #16a34a 0%, #14b8a6 100%);
            animation: fadeInDown 0.6s ease-in-out;
            box-shadow: 0 18px 34px rgba(22, 163, 74, 0.24);
        }
        
        .firma-header h2 {
            color: #333;
            margin: 0;
            font-size: 28px;
        }
        
        .firma-content {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 30px;
            margin-bottom: 40px;
        }
        
        .firma-section {
            background: rgba(248, 250, 252, 0.9);
            padding: 20px;
            border-radius: 18px;
            border-left: 4px solid #2563eb;
        }
        
        .firma-section h3 {
            margin-top: 0;
            color: #333;
            font-size: 16px;
        }
        
        .firma-section p {
            margin: 10px 0;
            color: #555;
            font-size: 14px;
        }
        
        .cajetin-firma {
            background: linear-gradient(135deg, #2563eb 0%, #4f46e5 100%);
            color: white;
            padding: 20px;
            border-radius: 18px;
            text-align: center;
            margin-top: 20px;
            box-shadow: 0 14px 28px rgba(37, 99, 235, 0.24);
        }
        
        .cajetin-firma h4 {
            margin: 0 0 15px 0;
            font-size: 14px;
        }
        
        .qr-placeholder {
            background-color: white;
            width: 180px;
            height: 180px;
            margin: 15px auto;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 16px;
            font-size: 12px;
            color: #999;
            border: 1px solid rgba(148, 163, 184, 0.35);
            box-shadow: inset 0 1px 2px rgba(15, 23, 42, 0.06);
            overflow: hidden;
        }
        
        .firma-detalles {
            background: rgba(239, 246, 255, 0.82);
            padding: 20px;
            border-radius: 18px;
            border-left: 4px solid #2563eb;
            margin-bottom: 30px;
        }
        
        .firma-detalles h4 {
            color: #0056b3;
            margin: 0 0 15px 0;
        }
        
        .detalles-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 15px;
        }
        
        .detalle-item {
            font-size: 14px;
        }
        
        .detalle-label {
            font-weight: bold;
            color: #333;
        }
        
        .detalle-valor {
            color: #666;
            margin-top: 5px;
        }
        
        .acciones-firma {
            display: flex;
            gap: 15px;
            justify-content: center;
            flex-wrap: wrap;
        }
        
        .btn-descarga {
            background: linear-gradient(135deg, #2563eb 0%, #4f46e5 100%);
            color: white;
            padding: 12px 24px;
            border: none;
            border-radius: 14px;
            cursor: pointer;
            font-size: 16px;
            transition: all 0.3s ease;
            text-decoration: none;
            display: inline-block;
            box-shadow: 0 14px 28px rgba(37, 99, 235, 0.24);
        }
        
        .btn-descarga:hover {
            background-color: #004085;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0, 80, 179, 0.3);
        }
        
        .btn-validar {
            background: linear-gradient(135deg, #16a34a 0%, #0f766e 100%);
            color: white;
            padding: 12px 24px;
            border: none;
            border-radius: 14px;
            cursor: pointer;
            font-size: 16px;
            transition: all 0.3s ease;
            box-shadow: 0 14px 28px rgba(22, 163, 74, 0.24);
        }
        
        .btn-validar:hover {
            background-color: #218838;
            transform: translateY(-2px);
        }
        
        .btn-inicio {
            background: linear-gradient(135deg, #64748b 0%, #475569 100%);
            color: white;
            padding: 12px 24px;
            border: none;
            border-radius: 14px;
            cursor: pointer;
            font-size: 16px;
            transition: all 0.3s ease;
            box-shadow: 0 14px 28px rgba(71, 85, 105, 0.18);
        }
        
        .btn-inicio:hover {
            background-color: #5a6268;
            transform: translateY(-2px);
        }
        
        @media (max-width: 768px) {
            .firma-content {
                grid-template-columns: 1fr;
            }
            
            .detalles-grid {
                grid-template-columns: 1fr;
            }
            
            .acciones-firma {
                flex-direction: column;
            }
            
            .btn-descarga, .btn-validar, .btn-inicio {
                width: 100%;
            }
        }
    </style>
</head>
<body class="firma-resultado">
    <%
        Boolean firmaCompletada = (Boolean) session.getAttribute("firma_completada");
        ClientAccessResponseDTO firmaAcceso = (ClientAccessResponseDTO) session.getAttribute("firma_acceso");
        String pdfGenerado = (String) session.getAttribute("pdf_generado");
        String motivoFirma = (String) session.getAttribute("motivo_firma");
        
        if (firmaCompletada == null || !firmaCompletada || pdfGenerado == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        
        // Obtener datos del certificado
        org.openapitools.client.model.CertificateResponseDTO cert = 
            (org.openapitools.client.model.CertificateResponseDTO) session.getAttribute("certificado");
        
        String apellidos = (cert.getSurname1() + " " + (cert.getSurname2() != null ? cert.getSurname2() : "")).trim();
        String nombreUsuario = cert != null ? (cert.getName() + " " + apellidos).trim() : "Usuario";
        String nif = cert != null ? cert.getNumberUserId() : "N/A";

        String token = (String) session.getAttribute(com.example.proyecto_ae.config.AppConstants.SESSION_FIRMA_TOKEN);
        String validationTarget = "https://sandbox.viafirma.com/sign-page/v/" + token;
        String qrData = URLEncoder.encode(validationTarget, "UTF-8");
        String qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=250x250&data=" + qrData;
    %>

    <div class="firma-box">
        <div class="firma-header">
            <div class="firma-icon-success">✓</div>
            <h2>¡Documento Firmado Correctamente!</h2>
            <p style="color: #666; margin-top: 10px;">Tu documento ha sido firmado digitalmente con éxito.</p>
        </div>

        <div class="firma-detalles">
            <h4>📋 Detalles de la Firma</h4>
            <div class="detalles-grid">
                <div class="detalle-item">
                    <div class="detalle-label">Usuario:</div>
                    <div class="detalle-valor"><%= nombreUsuario %></div>
                </div>
                <div class="detalle-item">
                    <div class="detalle-label">NIF/NIE:</div>
                    <div class="detalle-valor"><%= nif %></div>
                </div>
                <div class="detalle-item">
                    <div class="detalle-label">Archivo:</div>
                    <div class="detalle-valor"><%= pdfGenerado %></div>
                </div>
                <div class="detalle-item">
                    <div class="detalle-label">Fecha y Hora:</div>
                    <div class="detalle-valor"><%= new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date()) %></div>
                </div>
            </div>
        </div>

        <div class="firma-content">
            <div class="firma-section">
                <h3>📄 Documento Firmado</h3>
                <p>Tu documento ha sido sellado digitalmente y contiene la firma de Viafirma.</p>
                <div class="cajetin-firma">
                    <h4>CAJETÍN DE FIRMA</h4>
                    <p style="font-size: 12px; margin-bottom: 10px;">Código QR para validar la firma</p>
                    <div class="qr-placeholder" style="background: white; padding: 15px; border-radius: 8px; display: inline-block; box-shadow: 0 4px 10px rgba(0,0,0,0.1); width: 170px; height: 170px; margin: 0 auto;">
                        <img src="<%= qrUrl %>" alt="QR de validación" style="width: 100%; height: 100%; border-radius: 4px;" />
                    </div>
                    <p style="font-size: 11px; margin-top: 10px;">Escanea este código para validar la firma</p>
                </div>
            </div>

            <div class="firma-section">
                <h3>✔️ Estado de Firma</h3>
                <p><strong>Estado:</strong> Firmado</p>
                <p><strong>Tipo:</strong> Firma Digital Avanzada</p>
                <p><strong>Certificado:</strong> Viafirma</p>
                <p style="margin-top: 20px; color: #28a745; font-weight: bold;">✓ La firma es válida y verificable</p>
            </div>
        </div>

        <div class="firma-section" style="margin-bottom: 30px;">
            <h3>📄 Previsualización del Documento</h3>
            <iframe src="DescargarPdfFirmadoServlet" width="100%" height="600px" style="border: none; border-radius: 8px;"></iframe>
            
            <script>
                // Autodescarga al cargar la página
                window.onload = function() {
                    console.log("Iniciando autodescarga...");
                    setTimeout(function() {
                        const link = document.createElement('a');
                        link.href = 'DescargarPdfFirmadoServlet';
                        link.download = '<%= pdfGenerado %>';
                        document.body.appendChild(link);
                        link.click();
                        document.body.removeChild(link);
                    }, 1000); // Esperamos 1 segundo para asegurar que el servidor procesó el callback
                };
            </script>
        </div>

        <div style="background-color: #f0f0f0; padding: 15px; border-radius: 5px; margin-bottom: 30px; font-size: 13px; color: #555;">
            <strong>Nota:</strong> Este documento ha sido firmado digitalmente según la legislación de firma electrónica vigente. 
            El cajetín de firma incluye un código QR que permite validar la firma en Viafirma. 
            Puedes descargar el PDF firmado y compartirlo de forma segura.
        </div>

        <div class="acciones-firma">
            <a class="btn-descarga" href="DescargarPdfFirmadoServlet">
                📥 Descargar PDF Firmado
            </a>
            <button class="btn-validar" onclick="window.open('<%= validationTarget %>', '_blank')">
                ✓ Validar Firma
            </button>
            <button class="btn-inicio" onclick="window.location.href='index.jsp'">
                🏠 Volver al Inicio
            </button>
        </div>
    </div>

</body>
</html>