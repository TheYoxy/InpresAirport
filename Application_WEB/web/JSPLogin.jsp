<%--
  Created by IntelliJ IDEA.
  User: Nicolas
  Date: 06-11-17
  Time: 16:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page info="(c) BNSF - annee 2017" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Login</title>
</head>
<body>
    <%! Date current = new Date(); %>
    <%! String currentDate = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.FRANCE).format(current); %>
    <!-- Page demandée le <%=currentDate%> -->

    <H2> Caddie virtuelle</H2>
    <p>Nous sommes le : <%=currentDate%> </p>
    <p> LOGIN REUSSIS</p>
    <p>Bienvenue :
        <% String nom = request.getParameter("nom");
        String prenom = request.getParameter("prenom");
        String mdp = request.getParameter("mdp");
        %>
        <%= nom%> <%=prenom%> (*** <%=mdp%> ***)

        (Genere par la servlet <%=getServletInfo()%>)
    </p>
</body>
</html>
