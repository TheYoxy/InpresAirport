<%@ page import="Servlets.MainServlet" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:useBean id="selected" type="java.util.Vector" scope="session"/>
<jsp:useBean id="nbPlaces" type="java.lang.String" scope="request"/>
<html>
<head>
    <title>Détails des passagers</title>
    <!-- JS -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.15.1/moment.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.7.14/js/bootstrap-datetimepicker.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.3.0/js/bootstrap-datepicker.js"></script>
    <!-- CSS -->

    <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
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
<div class="container-fluid">
    <div class="row">
        <div class="col-lg-6 col-lg-offset-3">
            <h1>Vol ${selected.get(0)}</h1>
            <h3>Destination: ${selected.get(1)}</h3>
            <form class="form-horizontal" action="Main"
                  method="post">
                <%
                    int nb = Integer.parseInt(nbPlaces);
                    for (int i = 0; i < nb; i++)
                        out.println("                <div class=\"well\">\n" +
                                "                    <h4>Passager n°" + (i + 1) + " :</h4>\n" +
                                "                    <label for=\"nom" + i + "\">Nom: </label>\n" +
                                "                    <input id=\"nom" + i + "\" name=\"Nom\" class=\"form-control\" type=\"text\"\n" +
                                "                           placeholder=\"Nom\" required>\n" +
                                "                    <label for=\"Prenom" + i + "\">Prenom: </label>\n" +
                                "                    <input id=\"Prenom" + i + "\" name=\"Prenom\" class=\"form-control\" type=\"text\"\n" +
                                "                           placeholder=\"Prenom\" required>\n" +
                                "                    <label for=\"Naissance" + i + "\">Date de naissance:</label>\n" +
                                "                       <div class='input-group date' id='dtp" + i + "' data-date-format=\"dd-mm-yyyy\">" +
                                "                       <input id=\"Naissance" + i + "\" name=\"Naissance\" class=\"form-control\" type=\"text\" required>\n" +
                                "                       <span class=\"input-group-addon\">\n" +
                                "                         <span class=\"glyphicon glyphicon-calendar\"></span>\n" +
                                "                       </span>\n" +
                                "                    </div>\n" +
                                "                </div>\n");
                %>
                <input type="hidden" name="Type"
                       value="<%=MainServlet.WebType.PASSAGERS.toString()%>">
                <input type="submit" class="btn btn-default col-lg-6 col-lg-offset-3">
            </form>
        </div>
    </div>
</div>
<script type="text/javascript">
    $(function () {
        <% for (int i = 0; i < 2; i++)
            out.println("$('#dtp" + i + "').datepicker();\n");
        %>
    });
</script>
</body>
</html>
