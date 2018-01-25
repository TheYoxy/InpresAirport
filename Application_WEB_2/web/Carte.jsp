<%@ page import="Servlets.MainServlet" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:useBean id="list" type="java.util.List" scope="session"/>
<html>
<head>
    <title>Carte</title>
    <!-- JS -->
    <script src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.15.1/moment.min.js"></script>
    <!-- CSS -->
    <link rel="stylesheet"
          href="css/bootstrap.min.css">
    <link rel="stylesheet"
          href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <link rel="stylesheet"
          href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css">
    <link type="text/css" rel="stylesheet" href="http://fonts.googleapis.com/css?family=Open+Sans"/>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>
<div class="container">
    <div class="row">
        <h2>Carte pour le payement: </h2>
    </div>
    <div class="row">
        <form action="Main" method="post">
            <input type="hidden" name="Type"
                   value="<%=MainServlet.WebType.CARTE.toString()%>">
            <div class="form-group">
                <label for="proprio">Propriétaire de la carte: </label>
                <select class="form-control" id="proprio" name="proprio">
                    <% for (int i = 0; i < list.size(); i++) {%>
                    <option value="<%=i%>">
                        <%=list.get(i)%>
                    </option>
                    <%}%>
                </select>
            </div>
            <div class="form-group">
                <label for="card">Numéro de carte: </label>
                <input type="number" class="form-control" name="card" id="card">
            </div>
            <input type="submit" class="btn btn-default" value="Confirmer">
        </form>
    </div>
</div>
</body>
</html>
