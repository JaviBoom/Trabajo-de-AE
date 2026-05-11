# Memoria Técnica: Sede Electrónica de Firma y Registro
**Asignatura**: Administración Electrónica  
**Proyecto**: Portal de Gestión y Validación de Trámites Electrónicos

---

## 1. Introducción
El presente proyecto consiste en el desarrollo de una **Sede Electrónica** integral diseñada para cumplir con los estándares de interoperabilidad y seguridad requeridos en la administración pública. El sistema cubre el ciclo de vida completo de un trámite electrónico: desde la identificación del ciudadano hasta la obtención de un justificante firmado y registrado oficialmente.

## 2. Objetivos del Proyecto
*   Implementar un sistema de **identificación digital** basado en certificados reconocidos.
*   Automatizar la **generación y firma** de documentos en formatos longevos (PAdES y XAdES).
*   Garantizar la trazabilidad mediante un **Libro de Registro General**.
*   Proveer una herramienta de **validación técnica** para la verificación de integridad y autoría de los documentos.

## 3. Arquitectura Técnica
La solución se ha desarrollado siguiendo una arquitectura robusta y escalable:
*   **Backend**: Tecnologías Java EE (Servlets 4.0 y JSP) gestionadas con Maven.
*   **Servicios de Confianza**: Integración con la API de Viafirma mediante un cliente generado por OpenAPI.
*   **Generación de Documentos**: Uso de la librería `iText7` para la creación de PDFs con estándares institucionales.
*   **Frontend**: Interfaz dinámica basada en CSS3 (Flexbox/Grid) que garantiza una experiencia de usuario limpia y profesional.

## 4. Funcionalidades Implementadas

### 4.1. Portal Dinámico e Identificación
La sede implementa un control de estado que personaliza la experiencia del usuario. El acceso a los trámites de firma está restringido hasta que el ciudadano se identifique mediante **Viafirma Auth**, momento en el cual el portal desbloquea las opciones de gestión y personaliza la cabecera con los datos del certificado.

### 4.2. Firma y Registro Automático
El sistema permite la firma por lotes (Batch) de un justificante PDF y un archivo de metadatos XML. Tras la firma exitosa, el sistema realiza automáticamente un **asiento en el Libro de Registro**, asignando un número de registro único y persistiendo los datos de la operación para su consulta pública.

### 4.3. Validador de Documentos
Se ha desarrollado un motor de validación que consulta la API de Viafirma para obtener un informe detallado de cualquier documento firmado. La herramienta verifica:
*   Integridad del contenido (que el documento no haya sido alterado).
*   Validez de los certificados del firmante.
*   Sellado de tiempo y fuentes de confianza (AATL, EUTL, etc.).

## 5. Retos Técnicos y Soluciones
Durante el desarrollo se superaron retos significativos que demuestran la profundidad técnica del proyecto:
1.  **Optimización de Descargas (403 Forbidden)**: Se implementó una lógica de recuperación de enlaces directos desde el cliente para evitar bloqueos de infraestructura en la descarga de archivos firmados.
2.  **Extensión de la Librería API**: Se realizó una modificación manual en la capa de modelos de la librería cliente (`TrustedSourcesEnum`) para dar soporte a nuevas fuentes de confianza (`TL`) devueltas por la API, evitando errores de deserialización.
3.  **Diseño Simétrico**: Uso de técnicas avanzadas de CSS para garantizar que el portal mantenga una estética profesional y equilibrada independientemente del contenido dinámico.

## 6. Conclusión
El portal resultante no es solo un demostrador tecnológico, sino una solución funcional que aplica los principios de la Ley 39/2015, permitiendo una gestión electrónica segura, transparente y eficiente.
