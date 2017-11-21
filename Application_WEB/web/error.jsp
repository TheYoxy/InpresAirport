<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>InpresAirport</title>
    <%@include file="Include/Login/Head.jsp" %>
    <link type="text/css" rel="stylesheet" href="http://fonts.googleapis.com/css?family=Open+Sans"/>
    <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
    <meta charset="UTF-16BE">
</head>
<body>
<%@include file="Include/Login/LoginHeader.jsp" %>
<div class="body">
    <h2>Erreur</h2>
    <p>Une erreur est survenue durant le traitement.</p>
    <p><%
        String s = (String) request.getAttribute("From");
        if (s != null)
            out.println("Lieu:" + s);
    %></p>
    <p>
        <%
            Exception e = (Exception) request.getAttribute("Exception");
            if (e != null)
                out.println("Erreur: " + e.getLocalizedMessage());
        %>
    </p>
</div>
<%@include file="footer.jsp" %>
<%@include file="Include/Login/Script.jsp" %>
</body>
</html>
