package com.example.proyecto_ae.config;

/**
 * Constantes centralizadas de la aplicación
 */
public class AppConstants {

    // URLs de callback
    public static final String AUTH_CALLBACK_URL = "/CallbackServlet";
    public static final String FIRMA_CALLBACK_URL = "/FirmaCallbackServlet";

    // Rutas de redirección JSP públicas (puente hacia WEB-INF)
    public static final String PAGE_INDEX = "index.jsp";
    public static final String PAGE_DATOS = "datos.jsp";
    public static final String PAGE_CONFIRMACION_FIRMA = "confirmacion_firma.jsp";
    public static final String PAGE_ERROR = "error.jsp";

    // Directorios
    public static final String UPLOAD_DIR = "documentos_firmados";

    // Atributos de sesión
    public static final String SESSION_ID = "session_id";
    public static final String SESSION_CERTIFICADO = "certificado";
    public static final String SESSION_PDF_GENERADO = "pdf_generado";
    public static final String SESSION_PDF_RUTA = "pdf_ruta";
    public static final String SESSION_MOTIVO_FIRMA = "motivo_firma";
    public static final String SESSION_FIRMA_TOKEN = "firma_token";
    public static final String SESSION_FIRMA_COMPLETADA = "firma_completada";
    public static final String SESSION_FIRMA_ACCESO = "firma_acceso";

    // Atributos de request
    public static final String REQUEST_ERROR = "error";

    // Viafirma
    public static final String VIAFIRMA_USERNAME = System.getenv("VIAFIRMA_USERNAME") != null
            ? System.getenv("VIAFIRMA_USERNAME")
            : "upo_practices";
    public static final String VIAFIRMA_PASSWORD = System.getenv("VIAFIRMA_PASSWORD") != null
            ? System.getenv("VIAFIRMA_PASSWORD")
            : "MSW4zyVXBax3";

    // Formatos de fecha
    public static final String DATE_FORMAT_PDF = "dd/MM/yyyy HH:mm:ss";
    public static final String DATE_FORMAT_FILENAME = "yyyyMMdd_HHmmss";

    // Mensajes
    public static final String MSG_ERROR_NO_AUTH = "Debes autenticarte primero para firmar documentos";
    public static final String MSG_ERROR_MOTIVO_VACIO = "El motivo de la solicitud es requerido";
    public static final String MSG_ERROR_CONSENT_REQUERIDO = "Debes consentir la verificación de identidad";
    public static final String MSG_ERROR_PARTICIPACION_REQUERIDA = "Debes confirmar tu participación en el procedimiento";
}
