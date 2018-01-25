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
        <h2 class="text-center">Réservations web</h2>
        <form action="${pageContext.request.contextPath}/Login" method="post">
            <div class="form-group">
                <label for="username">User: </label>
                <input id="username" type="text" class="form-control" name="username">
            </div>
            <div class="form-group">
                <label for="password">Password: </label>
                <input id="password" type="password" class="form-control" name="password">
            </div>
            <input type="submit" class="btn btn-default col-lg-12" value="Log">
        </form>
    </div>
</div>
</body>
</html>
