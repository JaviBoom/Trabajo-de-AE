@echo off
REM Script para configurar variables de entorno de Viafirma en Windows
REM Uso: configura-viafirma.bat

setlocal enabledelayedexpansion

echo.
echo ====================================================
echo  Configurador de Variables de Entorno - Viafirma
echo ====================================================
echo.

REM Solicitar credenciales
set /p VIAFIRMA_USERNAME="Ingresa usuario de Viafirma: "
if "!VIAFIRMA_USERNAME!"=="" (
    echo ERROR: Usuario no puede estar vacío
    pause
    exit /b 1
)

set /p VIAFIRMA_PASSWORD="Ingresa contraseña de Viafirma: "
if "!VIAFIRMA_PASSWORD!"=="" (
    echo ERROR: Contraseña no puede estar vacía
    pause
    exit /b 1
)

REM Establecer variables de entorno para la sesión actual
setx VIAFIRMA_USERNAME "!VIAFIRMA_USERNAME!"
setx VIAFIRMA_PASSWORD "!VIAFIRMA_PASSWORD!"

echo.
echo ====================================================
echo  Variables configuradas correctamente
echo ====================================================
echo.
echo VIAFIRMA_USERNAME = !VIAFIRMA_USERNAME!
echo VIAFIRMA_PASSWORD = ******* (oculta por seguridad)
echo.
echo NOTA: Las variables se aplican a nuevas sesiones de CMD
echo       Si estás en NetBeans, reinicia la aplicación.
echo.

pause
