<%@ page import="Tools.Procedural" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>InpresAirport</title>
    <%@ include file="Include/Login/Head.jsp" %>
    <link type="text/css" rel="stylesheet" href="http://fonts.googleapis.com/css?family=Open+Sans"/>
    <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>
<%@include file="Include/Login/LoginHeader.jsp" %>
<%
    ResultSet rs = (ResultSet) request.getAttribute("Vols");
    if (rs != null) {
        try {
            while (rs.next()) {
                if (rs.getInt("PlacesDisponible") > 0) {
                    out.println("<a href=\"#\" class=\"lachat\">\n");
                    out.println("    <div class=\"ticket\">");
                    out.println("        <span class=\"prix\">" + Procedural.round(rs.getDouble("Prix"), 2) + "&euro;</span>");
                    out.println("        <h2 class=\"title\">" + rs.getString("Lieu") + "</h2>");
                    out.println("        <span class=\"date\">" + new SimpleDateFormat("EEEEE dd MMMMM yyyy").format(rs.getDate("Date")) + "</span>");
                    String arg = rs.getString("Description");
                    if (arg != null) out.println("        <p>" + arg + "</p>");
                    out.println("    </div>");
                    out.println("</a>");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    } else
        out.println("<h2>Aucun vol n'est disponible à ce jour.</h2>");
%>
<!--
<a href="#" class="lachat">
    <div class="ticket">
        <span class="prix">500&euro;</span>
        <h2 class="title">Paris</h2>
        <span class="date">31 janvier 2018</span>
        <p>Coucou, tu veux voir ma bite ?<br>OUI<br></p>
    </div>
</a>
-->
<%@include file="footer.jsp" %>
<%@include file="Include/Login/Script.jsp" %>
</body>
</html>