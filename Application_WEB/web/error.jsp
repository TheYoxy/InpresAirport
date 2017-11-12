<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>InpresAirport</title>
    <%@include file="Include/Login/Head.jsp"%>
    <link type="text/css" rel="stylesheet" href="http://fonts.googleapis.com/css?family=Open+Sans"/>
    <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body>
<%@include file="Include/Login/LoginHeader.jsp" %>

<h2>Erreur</h2>
<p>Une erreur est survenue durant le traitement.</p>
<p><%
    Exception e = (Exception) request.getAttribute("Exception");
    if (e != null)
        out.println(e.getLocalizedMessage());
%></p>
<%@include file="footer.jsp" %>
<%@include file="Include/Login/Script.jsp" %>
</body>
</html>
