<%@ page import="java.sql.Timestamp" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="NetworkObject.Bean.Voyageur" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:useBean id="selected" type="java.util.Vector" scope="session"/>
<jsp:useBean id="p" type="NetworkObject.Bean.Places" scope="session"/>
<jsp:useBean id="list" type="java.util.List" scope="session"/>
<jsp:useBean id="id" type="java.lang.String" scope="request"/>
<jsp:useBean id="c" type="NetworkObject.Bean.Carte" scope="request"/>
<html>
<head>
    <title>OUI</title>
    <!-- JS -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.15.1/moment.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.7.14/js/bootstrap-datetimepicker.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.3.0/js/bootstrap-datepicker.js"></script>
    <!-- CSS -->
    <link rel="stylesheet"
          href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <link rel="stylesheet"
          href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css">

    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.3.0/css/datepicker.css"
          rel="stylesheet">
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.7.14/css/bootstrap-datetimepicker.min.css">

    <link type="text/css" rel="stylesheet" href="http://fonts.googleapis.com/css?family=Open+Sans"/>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>
<div class="container">
    <div class="row">
        <h1 class="text-center">
            Résumé de la transaction
        </h1>
    </div>
    <div class="row">
        <p>
            Vous avez validé votre réservation pour le vol n°<em>${selected.get(0)}</em> à
            destination de ${selected.get(1)}.<br/>
            Veuillez vous présenter le <strong>
            <%! DateTimeFormatter dtf = DateTimeFormatter.ofPattern("d-MM-yyyy");%>
            <%= Timestamp.valueOf((String) selected.get(2)).toLocalDateTime().toLocalDate().format(dtf) %>
        </strong> à l'INPRESAIRPORT muni de vos billets.
        </p>
        <p>Prix total: <strong>${p.prix}</strong>€</p>
        <p>Id de la transaction: <em>${id}</em></p>
        <p>Info sur la carte:
        <ul class="card">
            <li>Numéro de carte: ${c.numeroCarte}</li>
            <li>Nom du propriétaire: ${c.voyageur.nom}</li>
            <li>Prenom du propriétaire: ${c.voyageur.prenom}</li>
            <li>Date de naissance du propriétaire: <%=c.getVoyageur().getNaissance().format(dtf)%>
            </li>
        </ul>
    </div>
    <div class="row">
        <h3 class="text-center">Liste des billets: </h3>
    </div>
    <div class="row">
        <table class="table table-hover">
            <thead>
            <tr>
                <th>Numéro de billet:</th>
                <th>Nom:</th>
                <th>Prénom:</th>
                <th>Date de naissances:</th>
            </tr>
            </thead>
            <tbody>
            <%
                for (int i = 0; i < p.getNumPlaces().size(); i++) {
                    String   s = p.getNumPlaces().get(i);
                    Voyageur v = (Voyageur) list.get(i);
            %>
            <tr>
                <td>
                    <%= s %>
                </td>
                <td>
                    <%= v.getNom()%>
                </td>
                <td>
                    <%= v.getPrenom()%>
                </td>
                <td>
                    <%= v.getNaissance().format(dtf)%>
                </td>
            </tr>
            <%}%>
            </tbody>
        </table>
    </div>

</div>
</body>
</html>
