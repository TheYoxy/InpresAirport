<%@ page import="java.io.PrintWriter" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>InpresAirport</title>
    <link type="text/css" rel="stylesheet" href="http://fonts.googleapis.com/css?family=Open+Sans"/>
    <meta charset="UTF-16BE">
</head>
<body>
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
                e.printStackTrace(new PrintWriter(out));
        %>
    </p>
</div>
</body>
</html>
