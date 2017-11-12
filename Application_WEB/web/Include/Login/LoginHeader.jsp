<header role="banner">
    <h1>Inpres Airport</h1>
    <nav class="main-nav">
        <%
            String type = (String) session.getAttribute("type");
            if (type == null || type.equals("fail")) {
                out.println("<ul>");
                out.println("<li><a class=\"cd-signin\" href=\"#0\">Sign in</a></li>");
                out.println("<li><a class=\"cd-signup\" href=\"#0\">Sign up</a></li>");
                out.println("</ul>");
            /*if(type!=null && type.equals("fail")){
                out.println("<h1 style = \" text-align : center\">Correspondance mot de passe / email incorrect </h1>");
            }*/
            } else {
                String user = (String) session.getAttribute("user");
                out.println("<ul>");
                out.println("<li><p class=\"user\" >Connecté : " + user + " </p></li>");
                //request.setAttribute("type", "logout");
                out.println("<li><a class=\"logout\" href=\"LoginServlet?type=logout\">Deconnexion</a></li>");
                out.println("</ul>");
            }
        %>
        <!-- <ul> -->
        <!-- inser more links here -->
        <!--  <li><a class="cd-signin" href="#0">Sign in</a></li>
          <li><a class="cd-signup" href="#0">Sign up</a></li>
      </ul> -->
    </nav>
</header>
<!-- LOG IN -->
<div class="cd-user-modal"> <!-- this is the entire modal form, including the background -->
    <div class="cd-user-modal-container"> <!-- this is the container wrapper -->
        <ul class="cd-switcher">
            <li><a href="#">Sign in</a></li>
            <li><a href="#">New account</a></li>
        </ul>

        <div id="cd-login"> <!-- log in form -->
            <form method="post" action="Caddie" class="cd-form">
                <p class="fieldset">
                    <label class="image-replace cd-email" for="signin-email">E-mail</label>
                    <input class="full-width has-padding has-border" id="signin-email" type="email" name="mail"
                           placeholder="E-mail">
                    <span class="cd-error-message">Error message here!</span>
                </p>

                <p class="fieldset">
                    <label class="image-replace cd-password" for="signin-password">Password</label>
                    <input class="full-width has-padding has-border" id="signin-password" type="password" name="pass"
                           placeholder="Password">
                    <a href="#" class="hide-password">Show</a>
                    <span class="cd-error-message">Error message here!</span>
                </p>

                <p class="fieldset">
                    <input type="checkbox" id="remember-me" checked>
                    <label for="remember-me">Remember me</label>
                </p>

                <p class="fieldset">
                    <input id="type" type="hidden" name="type" value="signin">
                    <input class="full-width" type="submit" value="Login">
                </p>
            </form>

            <p class="cd-form-bottom-message"><a href="#">Forgot your password?</a></p>
            <!-- <a href="#0" class="cd-close-form">Close</a> -->
        </div> <!-- cd-login -->

        <div id="cd-signup"> <!-- sign up form -->
            <form method="post" class="cd-form" action="Caddie">
                <p class="fieldset">
                    <label class="image-replace cd-username" for="signup-username">Username</label>
                    <input class="full-width has-padding has-border" id="signup-username" type="text" name="username"
                           placeholder="Username">
                    <span class="cd-error-message">Error message here!</span>
                </p>

                <p class="fieldset">
                    <label class="image-replace cd-email" for="signup-email">E-mail</label>
                    <input class="full-width has-padding has-border" id="signup-email" type="email" name="mail"
                           placeholder="E-mail">
                    <span class="cd-error-message">Error message here!</span>
                </p>

                <p class="fieldset">
                    <label class="image-replace cd-password" for="signup-password">Password</label>
                    <input class="full-width has-padding has-border" id="signup-password" type="password" name="pass"
                           placeholder="Password">
                    <a href="#" class="hide-password">Show</a>
                    <span class="cd-error-message">Error message here!</span>
                </p>

                <p class="fieldset">
                    <input type="checkbox" id="accept-terms">
                    <label for="accept-terms">I agree to the <a href="#">Terms</a></label>
                </p>

                <p class="fieldset">
                    <input id="typeSignup" name="type" type="hidden" name="type" value="signup">
                    <input class="full-width has-padding" type="submit" value="Create account">
                </p>
            </form>

            <!-- <a href="#0" class="cd-close-form">Close</a> -->
        </div> <!-- cd-signup -->

        <div id="cd-reset-password"> <!-- reset password form -->
            <p class="cd-form-message">Lost your password? Please enter your email address. You will receive a link to
                create a new password.</p>

            <form class="cd-form">
                <p class="fieldset">
                    <label class="image-replace cd-email" for="reset-email">E-mail</label>
                    <input class="full-width has-padding has-border" id="reset-email" type="email" placeholder="E-mail">
                    <span class="cd-error-message">Error message here!</span>
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
<!-- cd-user-modal -->