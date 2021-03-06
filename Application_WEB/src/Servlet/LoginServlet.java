package Servlet;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Beans.ConnectionB;
import Enums.ConnectionResult;
import Enums.ErrorField;
import Enums.Form;
import Tools.Bd.Bd;
import Tools.Bd.BdType;

@WebServlet(name = "Servlet.LoginServlet", value = "/Main")

public class LoginServlet extends HttpServlet {
    private Bd Sgbd;
    private String User = null;

    @Override
    public void destroy() {
        super.destroy();
        try {
            Sgbd.close(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            Sgbd = new Bd(BdType.MySql);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (User == null) {
            HttpSession session = request.getSession();
            session.setAttribute("connected", "no");
        }
        try {
            request.setAttribute("Vols", Sgbd.select("VolReservable"));
        } catch (SQLException e) {
            request.setAttribute("Exception", e);
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }
        request.getRequestDispatcher("/main.jsp").forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String type = request.getParameter("type"); //signin or signup
        ConnectionB connectionB = new ConnectionB();
        if (type != null) {
            if (type.equals("logout")) {
                session.invalidate();
                doGet(request, response);
                return;
            }
            String email = request.getParameter("mail");
            String pass = request.getParameter("pass");
            String username = request.getParameter("username");
            try {
                switch (type) {
                    case "signin": {
                        int retour;
                        connectionB.setResult((retour = checkUser(email, pass)) == 0 ? ConnectionResult.SUCCES : ConnectionResult.FAIL);
                        switch (retour) {
                            case 1:
                                connectionB.setField(ErrorField.PASSWORD);
                                connectionB.setErrorMessage("Le mot de passe entré est incorrect");
                                break;
                            case 2:
                                connectionB.setField(ErrorField.EMAIL);
                                connectionB.setErrorMessage("L'adresse mail entrée n'existe pas");
                                break;
                        }
                        connectionB.setPlace(Form.LOGIN);
                        session.setAttribute("connected", "yes");
                    }
                    break;
                    case "signup":
                        if (Sgbd.insertUser(username, pass, email)) {
                            connectionB.setResult(ConnectionResult.SUCCES);
                            User = username;
                        } else
                            connectionB.setResult(ConnectionResult.FAIL);
                        connectionB.setPlace(Form.SIGNIN);
                        session.setAttribute("connected", "yes");
                        break;
                }
            } catch (SQLException e) {
                request.setAttribute("Exception", e);
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }
            if (connectionB.getResult() == ConnectionResult.SUCCES)
                connectionB.setPlace(null);
            session.setAttribute("Result", connectionB);
            session.setAttribute("user", User);
            session.setAttribute("mail", request.getParameter("mail"));
//            doGet(request, response);
            response.sendRedirect(request.getHeader("referer"));
        }
    }

    public int checkUser(String mail, String pass) throws SQLException {
        String userbd;
        String passbd;
        String mailbd;
        ResultSet rs = Sgbd.select("Users");
        ResultSetMetaData rsmf = rs.getMetaData();
        while (rs.next()) {
            userbd = rs.getString(1);
            passbd = rs.getString(2);
            mailbd = rs.getString(5);
            if (mailbd.equals(mail))
                if (passbd.equals(pass)) {
                    User = userbd;
                    return 0;
                } else
                    return 1;
        }
        return 2;
    }
}
