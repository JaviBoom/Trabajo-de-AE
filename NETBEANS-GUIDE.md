# Instrucciones para NetBeans

## 🚀 Primeros Pasos en NetBeans

### 1. Configurar Variables de Entorno

**Opción A: Usando el script (Windows)**
```bash
# Ejecutar en línea de comandos
cd C:\Users\scala\Documents\NetBeansProjects\Proyecto_AE
configura-viafirma.bat
```

**Opción B: Manual en NetBeans**

1. Abrir el proyecto en NetBeans
2. Click derecho en el proyecto → **Properties**
3. Ir a **Run**
4. En **VM Options**, agregar:
```
-DVIAFIRMA_USERNAME=upo_practices -DVIAFIRMA_PASSWORD=MSW4zyVXBax3
```
5. Click **OK**

### 2. Compilar el Proyecto

```bash
# Opción 1: NetBeans
Click derecho en el proyecto → Clean and Build

# Opción 2: Terminal
cd C:\Users\scala\Documents\NetBeansProjects\Proyecto_AE
mvn clean compile
```

### 3. Ejecutar el Proyecto

```bash
# Opción 1: NetBeans
F6 o Click en Run Project (botón ▶)

# Opción 2: Terminal
mvn tomcat7:run
```

### 4. Acceder a la Aplicación

Una vez que el servidor está corriendo:
```
http://localhost:8084/Proyecto_AE/
```

---

## 📁 Archivos Nuevos Creados

| Archivo | Ubicación | Propósito |
|---------|-----------|----------|
| `ViafirmaService.java` | `src/main/java/servlets/` | Servicio centralizado |
| `FirmaServlet.java` | `src/main/java/servlets/` | Generación de PDF |
| `confirmacion.jsp` | `src/main/webapp/` | Página de éxito |
| `error.jsp` | `src/main/webapp/` | Página de error |
| `README.md` | Raíz del proyecto | Documentación |
| `NETBEANS-GUIDE.md` | Raíz del proyecto | Esta guía |

---

## 🔧 Cambios en Archivos Existentes

| Archivo | Cambios |
|---------|---------|
| `LoginServlet.java` | ✅ Mejorado con ViafirmaService |
| `CallbackServlet.java` | ✅ Mejor validación de errores |
| `datos.jsp` | ✅ Sintaxis de EL corregida |
| `estilos.css` | ✅ Nuevos estilos agregados |
| `web.xml` | ⚠️ REVISAR: Necesita inclusión de FirmaServlet |

---

## ⚠️ IMPORTANTE: Actualizar web.xml

Verifica que el archivo `web.xml` incluya el FirmaServlet:

```xml
<servlet>
    <servlet-name>FirmaServlet</servlet-name>
    <servlet-class>servlets.FirmaServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>FirmaServlet</servlet-name>
    <url-pattern>/FirmaServlet</url-pattern>
</servlet-mapping>
```

Si no está, agrégalo después de CallbackServlet.

---

## 🐛 Solución de Problemas en NetBeans

### Error: "Package org.openapitools.client not found"
**Solución**:
1. Click derecho en proyecto → Clean and Build
2. Si persiste: Click derecho → Resolve Project Dependencies

### Error: "Port 8084 is already in use"
**Solución**:
1. NetBeans → Tools → Options → Java → Tomcat
2. Cambiar puerto o:
3. Abrir línea de comandos:
```bash
netstat -ano | findstr :8084
taskkill /PID <PID> /F
```

### Las nuevas clases no se ven
**Solución**:
1. Refrescar proyecto: F5
2. Limpiar y compilar: Click derecho → Clean and Build
3. Si aún no ves: Reiniciar NetBeans

### JSP shows errors pero compila
**Solución**:
1. A veces NetBeans no actualiza el parsing
2. Click derecho en JSP → Compile File
3. Reiniciar NetBeans si persiste

---

## ✅ Checklist de Configuración

- [ ] Descargar/actualizar proyecto
- [ ] Configurar variables de entorno (VIAFIRMA_USERNAME, VIAFIRMA_PASSWORD)
- [ ] Abrir proyecto en NetBeans
- [ ] Clean and Build (completo)
- [ ] Verificar que no hay errores en Output
- [ ] Run Project (F6)
- [ ] Acceder a http://localhost:8084/Proyecto_AE/
- [ ] Probar flujo completo de autenticación

---

## 🧪 Pruebas Rápidas

### Test 1: Página de Inicio
- URL: `http://localhost:8084/Proyecto_AE/`
- ✓ Debes ver dos botones
- ✓ Botón "Continuar" debe estar gris

### Test 2: Autenticación
- Click en "Acceder con Certificado Digital"
- ✓ Te redirige a Viafirma
- ✓ Puedes seleccionar certificado

### Test 3: Datos del Usuario
- Después de autenticarte en Viafirma
- ✓ Ves tus datos (nombre, NIF, email)
- ✓ Formulario está disponible

### Test 4: Generación de PDF
- Completa el formulario
- ✓ PDF se genera exitosamente
- ✓ Ves página de confirmación

---

## 📊 Monitoreo

### Ver logs en NetBeans
Los logs aparecen en la pestaña **Output**:
```
INFO: Iniciando flujo de autenticación con Viafirma
INFO: Callback URL: http://localhost:8084/Proyecto_AE/CallbackServlet
INFO: Redirigiendo a Viafirma: https://sandbox.viafirma.com/...
INFO: [UUID] Autenticación exitosa para: Juan Pérez
INFO: Generando PDF para firma
INFO: PDF generado: Firma_12345678_20260426_143022.pdf
```

### Ver PDFs generados
Ubicación:
```
C:\Users\<usuario>\AppData\Local\Temp\documentos_firmados\
```

O en variables de entorno de Java:
```bash
# Ejecutar en terminal para ver la ruta
echo %TEMP%
```

---

## 💡 Tips y Tricks

### Cambiar puerto de Tomcat
1. En NetBeans: Tools → Options → Java → Apache Tomcat
2. Cambiar puerto en "Server Port"

### Modo debug
1. F5 (Debug Project) en lugar de F6
2. Poner breakpoints en los servlets
3. Paso a paso con F7

### Recargar sin reiniciar servidor
1. Cambiar cualquier JSP
2. Guardar (Ctrl+S)
3. Refrescar navegador (F5)

### Limpiar caché de NetBeans
```bash
# Cerrar NetBeans
# Borrar carpeta:
C:\Users\scala\AppData\Roaming\NetBeans\*\var\cache

# Reiniciar NetBeans
```

---

## 📞 Soporte

Si tienes problemas:

1. **Revisar consola Output** en NetBeans
2. **Ver logs** en `System.out`
3. **Limpiar y compilar** varias veces
4. **Reiniciar NetBeans** si nada funciona
5. **Verificar que Viafirma está accesible** (sandbox.viafirma.com)

---

## 🎓 Recursos

- [NetBeans Documentation](https://netbeans.apache.org/)
- [Viafirma API Docs](https://sandbox.viafirma.com/api-docs)
- [Maven Plugin for Tomcat](https://tomcat.apache.org/maven-plugin.html)
- [iText 7 Documentation](https://itextpdf.com/)

---

**Última actualización**: 26 de abril de 2026
