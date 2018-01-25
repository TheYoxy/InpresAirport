<%@ page import="java.util.Vector" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<jsp:useBean id="vols" scope="session" type="NetworkObject.Bean.Table"/>
<html>
<head>
    <title>Inpres Airport</title>
    <!-- JS -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.15.1/moment.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.7.14/js/bootstrap-datetimepicker.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.3.0/js/bootstrap-datepicker.js"></script>
    <script src="http://cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.11.1/bootstrap-table.min.js"></script>
    <script src="http://cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.11.1/locale/bootstrap-table-zh-CN.min.js"></script>

    <!-- CSS -->
    <link rel="stylesheet"
          href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <link rel="stylesheet"
          href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css">
    <link rel="stylesheet"
          href="http://cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.11.1/bootstrap-table.min.css">
</head>
<body>
<table data-toggle="table" id="table" class="table table-bordered table-hover">
    <thead>
    <tr>
        <% for (String s : vols.getTete()) {%>
        <th>
            <%=s %>
        </th>
        <% } %>
    </tr>
    </thead>
    <tbody>
    <% for (Vector<String> vs : vols.getChamps()) { %>
    <tr class="clickable-row">
        <% for (String s : vs) { %>
        <td>
            <%=s %>
        </td>
        <% } %>
    </tr>
    <% } %>
    </tbody>
</table>
<script>
    $(function () {
        $('#table').on('dbl-click-row.bs.table', function (row, $element, field) {
            // $.map($element,$.trim);
            $.post("/Main", $element, function (data, status, xhr) {
                if (status === "success")
                    $("html").html(data);
            });
        });
    });
</script>
</body>
</html>
