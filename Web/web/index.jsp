<%@ page import="Tools.Procedural" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.SQLException" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>InpresAirport</title>
    <link type="text/css" rel="stylesheet" href="http://fonts.googleapis.com/css?family=Open+Sans"/>
    <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body>
<%@include file="header.jsp" %>
<h1>Inpres Airport</h1>
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
                    out.println("        <span class=\"date\">" + rs.getDate("Date") + "</span>");
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
</body>
</html>