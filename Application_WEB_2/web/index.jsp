<%@ page contentType="text/html;charset=UTF-8" %>
<html lang="en">
<head>
    <title>Connexion</title>
    <!-- JS -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.15.1/moment.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.7.14/js/bootstrap-datetimepicker.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.3.0/js/bootstrap-datepicker.js"></script>
    <!-- CSS -->

    <link rel="stylesheet"
          href="css/bootstrap.min.css">
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
    <div class="col-lg-offset-3 col-lg-6">
        <h2 class="text-center">Bienvenue sur le site de réservation des billets de Inpres
            Airport.</h2>
        <h4>Veuillez vous identifier:</h4>
        <div class="row">
            <div class="col-lg-6">
                <form action="Login.jsp" method="get">
                    <input type="submit" class="btn btn-info btn-block" value="Compte existant">
                </form>
            </div>
            <div class="col-lg-6">
                <form action="NLogin.jsp" method="get">
                    <input type="submit" class="btn btn-info btn-block" value="Nouveau compte">
                </form>
            </div>
        </div>
    </div>
</div>
</body>
</html>
