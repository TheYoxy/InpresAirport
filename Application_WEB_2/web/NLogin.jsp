<%@ page contentType="text/html;charset=UTF-8" %>
<!doctype html>
<html lang="en">
<head>
    <title>Nouvel utilisateur</title>
    <!-- JS -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.15.1/moment.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.7.14/js/bootstrap-datetimepicker.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.3.0/js/bootstrap-datepicker.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-validate/1.17.0/jquery.validate.min.js"></script>
    <!-- CSS -->

    <%--<link rel="stylesheet" href="css/bootstrap.min.css">--%>
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
<div class="container">
    <div class="col-lg-offset-3 col-lg-6">
        <h2 class="text-center">Réservations web</h2>
        <div class="well well-lg">
            <form id="form" action="${pageContext.request.contextPath}/Login" method="post"
                  novalidate="novalidate">
                <input type="hidden" name="Type" value="New">
                <div class="form-group">
                    <label for="username">User: </label>
                    <div class=".col-sm-5 <%if (request.getAttribute("username") != null && Boolean.parseBoolean(request.getAttribute("username").toString())) out.print("has-feedback has-error");%>">
                        <input id="username" type="text" class="form-control" name="username">
                        <%
                            if (request.getAttribute("username") != null && Boolean.parseBoolean(request.getAttribute("username").toString())) {
                                out.println("<span class=\"glyphicon glyphicon-remove form-control-feedback\"></span>");
                                out.println("<em id=\"password-error\" class=\"error help-block\">This username is already taken</em>");
                            }
                        %>
                    </div>
                </div>
                <div class="form-group">
                    <label for="mail">Mail: </label>
                    <div class=".col-sm-5">
                        <input id="mail" type="email" class="form-control" name="mail"
                               value="${param["mail"]}">
                    </div>
                </div>
                <div class="form-group">
                    <label for="password">Password: </label>
                    <div class=".col-sm-5">
                        <input id="password" type="password" class="form-control" name="password">
                    </div>
                </div>
                <div class="form-group">
                    <label for="cpassword">Confirm password: </label>
                    <div class=".col-sm-5">
                        <input id="cpassword" type="password" class="form-control" name="password">
                    </div>
                </div>
                <div class="form-group">
                    <label for="fname">First name: </label>
                    <div class=".col-sm-5">
                        <input id="fname" type="text" class="form-control" name="fname"
                               value="${param["fname"]}">
                    </div>
                </div>
                <div class="form-group">
                    <label for="lname">Last name: </label>
                    <div class=".col-sm-5">
                        <input id="lname" type="text" class="form-control" name="lname"
                               value="${param["lname"]}">
                    </div>
                </div>
                <div class="form-group">
                    <input type="submit" class="btn btn-default btn-block" value="Submit">
                </div>
            </form>
        </div>
    </div>
</div>
<script>
    $(document).ready(function () {
        $("#form").validate({
            rules: {
                fname: "required",
                lname: "required",
                username: {
                    required: true,
                    minlength: 2
                },
                password: {
                    required: true,
                    minlength: 5
                },
                cpassword: {
                    required: true,
                    minlength: 5,
                    equalTo: "#password1"
                },
                mail: {
                    required: true,
                    email: true
                }
            },
            messages: {
                fname: "Please enter your firstname",
                lname: "Please enter your lastname",
                username: {
                    required: "Please enter a username",
                    minlength: "Your username must consist of at least 2 characters"
                },
                password: {
                    required: "Please provide a password",
                    minlength: "Your password must be at least 5 characters long"
                },
                cpassword: {
                    required: "Please provide a password",
                    minlength: "Your password must be at least 5 characters long",
                    equalTo: "Please enter the same password as above"
                },
                mail: "Please enter a valid email address"
            },
            errorElement: "em",
            errorPlacement: function (error, element) {
                // Add the `help-block` class to the error element
                $(element).nextAll("em").remove();
                error.addClass("help-block");

                // Add `has-feedback` class to the parent div.form-group
                // in order to add icons to inputs
                element.closest("div").addClass("has-feedback");

                if (element.prop("type") === "checkbox") {
                    error.insertAfter(element.parent("label"));
                } else {
                    error.insertAfter(element);
                }

                // Add the span element, if doesn't exists, and apply the icon classes to it.
                if (!element.next("span")[0]) {
                    $("<span class='glyphicon glyphicon-remove form-control-feedback'></span>").insertAfter(element);
                }
            },
            success: function (label, element) {
                // Add the span element, if doesn't exists, and apply the icon classes to it.
                $(element).nextAll("span").remove();
                $(element).nextAll("em").remove();

                if (!$(element).next("span")[0]) {
                    $("<span class='glyphicon glyphicon-ok form-control-feedback'></span>").insertAfter($(element));
                }
            },
            highlight: function (element, errorClass, validClass) {
                $(element).closest("div").addClass("has-error").removeClass("has-success");
                $(element).next("span").addClass("glyphicon-remove").removeClass("glyphicon-ok");
            },
            unhighlight: function (element, errorClass, validClass) {
                $(element).closest("div").addClass("has-success").removeClass("has-error");
                $(element).next("span").addClass("glyphicon-ok").removeClass("glyphicon-remove");
            }
        });
    });
</script>
</body>
</html>