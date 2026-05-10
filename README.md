# Proyecto AE - Sede Electrónica con Firma Digital Dual (Viafirma)

## 📋 Descripción General

Aplicación web Java que implementa una **Sede Electrónica** completa con autenticación mediante certificado digital y **firma dual de expedientes** (PDF + XML) a través de la plataforma Viafirma. El sistema permite a los usuarios autenticarse con su certificado FNMT/CERES, rellenar un formulario administrativo y generar un expediente digital compuesto por dos documentos firmados simultáneamente en un solo lote.

### Características Principales

- 🔐 **Autenticación con Certificado Digital** (FNMT, CERES) vía Viafirma
- 📄 **Firma Dual en Lote** — PDF (PAdES-B) + XML (XAdES-B) en una sola operación
- 🏛️ **Generación de PDF Premium** — Comprobante de firma con datos del certificado, validación técnica y sello QR
- 📊 **Expediente XML Técnico** — Metadatos estructurados para interoperabilidad entre administraciones
- ✅ **Validación Oficial** — Enlace directo al validador de Viafirma
- 🎨 **Interfaz Premium** — Diseño moderno con tarjetas duales de descarga

## 🏗️ Arquitectura

### Flujo Completo
```
1.  Usuario accede a index.jsp (Sede Electrónica)
2.  Clic en "Acceder con Certificado Digital"
3.  LoginServlet → API Viafirma (prepareAuthentication)
4.  Usuario se autentica con su certificado en Viafirma
5.  Viafirma redirige a CallbackServlet con código
6.  CallbackServlet obtiene datos del certificado (getCertificate)
7.  Usuario ve sus datos en datos.jsp
8.  Completa formulario y hace clic "Generar y Firmar Documento"
9.  FirmaServlet genera PDF Premium + XML técnico
10. ViafirmaService.prepareSignature() → Firma Dual en Lote (PAdES + XAdES)
11. Usuario firma en Viafirma con su certificado
12. FirmaCallbackServlet captura los enlaces de descarga
13. confirmacion_firma.jsp muestra tarjetas duales de descarga
```

### Componentes del Backend

| Componente | Responsabilidad |
|---|---|
| `LoginServlet` | Inicia flujo de autenticación con Viafirma |
| `CallbackServlet` | Procesa el callback post-autenticación, extrae datos del certificado |
| `FirmaServlet` | Genera PDF Premium + XML técnico, lanza firma dual |
| `FirmaCallbackServlet` | Captura enlaces de documentos firmados de Viafirma |
| `DescargarPdfFirmadoServlet` | Gestión de descarga de documentos firmados |
| `ViafirmaService` | Servicio centralizado: autenticación, firma en lote, descarga |

### Páginas JSP

| Página | Función |
|---|---|
| `index.jsp` | Página principal de la Sede Electrónica |
| `datos.jsp` | Formulario con datos pre-rellenados del certificado |
| `confirmacion_firma.jsp` | Resultado con tarjetas duales de descarga (PDF + XML) |
| `error.jsp` | Gestión de errores |

## 🔧 Configuración

### Variables de Entorno (RECOMENDADO)

Configura las siguientes variables de entorno en tu sistema:

```bash
# Windows (CMD)
set VIAFIRMA_USERNAME=tu_usuario
set VIAFIRMA_PASSWORD=tu_contraseña

# Windows (PowerShell)
$env:VIAFIRMA_USERNAME = "tu_usuario"
$env:VIAFIRMA_PASSWORD = "tu_contraseña"

# Linux/Mac
export VIAFIRMA_USERNAME=tu_usuario
export VIAFIRMA_PASSWORD=tu_contraseña
```

#### Configurar en NetBeans/IDE

**NetBeans:**
1. Click derecho en el proyecto → Properties
2. Run → VM Options
3. Agregar: `-DVIAFIRMA_USERNAME=tu_usuario -DVIAFIRMA_PASSWORD=tu_contraseña`

