<%@ page import="Enums.ConnectionResult" %>
<%@ page import="Enums.ErrorField" %>
<%@ page import="Enums.Form" %>
<%@ page import="Beans.ReservationB" %>
<jsp:useBean id="Result" class="Beans.ConnectionB" scope="session"/>
<header role="banner">
    <a href="${pageContext.request.contextPath}/Main" class="title"><h1>Inpres Airport</h1></a>
    <nav class="main-nav">
        <ul>
            <%
                ConnectionResult cr = Result.getResult();
                ReservationB tempreser;
                if (cr == null || cr == ConnectionResult.FAIL) {
                    out.println("<li><a class=\"cd-signin nav-menu\" href=\"#\">Sign in</a></li>");
                    out.println("<li><a class=\"cd-signup nav-menu\" href=\"#\">Sign up</a></li>");
                    if(session.getAttribute("reservation") != null) {
                        tempreser = (ReservationB) session.getAttribute("reservation");
                        out.println("<li><a class=\"panier\" href=\"#volet\"><img id=\"panierimg\" src=\"img/shopping-cart.svg\" style=\"border: 0; float: left; margin-right: 15px\" />Mon panier : " + tempreser.getNbrReservation() + " tickets</a></li>");
                    }
                    //TODO Message d'erreur lorsque le login foire
            /*if(type!=null && type.equals("fail")){
                out.println("<h1 style = \" text-align : center\">Correspondance mot de passe / email incorrect </h1>");
            }*/
                } else {
                    String user = (String) session.getAttribute("user");
                    out.println("<li><p class=\"user\">Connect\u00e9 : " + user + " </p></li>");
                    //request.setAttribute("type", "logout");
                    out.println("<li><form method=\"post\" action=\"Main\" id=\"DC\">");
                    out.println("<a class=\"logout\" onClick=\"doPost(this)\" href='#'>Deconnexion</a>");
                    out.println("<input type='hidden' value='logout' name='type'/>");
                    out.println("</form></li>");
                }
            %>
            <!-- <ul> -->
            <!-- inser more links here -->
            <!-- <li><a class="cd-signin" href="#0">Sign in</a></li>
            <li><a class="cd-signup" href="#0">Sign up</a></li>
            </ul> -->
            <li id="caddie">
                <a href="#" class="nav-menu cd-caddie"><img src="" alt="Caddie"/></a>
                <div class="cd-caddie-menu">
                    <table>
                        <thead>
                            <tr><th>AA</th><th>BB</th></tr>
                        </thead>
                        <tfoot>
                            <tr><th colspan="2"></th></tr>
                        </tfoot>
                        <tbody>
                            <tr><td>Objet</td><td>Prix</td></tr>
                        </tbody>
                    </table>
                </div>
            </li>
        </ul>
    </nav>
