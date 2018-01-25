<%@ page import="Servlets.MainServlet" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!doctype html>
<html>
<head>
    <title>OUI</title>
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

</head>
<style>
    input::-webkit-outer-spin-button,
    input::-webkit-inner-spin-button {
        /* display: none; <- Crashes Chrome on hover */
        -webkit-appearance: none;
        margin: 0; /* <-- Apparently some margin are still there even though it's hidden */
    }
</style>
<body>
<div class="container">
    <div class="col-lg-offset-3 col-lg-6">
        <h2 class="text-center">Nombre de places à reserver</h2>
        <form id="form" action="Main" method="post">
            <input type="hidden" name="Type" value="<%=MainServlet.WebType.PLACES.toString()%>">
            <div class="form-group">
                <label for="NbPlaces">Nombre de places:</label>
                <div></div>
                <input id="NbPlaces" type="number" name="Places" class="form-control" min="0">
            </div>
            <button type="submit" class="btn btn-default col-xs-12">Valider</button>
        </form>
    </div>
</div>
</body>
</html>
