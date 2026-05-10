<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // Si Viafirma redirige a la raíz, capturamos el código y lo reenviamos al CallbackServlet
    String code = request.getParameter("code");
    String error = request.getParameter("error");
    String pendingCode = (String) session.getAttribute("pending_viafirma_code");
    String globalCode = (String) application.getAttribute("GLOBAL_PENDING_CODE");

    if ((code != null && !code.trim().isEmpty()) || (error != null && !error.trim().isEmpty())) {
        response.sendRedirect("CallbackServlet?" + request.getQueryString());
        return;
    } else if (globalCode != null && !globalCode.trim().isEmpty()) {
        application.removeAttribute("GLOBAL_PENDING_CODE");
        response.sendRedirect("CallbackServlet?code=" + globalCode);
        return;
    } else if (pendingCode != null && !pendingCode.trim().isEmpty()) {
        session.removeAttribute("pending_viafirma_code");
        response.sendRedirect("CallbackServlet?code=" + pendingCode);
        return;
    }
%>
<jsp:forward page="/WEB-INF/jsp/pages/index.jsp" />
