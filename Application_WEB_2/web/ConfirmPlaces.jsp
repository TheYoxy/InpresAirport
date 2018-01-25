<%@ page import="Servlets.MainServlet" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="p" type="NetworkObject.Bean.Places" scope="session"/>
<html>
<head>
    <title>Confirmation</title>
</head>
<body>
<h3>Prix total: ${p.prix}</h3>
<table>
    <thead>
    <tr>
        <th>Numéro de billet:</th>
    </tr>
    </thead>
    <tbody>
    <%for (String s : p.getNumPlaces()) {%>
    <tr>
        <td>
            <%= s %>
        </td>
    </tr>
    <%}%>
    </tbody>
</table>
<form action="Main" method="post">
    <input type="hidden" name="Type"
           value="<%=MainServlet.WebType.CONFIRMATION_PLACES.toString()%>">
    <input type="hidden" name="Conf" value="true">
    <input type="submit" value="Confirmer">
</form>
<form action="Main" method="post">
    <input type="hidden" name="Type"
           value="<%=MainServlet.WebType.CONFIRMATION_PLACES.toString()%>">
    <input type="hidden" name="Conf" value="false">
    <input type="submit" value="Annuler">
</form>

</body>
</html>
