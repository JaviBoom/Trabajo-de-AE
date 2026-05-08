# Proyecto AE - Autenticación y Firma Digital con Viafirma

## 📋 Descripción General

Aplicación web Java que implementa autenticación con certificado digital a través de Viafirma. Los usuarios pueden autenticarse, rellenar un formulario y generar un PDF firmado digitalmente.

## 🏗️ Arquitectura

### Flujo de Autenticación
```
1. Usuario accede a index.jsp
2. Hace clic en "Acceder con Certificado Digital"
3. Se redirige a LoginServlet
4. LoginServlet contacta con API de Viafirma
5. Usuario se autentica en Viafirma con su certificado
6. Viafirma redirige a CallbackServlet con código
7. CallbackServlet obtiene datos del certificado
8. Usuario ve sus datos en datos.jsp
9. Completa formulario y hace clic "Firmar"
10. FirmaServlet genera PDF y redirige a confirmacion.jsp
```

### Componentes

- **LoginServlet**: Inicia el flujo de autenticación
- **CallbackServlet**: Maneja el callback de Viafirma
- **FirmaServlet**: Genera PDF con datos del certificado
- **ViafirmaService**: Servicio centralizado para integración con Viafirma
- **JSP Pages**: 
  - `index.jsp` - Página de inicio
  - `datos.jsp` - Formulario con datos del usuario
  - `confirmacion.jsp` - Confirmación de firma exitosa
  - `error.jsp` - Página de errores

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

```xml
<!-- Viafirma API (generada con OpenAPI) -->
<!-- iText 7 para generación de PDFs -->
<!-- Apache HttpComponents para peticiones HTTP -->
<!-- Jackson para serialización JSON -->
```

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
```bash
F6 o Run Project
```

### Ejecutar con Maven
```bash
mvn tomcat7:run
```

## 🎯 Uso

1. **Acceder a la aplicación**
   - URL: `http://localhost:8084/Proyecto_AE/`

2. **Acceder**
   - Hacer clic en "Acceder con Certificado Digital"
   - Seleccionar certificado en Viafirma
   - Confirmar autenticación

3. **Firmar documento**
   - Revisar datos del certificado (pre-rellenados)
   - Ingresar motivo de la solicitud
   - Marcar los checkboxes de consentimiento
   - Hacer clic en "Generar y Firmar Documento"

4. **Descargar PDF** (en desarrollo)
   - En la página de confirmación, descargar el PDF generado

## 📁 Estructura de Directorios

```
Proyecto_AE/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/mycompany/proyecto_ae/
│   │   │   └── servlets/
│   │   │       ├── LoginServlet.java
│   │   │       ├── CallbackServlet.java
│   │   │       ├── FirmaServlet.java
│   │   │       └── ViafirmaService.java
│   │   └── webapp/
│   │       ├── index.jsp
│   │       ├── datos.jsp
│   │       ├── confirmacion.jsp
│   │       ├── error.jsp
│   │       └── estilos.css
│   └── test/
├── pom.xml
└── README.md
```

## 🔒 Seguridad

### Implementado
- ✅ Validación de parámetros
- ✅ Verificación de sesión
- ✅ Manejo centralizado de credenciales
- ✅ Manejo de errores robusto
- ✅ Logs de auditoría

### Recomendaciones para Producción
- [ ] Implementar HTTPS (SSL/TLS)
- [ ] Implementar CSRF tokens
- [ ] Agregar limite de rate limiting
- [ ] Usar WAF (Web Application Firewall)
- [ ] Implementar logging centralizado
- [ ] Agregar monitoreo y alertas
- [ ] Backup regular de PDFs generados

## 🐛 Troubleshooting

### Error: "Código de autenticación inválido"
**Causa**: El código devuelto por Viafirma no tiene el formato esperado.
**Solución**: Verificar que Viafirma está accesible y que las credenciales son correctas.

### Error: "Debes autenticarte primero"
**Causa**: La sesión expiró (30 minutos) o no hay certificado en sesión.
**Solución**: Volver a `index.jsp` y autenticarse de nuevo.

### PDF no se genera
**Causa**: Sin permisos de escritura en el directorio temporal.
**Solución**: Verificar permisos de carpeta `%TEMP%/documentos_firmados`

### Puerto 8084 ya está en uso
**Causa**: Otra aplicación está usando el puerto.
**Solución**: Cambiar puerto en `server.xml` de Tomcat o en la configuración del servidor de NetBeans.

## 📝 Notas de Desarrollo

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
Para agregar tests unitarios:
```bash
mvn test
```

### Documentación de API
Para generar documentación:
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

Este proyecto fue generado con Gemini AI y es para uso educativo.

---

**Última actualización**: 26 de abril de 2026
