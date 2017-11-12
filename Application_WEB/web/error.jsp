<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Error</title>
    <link type="text/css" rel="stylesheet" href="http://fonts.googleapis.com/css?family=Open+Sans"/>
    <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body>
<h1>Erreur</h1>
<p>Une erreur est survenue durant le traitement.</p>
<p><%
    Exception e = (Exception) request.getAttribute("Exception");
    if (e != null)
        out.println(e.getLocalizedMessage());
%></p>
</body>
</html>