</header>
<!-- LOG IN -->
<div class="cd-user-modal ${Result.place != null ? "is-visible" : ""}">
    <!-- this is the entire modal form, including the background -->
    <div class="cd-user-modal-container"> <!-- this is the container wrapper -->
        <ul class="cd-switcher">
            <li><a href="#" ${Result.place == Form.LOGIN ? "class='selected'" : ""}>Sign in</a></li>
            <li><a href="#" ${Result.place == Form.SIGNIN ? "class='selected'" : ""}>New account</a>
            </li>
        </ul>

        <div id="cd-login" ${Result.place == Form.LOGIN ? "class='is-selected'" : ""}>
            <!-- log in form -->
            <form method="post" action="Main" class="cd-form">
                <p class="fieldset">
                    <label class="image-replace cd-email" for="signin-email">E-mail</label>
                    <input class="full-width has-padding has-border" id="signin-email" type="email"
                           name="mail"
                           placeholder="E-mail" value="${param["mail"]}">
                    <span class="cd-error-message ${Result.result == ConnectionResult.FAIL && Result.field == ErrorField.EMAIL && Result.place == Form.LOGIN? "is-visible" : ""}">${Result.errorMessage}</span>
                </p>

                <p class="fieldset">
                    <label class="image-replace cd-password" for="signin-password">Password</label>
                    <input class="full-width has-padding has-border" id="signin-password"
                           type="password"
                           name="pass"
                           placeholder="Password">
                    <a href="#" class="hide-password">Show</a>
                    <span class="cd-error-message ${Result.result == ConnectionResult.FAIL && Result.field == ErrorField.PASSWORD && Result.place == Form.LOGIN? "is-visible" : ""}">${Result.errorMessage}</span>
                </p>

                <%--<p class="fieldset">--%>
                <%--<input type="checkbox" id="remember-me" checked>--%>
                <%--<label for="remember-me">Remember me</label>--%>
                <%--</p>--%>

                <p class="fieldset">
                    <input id="type" type="hidden" name="type" value="signin">
                    <input class="full-width" type="submit" value="Login">
                </p>
            </form>

            <%--<p class="cd-form-bottom-message"><a href="#">Forgot your password?</a></p>--%>
            <!-- <a href="#0" class="cd-close-form">Close</a> -->
        </div> <!-- cd-login -->

        <div id="cd-signup" ${Result.place == Form.SIGNIN ? "class='is-selected'" : ""}>
            <!-- sign up form -->
            <form method="post" class="cd-form" action="Main">
                <p class="fieldset">
                    <label class="image-replace cd-username" for="signup-username">Username</label>
                    <input class="full-width has-padding has-border" id="signup-username"
                           type="text"
                           name="username"
                           placeholder="Username" value="${param["username"]}">
                    <span class="cd-error-message ${Result.result == ConnectionResult.FAIL && Result.field == ErrorField.LOGIN && Result.place == Form.SIGNIN? "is-visible" : ""}">${Result.errorMessage}</span>
                </p>

                <p class="fieldset">
                    <label class="image-replace cd-email" for="signup-email">E-mail</label>
                    <input class="full-width has-padding has-border" id="signup-email" type="email"
                           name="mail"
                           placeholder="E-mail" value="${param["email"]}">
                    <span class="cd-error-message ${Result.result == ConnectionResult.FAIL && Result.field == ErrorField.EMAIL && Result.place == Form.SIGNIN? "is-visible" : ""}">${Result.errorMessage}</span>
                </p>

                <p class="fieldset">
                    <label class="image-replace cd-password" for="signup-password">Password</label>
                    <input class="full-width has-padding has-border" id="signup-password"
                           type="password"
                           name="pass"
                           placeholder="Password">
                    <a href="#" class="hide-password">Show</a>
                    <span class="cd-error-message ${Result.result == ConnectionResult.FAIL && Result.field == ErrorField.PASSWORD && Result.place == Form.SIGNIN? "is-visible" : ""}">${Result.errorMessage}</span>
                </p>

                <%--<p class="fieldset">--%>
                <%--<input type="checkbox" id="accept-terms">--%>
                <%--<label for="accept-terms">I agree to the <a href="#">Terms</a></label>--%>
                <%--</p>--%>

                <p class="fieldset">
                    <input id="typeSignup" type="hidden" name="type" value="signup">
                    <input class="full-width has-padding" type="submit" value="Create account">
                </p>
            </form>

            <!-- <a href="#0" class="cd-close-form">Close</a> -->
        </div> <!-- cd-signup -->

        <div id="cd-reset-password"> <!-- reset password form -->
            <p class="cd-form-message">Lost your password? Please enter your email address. You will
                receive a link
                to
                create a new password.</p>

            <form class="cd-form">
                <p class="fieldset">
                    <label class="image-replace cd-email" for="reset-email">E-mail</label>
                    <input class="full-width has-padding has-border" id="reset-email" type="email"
                           placeholder="E-mail">
                    <span class="cd-error-message ${Result.result == ConnectionResult.FAIL && Result.field == ErrorField.EMAIL ? "is-visible" : ""}">${Result.errorMessage}</span>
                </p>

                <p class="fieldset">
                    <input class="full-width has-padding" type="submit" value="Reset password">
                </p>
            </form>

            <p class="cd-form-bottom-message"><a href="#">Back to log-in</a></p>
        </div> <!-- cd-reset-password -->
        <a href="#" class="cd-close-form">Close</a>
    </div> <!-- cd-user-modal-container -->
</div>
${Result.place = null}
<!-- cd-user-modal -->

<!------ CADDIE ------->
<div id="volet_clos">
    <div id="volet">
        <h3>Liste de vos articles</h3>
        <p>Article 1</p><br />
        <p>Article 1</p><br />
        <p>Article 1</p><br />
        <p>Article 1</p><br />
        <br />
        <a href="#volet_clos" class="fermer">fermer !</a>
        <form method="post" action="Caddie">
            <input id="get" type="hidden" name="type" value="get">
            <input class="buyBtn" type="submit" value="Details">
        </form>
    </div>
</div>

<!---- FIN CADDIE ----->