**IntelliJ IDEA:**
1. Run → Edit Configurations
2. Environment variables
3. Agregar: `VIAFIRMA_USERNAME=tu_usuario;VIAFIRMA_PASSWORD=tu_contraseña`

#### Configurar en Tomcat

**archivo: `CATALINA_HOME/bin/catalina.sh`** (Linux/Mac)
```bash
CATALINA_OPTS="$CATALINA_OPTS -DVIAFIRMA_USERNAME=tu_usuario"
CATALINA_OPTS="$CATALINA_OPTS -DVIAFIRMA_PASSWORD=tu_contraseña"
```

**archivo: `CATALINA_HOME\bin\catalina.bat`** (Windows)
```batch
set CATALINA_OPTS=%CATALINA_OPTS% -DVIAFIRMA_USERNAME=tu_usuario
set CATALINA_OPTS=%CATALINA_OPTS% -DVIAFIRMA_PASSWORD=tu_contraseña
```

### Cambiar URL de Viafirma

Si necesitas cambiar de sandbox a producción o viceversa, edita `ViafirmaService.java`:

```java
// Sandbox (actual)
private static final String API_BASE_URL = "https://sandbox.viafirma.com/signservices";

// Producción
private static final String API_BASE_URL = "https://viafirma.com/signservices";
```

## 📦 Dependencias

El proyecto usa Maven. Las dependencias principales son:

- **Viafirma API** — Cliente generado con OpenAPI Generator
- **iText 7** — Generación de PDFs Premium con tablas, colores y tipografía
- **Apache HttpComponents** — Peticiones HTTP a la API
- **Jackson** — Serialización/deserialización JSON
- **Java Servlet API 4.0** — Servlets y JSP

Ver `pom.xml` para la lista completa.

## 🚀 Compilación y Ejecución

### Compilar
```bash
mvn clean compile
```

### Generar WAR
```bash
mvn clean package
```

### Ejecutar en NetBeans
```
F6 o Run Project
```

### Ejecutar con Maven
```bash
mvn tomcat7:run
```

## 🎯 Uso

1. **Acceder a la aplicación**
   - URL: `http://localhost:8084/Proyecto_AE/`

2. **Autenticarse**
   - Hacer clic en "Acceder con Certificado Digital"
   - Seleccionar certificado en Viafirma (FNMT/CERES)
   - Confirmar autenticación

3. **Firmar Expediente Dual**
   - Revisar datos del certificado (pre-rellenados automáticamente)
   - Ingresar motivo de la solicitud
   - Marcar los checkboxes de consentimiento
   - Hacer clic en "Generar y Firmar Documento"
   - Firmar con certificado digital en la pasarela de Viafirma

4. **Descargar Documentos Firmados**
   - En la página de confirmación, descargar el **PDF firmado** (estándar PAdES)
   - Descargar el **XML firmado** (estándar XAdES)
   - Abrir la vista previa del documento
   - Validar la firma en el portal oficial de Viafirma

## 📁 Estructura de Directorios

```
Proyecto_AE/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/example/proyecto_ae/
│   │   │   │   ├── config/
│   │   │   │   │   └── AppConstants.java
│   │   │   │   ├── services/
│   │   │   │   │   └── ViafirmaService.java
│   │   │   │   └── servlets/
│   │   │   │       ├── auth/
│   │   │   │       │   ├── LoginServlet.java
│   │   │   │       │   └── CallbackServlet.java
│   │   │   │       └── firma/
│   │   │   │           ├── FirmaServlet.java
│   │   │   │           ├── FirmaCallbackServlet.java
│   │   │   │           └── DescargarPdfFirmadoServlet.java
│   │   │   └── org/openapitools/client/  (API Viafirma generada)
│   │   └── webapp/
│   │       ├── WEB-INF/jsp/pages/
│   │       │   ├── index.jsp
│   │       │   ├── datos.jsp
│   │       │   ├── confirmacion_firma.jsp
│   │       │   └── error.jsp
│   │       └── estilos.css
│   └── test/
├── pom.xml
├── README.md
└── NETBEANS-GUIDE.md
```

