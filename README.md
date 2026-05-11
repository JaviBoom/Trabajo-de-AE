# Sede Electrónica - Portal de Firma y Registro

## Descripción General
Este proyecto implementa una **Sede Electrónica** profesional y robusta para la gestión de trámites administrativos. El portal permite la identificación ciudadana, la firma electrónica de documentos con validez legal, el registro automático de asientos en el Libro de Registro General y la validación técnica de documentos firmados.

## Características Principales

### 1. Gestión de Identidad y Sesión
- **Acceso Seguro**: Integración con Viafirma Auth para identificación mediante certificado digital.
- **Portal Dinámico**: La interfaz se adapta al estado del usuario (Anónimo vs Identificado).
- **Control de Sesión**: Gestión completa de login y logout con persistencia de datos del certificado.

### 2. Firma y Registro Electrónico
- **Generación Dinámica**: Creación automática de justificantes PDF (iText7) y metadatos XML.
- **Firma Avanzada**: Soporte para firmas PAdES (PDF) y XAdES (XML) mediante firma por lotes (Batch).
- **Sello QR**: Estampado de código QR en el lateral superior de los documentos para facilitar su verificación.
- **Registro de Entrada**: Cada documento firmado se anota automáticamente en el **Libro de Registro General** con un número de asiento oficial.

### 3. Validador de Firmas Interno
- **Verificación Técnica**: Herramienta integrada para validar la integridad de los documentos y la vigencia de los certificados.
- **Informe Detallado**: Muestra el nombre de los firmantes, fechas de firma, formato (PAdES/XAdES) y estado de confianza.
- **Compatibilidad Ampliada**: Soporte para múltiples fuentes de confianza (AATL, EUTL, AIA, etc.).

### 4. Arquitectura y Robustez
- **Descargas Directas**: Sistema optimizado de recuperación de archivos desde la nube de Viafirma para evitar bloqueos de infraestructura (403 Forbidden).
- **Gestión de Errores**: Manejo de excepciones, reintentos automáticos y soporte para diversos métodos HTTP (GET/POST).
- **Diseño Institucional**: Estética profesional, sobria y simétrica, cumpliendo con estándares de administración pública.

## Tecnologías Utilizadas
- **Backend**: Java EE (Servlets, JSP), Maven.
- **Integración**: Viafirma API (OpenAPI Client).
- **Documentación**: iText7 para la generación de PDF institucionales.
- **Frontend**: HTML5, CSS3 (Flexbox/Grid) con diseño responsive.

## Cómo empezar
1. **Compilación**: Ejecutar `mvn clean install` (Asegúrese de detener el servidor Tomcat antes).
2. **Despliegue**: Desplegar el archivo `.war` resultante en un servidor Apache Tomcat 9+.
3. **Uso**: 
   - Acceda a la página principal.
   - Identifíquese con su certificado digital.
   - Realice un trámite de firma.
   - Verifique su asiento en el "Libro de Registro".
   - Use el "Validador" para comprobar cualquier PDF firmado.

---
*Este proyecto cumple con los requisitos técnicos para entornos de Administración Electrónica y el Esquema Nacional de Seguridad.*
