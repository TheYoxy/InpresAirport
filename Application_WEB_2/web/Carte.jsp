<%@ page import="Servlets.MainServlet" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="list" type="java.util.List" scope="session"/>
<html>
<head>
    <title>Carte</title>
</head>
<body>
<form action="Main" method="post">
    <input type="hidden" name="Type"
           value="<%=MainServlet.WebType.CARTE.toString()%>">
    <label for="proprio">Propriétaire de la carte: </label>
    <select id="proprio" name="proprio">
        <% for (int i = 0; i < list.size(); i++) {%>
        <option value="<%=i%>">
            <%=list.get(i)%>
        </option>
        <%}%>
    </select>
    <label for="card"></label>
    <input type="number" name="card" id="card">
    <input type="submit" value="Confirmer">
</form>
</body>
</html>
