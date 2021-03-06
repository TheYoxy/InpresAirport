<%@ page import="java.util.List" %>
<%@ page import="Beans.ReservationB" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:useBean id="Result" class="Beans.ConnectionB" scope="session"/>
<header role="banner">
    <a href="${pageContext.request.contextPath}/" class="title"><h1>Inpres Airport</h1></a>
    <nav class="main-nav">
        <ul>
            <%
                String user = (String) session.getAttribute("user");
                //TODO Fix si tu reviens d'une autre page => Forcément nouvel utilisateur
                if (user == null) {
                    out.println("<li><a class=\"cd-signin nav-menu\" href=\"#\">Connexion</a></li>");
                    out.println("<li><a class=\"cd-signup nav-menu\" href=\"#\">Nouveau compte</a></li>");
//                    if (session.getAttribute("reservation") != null) {
//                        tempreser = (ReservationB) session.getAttribute("reservation");
//                        out.println("<li><a class=\"panier\" href=\"#volet\"><img id=\"panierimg\" src=\"" + request.getContextPath() + "/Include/Login/img/cd-icon-caddie.svg\" style=\"border: 0; float: left; margin-right: 15px\" />Mon panier : " + tempreser.getNbrReservation() + " tickets</a></li>");
//                    }
                    //TODO Message d'erreur lorsque le login foire
                    /*if(type!=null && type.equals("fail")){
                        out.println("<h1 style = \" text-align : center\">Correspondance mot de passe / email incorrect </h1>");
                    }*/
                } else {
                    out.println("<li><p class=\"user nav-menu\">Connect\u00e9 : " + user + " </p></li>");
                    out.println("<li><form method=\"post\" action=\"Main\" id=\"DC\">");
                    out.println("<button class=\"logout nav-menu\" type='submit'>Deconnexion</button>");
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
                <a href="#" class="nav-menu cd-caddie">
                    <img id="img-caddie"
                         src="${pageContext.request.contextPath}/Include/Login/img/cd-icon-caddie.svg"
                         alt="Caddie"/>
                </a>
                <div class="cd-caddie-menu">
                    <form method="post" action="Caddie">
                        <input type="hidden" name="type" value="get">
                        <table class="cd-caddie-resume">
                            <thead>
                            <tr>
                                <th>Vol</th>
                                <th>Nombre de places</th>
                            </tr>
                            </thead>
                            <tfoot>
                            <tr>
                                <th colspan="2">
                                    <input class="buyBtn" type="submit" value="Details">
                                </th>
                            </tr>
                            </tfoot>
                            <tbody>
                            <%
                                List<ReservationB> lb = (List<ReservationB>) session.getAttribute("reservation");
                                if (lb != null)
                                    for (ReservationB rb : lb) {
                                        out.println("<tr>");
                                        out.println("   <td>" + rb.getNumVol() + "   (<span class='b'>" + rb.getInfosVol().get(1) + "</span>)</td>");
                                        out.println("   <td>" + rb.getNbrPlaces() + "</td>");
                                        out.println("</tr>");
                                    }
                            %>
                            </tbody>
                        </table>
                    </form>
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
                           placeholder="E-mail" value="${params["mail"]}">
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
            <!-- <a href="#0" class="cd-close-form">close</a> -->
        </div> <!-- cd-login -->

        <div id="cd-signup" ${Result.place == Form.SIGNIN ? "class='is-selected'" : ""}>
            <!-- sign up form -->
            <form method="post" class="cd-form" action="Main">
                <p class="fieldset">
                    <label class="image-replace cd-username" for="signup-username">Username</label>
                    <input class="full-width has-padding has-border" id="signup-username"
                           type="text"
                           name="username"
                           placeholder="Username" value="${params["username"]}">
                    <span class="cd-error-message ${Result.result == ConnectionResult.FAIL && Result.field == ErrorField.LOGIN && Result.place == Form.SIGNIN? "is-visible" : ""}">${Result.errorMessage}</span>
                </p>

                <p class="fieldset">
                    <label class="image-replace cd-email" for="signup-email">E-mail</label>
                    <input class="full-width has-padding has-border" id="signup-email" type="email"
                           name="mail"
                           placeholder="E-mail" value="${params["email"]}">
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

            <!-- <a href="#0" class="cd-close-form">close</a> -->
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
        <p>Article 1</p><br/>
        <p>Article 1</p><br/>
        <p>Article 1</p><br/>
        <p>Article 1</p><br/>
        <br/>
        <a href="#volet_clos" class="fermer">fermer !</a>
        <form method="post" action="Caddie">
            <input id="get" type="hidden" name="type" value="get">
            <input class="buyBtn" type="submit" value="Details">
        </form>
    </div>
</div>

<!---- FIN CADDIE ----->