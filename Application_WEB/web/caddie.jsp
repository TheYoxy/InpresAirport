<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.SQLException" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Mon caddie</title>
    <link type="text/css" rel="stylesheet" href="http://fonts.googleapis.com/css?family=Open+Sans"/>
    <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
    <%@ include file="Include/Login/Head.jsp" %>
    <%@include file="Include/Login/Script.jsp" %>
    <script src="js/caddie.js"></script>
    <link type="text/css" rel="stylesheet" href="css/style.css"/>
    <meta charset="UTF-16BE">
</head>


<body>
<%@include file="Include/Login/LoginHeader.jsp" %>
<h1>Vos billets </h1>

<div class="shopping-cart">

    <div class="column-labels">
        <label class="product-image">Image</label>
        <label class="product-details">Produit</label>
        <label class="product-price">Prix</label>
        <label class="product-quantity">Quantité</label>
        <label class="product-removal">Rerirer</label>
        <label class="product-line-price">Total</label>
    </div>
    <script>
        recalculateCart();
    </script>
    <%
        ResultSet rs = (ResultSet) request.getAttribute("Vols");
        if (session.getAttribute("reservation") != null) {
            List<ReservationB> reservation = (List<ReservationB>) session.getAttribute("reservation");
            if (reservation.size() != 0) {
                int i;
                try {
                    for (i = 0, rs.next(); i < reservation.size(); i++, rs.next()) {
                        out.println("<div class=\"product\">");
                        out.println("   <div class=\"product-image\">");
                        out.println("       <img src=\"https://s.cdpn.io/3/dingo-dog-bones.jpg\">");
                        out.println("   </div>");
                        out.println("   <div class=\"product-details\">");
                        out.println("       <div class=\"product-title\">Vol numero " + reservation.get(i).getNumVol() + " | " + rs.getString("Lieu") + "</div>");
                        out.println("           <p class=\"product-description\">" + rs.getString("Description") + "</p>");
                        out.println("       </div>");
                        out.println("   <div class=\"product-price\">" + rs.getDouble("Prix") + "</div>");
                        out.println("   <div class=\"product-quantity\">");
                        out.println("       <input type=\"number\" value=\"" + reservation.get(i).getNbrPlaces() + "\" min=\"1\" onclick=\"updateQuantity(this)\" >");
                        out.println("   </div>");
                        out.println("   <div class=\"product-removal\">");
                        out.println("       <form method=\"post\" action=\"Caddie\">");
                        out.println("       <input type='hidden' value='"+reservation.get(i).getNumVol()+"' name=\"vol\"/>");
                        out.println("       <input type='hidden' value=\"remove\" name='type'/>");
                        out.println("       <input class=\"remove-product\" type=\"submit\" value=\"Remove\" onclick=\"removeItem(this)\">");
                        out.println("       </form>");
                        out.println("   </div>");
                        out.println("   <div class=\"product-line-price\">" + (rs.getDouble("Prix") * reservation.get(i).getNbrPlaces()) + "</div>");
                        out.println("</div>");
                    }
                } catch (SQLException e) {

                }
            } else
                out.println("<h2>Aucun articles dans le caddie</h2>");
        } else
            out.println("<h2>Aucun articles dans le caddie</h2>");
    %>


    <div class="totals">
        <div class="totals-item">
            <label>Sous-total</label>
            <div class="totals-value" id="cart-subtotal">0.0</div>
        </div>
        <div class="totals-item">
            <label>TVA (21%)</label>
            <div class="totals-value" id="cart-tax">0.0</div>
        </div>
        <div class="totals-item">
            <label>Shipping</label>
            <div class="totals-value" id="cart-shipping">0.0</div>
        </div>
        <div class="totals-item totals-item-total">
            <label>Total</label>
            <div class="totals-value" id="cart-total">0.0</div>
        </div>
    </div>

    <button class="checkout">Payement</button>

</div>
</body>
</html>
