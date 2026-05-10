<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Mapa de Sedes - Sede Electrónica</title>
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/estilos.css">
    <style>
        .mapa-container { max-width: 1100px; margin: 0 auto; padding: 30px 20px; }
        .nav-bar { display: flex; gap: 8px; background: white; padding: 8px; border-radius: 16px; margin-bottom: 25px; box-shadow: 0 4px 15px rgba(0,0,0,0.06); border: 1px solid #e2e8f0; flex-wrap: wrap; }
        .nav-link { padding: 10px 18px; border-radius: 10px; font-size: 13px; font-weight: 600; color: #475569; text-decoration: none; transition: all 0.2s; }
        .nav-link:hover { background: #f1f5f9; color: #1e293b; }
        .nav-link.active { background: #2563eb; color: white; }

        .mapa-header { background: white; border-radius: 18px; padding: 30px; border: 1px solid #e2e8f0; margin-bottom: 20px; }
        .mapa-header h1 { font-size: 1.6rem; margin: 0 0 8px 0; color: #1e293b; }
        .mapa-header p { color: #64748b; margin: 0; font-size: 14px; }
        #map { height: 500px; border-radius: 18px; border: 1px solid #e2e8f0; box-shadow: 0 8px 25px rgba(0,0,0,0.08); margin-bottom: 20px; z-index: 1; }
        .leyenda { background: white; border-radius: 18px; padding: 25px; border: 1px solid #e2e8f0; }
        .leyenda h3 { margin: 0 0 15px 0; font-size: 1rem; color: #1e293b; }
        .leyenda-items { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 12px; }
        .leyenda-item { display: flex; align-items: center; gap: 10px; font-size: 13px; color: #475569; }
        .leyenda-dot { width: 12px; height: 12px; border-radius: 50%; flex-shrink: 0; }
        .fuente-datos { margin-top: 20px; text-align: center; font-size: 12px; color: #94a3b8; padding: 15px; }
        .fuente-datos a { color: #2563eb; }
    </style>
</head>
<body style="background: linear-gradient(135deg, #f8fafc 0%, #eff6ff 50%, #f1f5f9 100%); min-height: 100vh;">
    <div class="mapa-container">

        <!-- NAVEGACIÓN -->
        <div class="nav-bar">
            <a href="index.jsp" class="nav-link">🏠 Inicio</a>
            <a href="mapa.jsp" class="nav-link active">📍 Mapa de Sedes</a>
            <a href="registro.jsp" class="nav-link">📋 Registro Electrónico</a>
            <a href="privacidad.jsp" class="nav-link">🔒 Privacidad</a>
        </div>

        <div class="mapa-header">
            <h1>📍 Mapa de Sedes y Oficinas de Registro</h1>
            <p>Localiza oficinas de atención al ciudadano, sedes administrativas y puntos de registro de la Universidad Pablo de Olavide y administraciones públicas de Sevilla. Datos proporcionados mediante formato GeoJSON.</p>
        </div>

        <!-- MAPA LEAFLET -->
        <div id="map"></div>

        <!-- LEYENDA -->
        <div class="leyenda">
            <h3>📌 Leyenda del Mapa</h3>
            <div class="leyenda-items">
                <div class="leyenda-item"><div class="leyenda-dot" style="background:#2563eb;"></div> Universidad Pablo de Olavide</div>
                <div class="leyenda-item"><div class="leyenda-dot" style="background:#16a34a;"></div> Oficina de Registro</div>
                <div class="leyenda-item"><div class="leyenda-dot" style="background:#dc2626;"></div> Delegación del Gobierno</div>
                <div class="leyenda-item"><div class="leyenda-dot" style="background:#f59e0b;"></div> Junta de Andalucía</div>
                <div class="leyenda-item"><div class="leyenda-dot" style="background:#8b5cf6;"></div> Ayuntamiento de Sevilla</div>
                <div class="leyenda-item"><div class="leyenda-dot" style="background:#ec4899;"></div> Seguridad Social / Hacienda</div>
            </div>
        </div>

        <div class="fuente-datos">
            Datos representados en formato <strong>GeoJSON</strong> (estándar abierto RFC 7946) · Visualización con <a href="https://leafletjs.com/" target="_blank">Leaflet.js</a> · Tiles de <a href="https://www.openstreetmap.org/" target="_blank">OpenStreetMap</a>
        </div>
    </div>

    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
    <script>
        // Inicializar mapa centrado en Sevilla
        var map = L.map('map').setView([37.3562, -5.9365], 13);

        // Capa base OpenStreetMap
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
            maxZoom: 19
        }).addTo(map);

        // Iconos personalizados por color
        function crearIcono(color) {
            return L.divIcon({
                className: 'custom-marker',
                html: '<div style="background:' + color + '; width:14px; height:14px; border-radius:50%; border:3px solid white; box-shadow:0 2px 6px rgba(0,0,0,0.3);"></div>',
                iconSize: [20, 20],
                iconAnchor: [10, 10],
                popupAnchor: [0, -12]
            });
        }

        // DATOS GEOJSON - Sedes administrativas de Sevilla
        var geojsonData = {
            "type": "FeatureCollection",
            "features": [
                {
                    "type": "Feature",
                    "properties": { "nombre": "Universidad Pablo de Olavide", "tipo": "Universidad", "direccion": "Ctra. de Utrera, km 1, 41013 Sevilla", "telefono": "954 34 92 00", "color": "#2563eb", "servicios": "Registro General, Secretaría General, Sede Electrónica" },
                    "geometry": { "type": "Point", "coordinates": [-5.9418, 37.3538] }
                },
                {
                    "type": "Feature",
                    "properties": { "nombre": "Registro General UPO", "tipo": "Oficina de Registro", "direccion": "Edificio José Celestino Mutis, UPO", "telefono": "954 34 92 00", "color": "#16a34a", "servicios": "Registro de Entrada/Salida, Compulsa Electrónica" },
                    "geometry": { "type": "Point", "coordinates": [-5.9430, 37.3545] }
                },
                {
                    "type": "Feature",
                    "properties": { "nombre": "Delegación del Gobierno en Andalucía", "tipo": "AGE", "direccion": "Plaza de España, s/n, 41013 Sevilla", "telefono": "955 56 90 00", "color": "#dc2626", "servicios": "Registro General AGE, Información al ciudadano" },
                    "geometry": { "type": "Point", "coordinates": [-5.9870, 37.3772] }
                },
                {
                    "type": "Feature",
                    "properties": { "nombre": "Consejería de la Presidencia - Junta de Andalucía", "tipo": "Junta de Andalucía", "direccion": "Av. de Roma, s/n, 41013 Sevilla", "telefono": "955 03 50 00", "color": "#f59e0b", "servicios": "Registro, @ries, Ventanilla Electrónica (VEA)" },
                    "geometry": { "type": "Point", "coordinates": [-5.9815, 37.3750] }
                },
                {
                    "type": "Feature",
                    "properties": { "nombre": "Ayuntamiento de Sevilla - Registro General", "tipo": "Administración Local", "direccion": "Plaza Nueva, 1, 41001 Sevilla", "telefono": "955 47 00 00", "color": "#8b5cf6", "servicios": "Registro Municipal, Padrón, Certificados" },
                    "geometry": { "type": "Point", "coordinates": [-5.9966, 37.3886] }
                },
                {
                    "type": "Feature",
                    "properties": { "nombre": "Oficina de la Seguridad Social", "tipo": "Seguridad Social", "direccion": "Av. de la Constitución, 2, 41004 Sevilla", "telefono": "901 50 20 50", "color": "#ec4899", "servicios": "Trámites de Seguridad Social, Certificados digitales" },
                    "geometry": { "type": "Point", "coordinates": [-5.9950, 37.3855] }
                },
                {
                    "type": "Feature",
                    "properties": { "nombre": "Agencia Tributaria (AEAT)", "tipo": "Hacienda", "direccion": "Av. Kansas City, s/n, 41007 Sevilla", "telefono": "901 33 55 33", "color": "#ec4899", "servicios": "Certificados tributarios, Cl@ve, Registro electrónico" },
                    "geometry": { "type": "Point", "coordinates": [-5.9760, 37.3930] }
                },
                {
                    "type": "Feature",
                    "properties": { "nombre": "Oficina de Atención al Ciudadano - Junta", "tipo": "Junta de Andalucía", "direccion": "C/ Torneo, 26, 41002 Sevilla", "telefono": "955 06 36 00", "color": "#f59e0b", "servicios": "Información general, Presentación electrónica, SCSP" },
                    "geometry": { "type": "Point", "coordinates": [-5.9965, 37.3955] }
                }
            ]
        };

        // Añadir marcadores GeoJSON al mapa
        L.geoJSON(geojsonData, {
            pointToLayer: function(feature, latlng) {
                return L.marker(latlng, { icon: crearIcono(feature.properties.color) });
            },
            onEachFeature: function(feature, layer) {
                var p = feature.properties;
                var popup = '<div style="font-family:Segoe UI,sans-serif; min-width:220px;">' +
                    '<h4 style="margin:0 0 6px 0; color:#1e293b; font-size:14px;">' + p.nombre + '</h4>' +
                    '<p style="margin:0 0 4px 0; font-size:12px; color:#64748b;"><strong>Tipo:</strong> ' + p.tipo + '</p>' +
                    '<p style="margin:0 0 4px 0; font-size:12px; color:#64748b;"><strong>Dirección:</strong> ' + p.direccion + '</p>' +
                    '<p style="margin:0 0 4px 0; font-size:12px; color:#64748b;"><strong>Teléfono:</strong> ' + p.telefono + '</p>' +
                    '<p style="margin:0; font-size:11px; color:#2563eb; background:#eff6ff; padding:4px 8px; border-radius:6px; margin-top:8px;">' + p.servicios + '</p>' +
                    '</div>';
                layer.bindPopup(popup);
            }
        }).addTo(map);
    </script>
</body>
</html>
