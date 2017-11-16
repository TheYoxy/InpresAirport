<%--
  Created by IntelliJ IDEA.
  User: Nicolas
  Date: 16-11-17
  Time: 14:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Mon caddie</title>
        <%@ include file="Include/Login/Head.jsp" %>
        <%@include file="Include/Login/Script.jsp" %>
        <script src="js/caddie"></script>
        <link type="text/css" rel="stylesheet" href="css/style.css"/>
        <meta charset="UTF-16BE">
    </head>


<body>
    <%@include file="Include/Login/LoginHeader.jsp" %>
    <% int nbrArticles = 3 ; %>
    <h1>Shopping Cart</h1>

    <div class="shopping-cart">

        <div class="column-labels">
            <label class="product-image">Image</label>
            <label class="product-details">Produit</label>
            <label class="product-price">Prix</label>
            <label class="product-quantity">Quantité</label>
            <label class="product-removal">Rerirer</label>
            <label class="product-line-price">Total</label>
        </div>

        <%
            if ( nbrArticles != 0) {
                int i = nbrArticles;
                    while (i !=0) {
                        out.println("<div class=\"product\">");
                        out.println("<div class=\"product-image\">");
                        out.println("<img src=\"https://s.cdpn.io/3/dingo-dog-bones.jpg\">");
                        out.println("</div>");
                        out.println("<div class=\"product-details\">");
                        out.println("<div class=\"product-title\">Dingo Dog Bones</div>");
                        out.println("<p class=\"product-description\">The best dog bones of all time. Holy crap. Your dog will be begging for these things! I got curious once and ate one myself. I'm a fan.</p>");
                        out.println("</div>");
                        out.println("<div class=\"product-price\">12.99</div>");
                        out.println("<div class=\"product-quantity\">");
                        out.println("<input type=\"number\" value=\"2\" min=\"1\">");
                        out.println("</div>");
                        out.println("<div class=\"product-removal\">");
                        out.println("<button class=\"remove-product\">");
                        out.println("Remove");
                        out.println("</button>");
                        out.println("</div>");
                        out.println("<div class=\"product-line-price\">25.98</div>");
                        out.println("</div>");

                        i--;
                    }
            } else
                out.println("<h2>Aucun articles dans le caddie</h2>");
        %>


        <div class="totals">
            <div class="totals-item">
                <label>Subtotal</label>
                <div class="totals-value" id="cart-subtotal">71.97</div>
            </div>
            <div class="totals-item">
                <label>Tax (5%)</label>
                <div class="totals-value" id="cart-tax">3.60</div>
            </div>
            <div class="totals-item">
                <label>Shipping</label>
                <div class="totals-value" id="cart-shipping">15.00</div>
            </div>
            <div class="totals-item totals-item-total">
                <label>Grand Total</label>
                <div class="totals-value" id="cart-total">90.57</div>
            </div>
        </div>

        <button class="checkout">Checkout</button>

    </div>


</body>
</html>
