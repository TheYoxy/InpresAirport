<%@ page import="Servlets.MainServlet" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:useBean id="p" type="NetworkObject.Bean.Places" scope="session"/>
<html>
<head>
    <title>Confirmation</title>
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
        <h1 class="text-center">Résumé de la réservation:</h1>
    </div>
    <div class="row">
        <h3>Prix total: ${p.prix}</h3>
    </div>
    <div class="row">
        <table class="table table-hover">
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
    </div>
    <div class="row">
        <div class="col-lg-3">
            <form action="Main" method="post">
                <input type="hidden" name="Type"
                       value="<%=MainServlet.WebType.CONFIRMATION_PLACES.toString()%>">
                <input type="hidden" name="Conf" value="true">
                <input type="submit" class="btn btn-success btn-block" value="Confirmer">
            </form>
        </div>
        <div class="col-lg-3">
            <form action="Main" method="post">
                <input type="hidden" name="Type"
                       value="<%=MainServlet.WebType.CONFIRMATION_PLACES.toString()%>">
                <input type="hidden" name="Conf" value="false">
                <input type="submit" class="btn btn-danger btn-block" value="Annuler">
            </form>
        </div>
    </div>
</div>
</body>
</html>