## 🔐 Firma Dual — Detalles Técnicos

### PDF (PAdES-B Level)
- Generado con **iText 7**
- Contiene: datos del certificado, estado de validación técnica, metadatos temporales
- Sello QR oficial de Viafirma en la esquina inferior izquierda
- Formato legible para humanos

### XML (XAdES-B Level)
- Estructura `<expediente_digital>` con metadatos y datos del firmante
- Pensado para **interoperabilidad** entre administraciones públicas
- Formato procesable por máquinas (Sede Electrónica, registros)

### Proceso de Firma en Lote
`ViafirmaService.prepareSignature()` envía ambos documentos a Viafirma en una sola petición. El SDK determina automáticamente el perfil de firma según la extensión del archivo:
- `.pdf` → PAdES-B
- `.xml` → XAdES-B

## 🔒 Seguridad

### Implementado
- ✅ Autenticación con certificado digital cualificado
- ✅ Firma electrónica avanzada (PAdES + XAdES)
- ✅ Validación de parámetros en todos los servlets
- ✅ Verificación de sesión obligatoria
- ✅ Manejo centralizado de credenciales (variables de entorno)
- ✅ Manejo de errores robusto con páginas dedicadas
- ✅ Descarga directa desde Viafirma (sin proxy server-side)

### Recomendaciones para Producción
- [ ] Implementar HTTPS (SSL/TLS)
- [ ] Implementar CSRF tokens
- [ ] Agregar rate limiting
- [ ] Usar WAF (Web Application Firewall)
- [ ] Implementar logging centralizado (SLF4J + Logback)
- [ ] Agregar monitoreo y alertas
- [ ] Backup regular de documentos firmados

## 🐛 Troubleshooting

### Error: "Código de autenticación inválido"
**Causa**: El código devuelto por Viafirma no tiene el formato esperado.
**Solución**: Verificar que Viafirma está accesible y que las credenciales son correctas.

### Error: "Debes autenticarte primero"
**Causa**: La sesión expiró (30 minutos) o no hay certificado en sesión.
**Solución**: Volver a `index.jsp` y autenticarse de nuevo.

### Error 403 al descargar documentos
**Causa**: Viafirma bloquea descargas server-side sin contexto de sesión.
**Solución**: Ya resuelto — la app usa enlaces directos que el navegador del usuario abre.

### Vista previa no carga
**Causa**: Viafirma establece `X-Frame-Options: DENY`, bloqueando iframes.
**Solución**: Ya resuelto — se usa un botón "Abrir Vista Previa" en nueva pestaña.

### PDF no se genera
**Causa**: Sin permisos de escritura en el directorio temporal.
**Solución**: Verificar permisos de carpeta `%TEMP%/documentos_firmados`

### Puerto 8084 ya está en uso
**Causa**: Otra aplicación está usando el puerto.
**Solución**: Cambiar puerto en `server.xml` de Tomcat o en la configuración del servidor de NetBeans.

## 📝 Notas de Desarrollo

### Estándares Implementados
- **PAdES** (PDF Advanced Electronic Signatures) — ETSI EN 319 142
- **XAdES** (XML Advanced Electronic Signatures) — ETSI EN 319 132
- **Ley 6/2020** — Reguladora de determinados aspectos de los servicios electrónicos de confianza

### Logging
La aplicación usa `System.out` y `System.err` para logging. Para producción, considera usar SLF4J + Logback:

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

private static final Logger logger = LoggerFactory.getLogger(MiServlet.class);
logger.info("Mensaje de información");
logger.error("Error", exception);
```

### Testing
```bash
mvn test
```

### Documentación de API
```bash
mvn javadoc:javadoc
```

## 📞 Soporte

Si encuentras problemas:
1. Revisar logs en la consola de NetBeans
2. Verificar que Viafirma está accesible
3. Verificar que las credenciales son correctas
4. Revisar permisos de carpetas

## 📄 Licencia

Este proyecto es para uso educativo — Asignatura de Administración Electrónica (UPO).

---

**Última actualización**: 10 de mayo de 2026
