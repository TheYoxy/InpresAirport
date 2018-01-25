<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <title>Connexion</title>
</head>
<body>
<form action="${pageContext.request.contextPath}/Login" method="post">
    <label for="username">User: </label>
    <input id="username" type="text" name="username">
    <label for="password">Password: </label>
    <input id="password" type="password" name="password">
    <input type="submit" value="Log">
</form>
</body>
</html>
