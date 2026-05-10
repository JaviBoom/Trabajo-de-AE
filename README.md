# Proyecto AE - Sede Electrónica Avanzada con Firma Digital Dual

## 📋 Descripción General

Aplicación web Java que implementa una **Sede Electrónica completa** orientada al cumplimiento normativo y técnico de la asignatura de Administración Electrónica. El proyecto ha evolucionado de un sistema de firma a un portal administrativo integral que cumple con los estándares de interoperabilidad, transparencia y protección de datos exigidos en el sector público español.

### 🌟 Características Destacadas (Alineación Académica)

- 🔐 **Autenticación y Firma Dual**: Integración con Viafirma para autenticación cualificada y firma de expedientes en lote (PDF/PAdES + XML/XAdES).
- 🏛️ **Sede Electrónica Centralizada**: Portal de acceso único con catálogo de trámites, siguiendo los principios del Punto de Acceso General (Tema 3 y 4).
- 📍 **Mapa de Sedes Abierto**: Visualización de oficinas de registro y sedes administrativas mediante **Leaflet.js** y datos en formato **GeoJSON** (EPD 8 y 9).
- 📋 **Registro Electrónico General**: Generación automática de asientos registrales de **ENTRADA** y **SALIDA** conforme al **Art. 16 de la Ley 39/2015** (Tema 3).
- 🛡️ **Protección de Datos (RGPD)**: Implementación de política de privacidad informativa, derechos ARCO+ y base jurídica del tratamiento según el **Reglamento (UE) 2016/679** (Tema 5).
- 🔑 **Consentimiento SCSP**: Sistema de verificación de identidad y residencia sin aportación de documentos en papel, basado en el **RD 522/2006** (Tema 4).

## 🏗️ Arquitectura y Componentes

### Flujo Administrativo Completo
1.  **Acceso**: Autenticación con certificado digital vía `LoginServlet`.
2.  **Trámite**: Formulario de solicitud con selección de consentimiento **SCSP** y **RGPD**.
3.  **Firma**: Generación de expediente dual (PDF Premium + XML Interoperable) y firma en lote.
4.  **Registro**: Tras la firma, `FirmaCallbackServlet` genera automáticamente el **asiento registral** en el libro de registros de la Sede.
5.  **Notificación**: Confirmación al interesado con acceso a los documentos firmados y referencia de registro.

### Estructura de Páginas (Portal de Sede)

| Página | Concepto Académico | Descripción |
|---|---|---|
| `index.jsp` | **Ventanilla Electrónica** | Portal principal, catálogo de trámites y avisos legales. |
| `mapa.jsp` | **Datos Abiertos (Open Data)** | Mapa interactivo con Leaflet.js y capas GeoJSON de sedes. |
| `registro.jsp` | **Registro Electrónico** | Libro de asientos registrales de entrada/salida (Art. 16 Ley 39/2015). |
| `privacidad.jsp` | **RGPD / LOPDGDD** | Información detallada sobre el tratamiento de datos personales. |
| `datos.jsp` | **Interoperabilidad (SCSP)** | Formulario con gestión de consentimientos para consulta de datos. |

## 🔧 Configuración

### Variables de Entorno
El sistema utiliza las siguientes credenciales para la conexión con Viafirma Sandbox:
- `VIAFIRMA_USERNAME`
- `VIAFIRMA_PASSWORD`

*(Configurables en el entorno del servidor o mediante VM Options en NetBeans: `-DVIAFIRMA_USERNAME=...`)*

## 📚 Base Normativa Implementada

El proyecto ha sido diseñado siguiendo estrictamente el marco legal estudiado en la asignatura:

1.  **Ley 39/2015, de 1 de octubre**, del Procedimiento Administrativo Común.
2.  **Ley 40/2015, de 1 de octubre**, de Régimen Jurídico del Sector Público.
3.  **Reglamento (UE) No 910/2014 (eIDAS)**, relativo a la identificación electrónica y servicios de confianza.
4.  **Ley 6/2020, de 11 de noviembre**, reguladora de determinados aspectos de los servicios electrónicos de confianza.
5.  **Reglamento (UE) 2016/679 (RGPD)** y **Ley Orgánica 3/2018 (LOPDGDD)**.
6.  **Real Decreto 522/2006** (Supresión de fotocopias de DNI) y **RD 523/2006** (Telematización de datos de residencia).

## 🚀 Tecnologías Utilizadas

- **Backend**: Java Servlets 4.0, Maven.
- **Frontend**: JSP, Vanilla CSS, Leaflet.js (Mapas).
- **Firma**: Viafirma Platform SDK (OpenAPI Client).
- **Documentación**: iText 7 (Generación de PDF), Jackson (JSON/GeoJSON).

## 🛠️ Dificultades Técnicas y Soluciones

Durante el desarrollo nos enfrentamos a varios retos que requirieron soluciones específicas:

1.  **Error 403 en Descarga Delegada**: Inicialmente, el servidor intentaba descargar el PDF de Viafirma para servírselo al usuario. Sin embargo, la API de Viafirma bloqueaba estas peticiones por falta de contexto de sesión/cookies del usuario final.
    -   *Solución*: Implementamos una estrategia de **descarga directa**, capturando el `signedLink` en el callback y redirigiendo al usuario mediante un botón de descarga en el navegador.
2.  **Bloqueo de Vista Previa (X-Frame-Options)**: Intentamos mostrar el PDF en un `iframe`, pero las políticas de seguridad de Viafirma (`DENY`) impedían la visualización.
    -   *Solución*: Sustituimos el iframe por un panel de previsualización que abre el documento en una **pestaña nueva**, garantizando la accesibilidad y seguridad.
3.  **Posicionamiento del Sello de Firma**: El sello automático de Viafirma solapaba a veces el contenido legal del PDF.
    -   *Solución*: Ajustamos el "Stamper" dinámicamente (`xAxis: 50`, `yAxis: 750`) y añadimos saltos de línea de seguridad al final del documento generado con iText para dejar espacio libre.

## 📁 Estructura del Proyecto

```
Proyecto_AE/
├── src/main/java/
│   ├── com/example/proyecto_ae/
│   │   ├── services/      # Lógica de Viafirma e Interoperabilidad
│   │   └── servlets/      # Controladores (Auth, Firma, Registro)
├── src/main/webapp/
│   ├── WEB-INF/jsp/pages/ # Páginas internas de la Sede
│   ├── estilos.css        # Diseño institucional premium
│   └── index.jsp          # Punto de entrada público
└── pom.xml                # Gestión de dependencias (iText, Jackson, SDK)
```

---
**Última actualización**: 10 de mayo de 2026
*Proyecto desarrollado para la asignatura de Administración Electrónica (Grado en Ingeniería Informática).*
