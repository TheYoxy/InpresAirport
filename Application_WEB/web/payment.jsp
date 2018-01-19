<%@ page import="java.util.Map" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Mon caddie</title>
    <!--<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/payment.css"/>-->
    <link type="text/css" rel="stylesheet" href="http://fonts.googleapis.com/css?family=Open+Sans"/>
    <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
    <%@ include file="Include/Login/Head.jsp" %>
    <%@ include file="Include/Login/Script.jsp" %>
    <link type="text/css" rel="stylesheet" href="css/payment.css"/>
    <meta charset="UTF-16BE">
</head>

<body>
<%@include file="Include/Login/LoginHeader.jsp" %>
<h2>Information à propos de votre commande:</h2>
<table class="tablepayement">
    <thead>
    <tr>
        <th>Numéro du vol</th>
        <th>Numéro du billet</th>
    </tr>
    </thead>
    <tbody>
    <%
        Map<Integer, List<String>> map = (Map<Integer, List<String>>) session.getAttribute("payement");
        if (map != null) {
            for (Map.Entry<Integer, List<String>> e : map.entrySet()) {
                out.println("<tr><td style='border-top: solid .5px'>");
                out.println(e.getKey());
                out.println("</td>");
                if (e.getValue().size() != 0)
                    out.println("<td style='border-top: solid .5px'>" + e.getValue().get(0) + "</td>");
                out.println("</tr>");
                for (String s : e.getValue()) {
                    out.println("<tr><td/><td>" + s + "</td></tr>");
                }
            }
        }
    %>
    </tbody>
</table>
</body>
</html>
