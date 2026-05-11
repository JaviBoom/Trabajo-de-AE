<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Mapa de Sedes - Administración Electrónica</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/estilos.css?v=2">
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
    <style>
        #map { height: 500px; width: 100%; border-radius: 8px; border: 1px solid #ddd; }
    </style>
</head>
<body>
    <header>
        <div class="container">
            <h1>Sede Electrónica</h1>
            <p>Mapa de Puntos de Atención y Registro</p>
        </div>
    </header>

    <div class="container main-content">
        <div class="section-card">
            <h3>Puntos de Atención Presencial</h3>
            <p>Consulte las oficinas de registro y atención al ciudadano más cercanas.</p>
            <div id="map"></div>
        </div>
        
        <div class="section-card">
            <a href="index.jsp" class="btn btn-primary">VOLVER AL INICIO</a>
        </div>
    </div>

    <footer>
        <div class="container">
            <p>&copy; 2026 Sede Electrónica de Administración Pública</p>
        </div>
    </footer>

    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
    <script>
        var map = L.map('map').setView([37.3891, -5.9845], 13);
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '&copy; OpenStreetMap contributors'
        }).addTo(map);

        var locations = [
            { name: "Sede Central de Registro", lat: 37.3891, lng: -5.9845, color: "#1e3a8a", desc: "Oficina principal de atención" },
            { name: "Oficina de Registro Norte", lat: 37.4050, lng: -5.9730, color: "#28a745", desc: "Atención al ciudadano" },
            { name: "Punto de Validación Sur", lat: 37.3538, lng: -5.9418, color: "#004580", desc: "Sede de validación de certificados" }
        ];

        locations.forEach(function(loc) {
            L.marker([loc.lat, loc.lng]).addTo(map)
                .bindPopup("<b>" + loc.name + "</b><br>" + loc.desc);
        });
    </script>
</body>
</html>
