package Servlet;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Tools.Bd;
import Tools.BdType;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.*;

/**
 * @author Nicolas
 */
@WebServlet(name = "Servlet.LoginServlet", value = "/Caddie")

public class LoginServlet extends HttpServlet {
    private static int NbrConnexions;
    private String Status;
    private String User = "admin";
    private Bd Sgbd;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        NbrConnexions = 0;
        try {
            Sgbd = new Bd(BdType.MySql);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        synchronized (this) {
            NbrConnexions++;
        }
        HttpSession session = request.getSession();
        try {
            request.setAttribute("Vols", Sgbd.Select("VolReservable"));
        } catch (SQLException e) {
            request.setAttribute("Exception", e);
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }
        request.getRequestDispatcher("/index.jsp").forward(request, response);
//      doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String email;
        String pass;
        String username;
        String type = request.getParameter("type"); //signin or signup

        if (type != null) {
            switch (type) {
                case "signin":
                    email = (String) session.getAttribute("mail");
                    pass = request.getParameter("pass");
                    try {
                        Status = checkUser(email,pass) ? "success" : "fail";
                    } catch (SQLException e) {
                        request.setAttribute("Exception", e);
                        request.getRequestDispatcher("/error.jsp").forward(request, response);
                        return;
                    }
                    break;
                case "signup":
                    email = (String) session.getAttribute("mail");
                    pass = request.getParameter("pass");
                    username = request.getParameter("username");
                    try {
                        if (createUser(username, pass, email)) {
                            Status = "success";
                            User = username;
                        }
                    } catch (SQLException e) {
                        request.setAttribute("Exception", e);
                        request.getRequestDispatcher("/error.jsp").forward(request, response);
                        return;
                    }
                    break;
                case "logout":
                    Status = "fail";
                    session.invalidate();
                    break;
            }
        }
        session.setAttribute("type", Status);
        session.setAttribute("user", User);
        session.setAttribute("mail", request.getParameter("mail"));
    }

    public boolean checkUser(String mail, String pass) throws SQLException {
        String userbd;
        String passbd;
        String mailbd;
        boolean statusbd = false;

        ResultSet rs = Sgbd.Select("users");
        ResultSetMetaData rsmf = rs.getMetaData();
        while (rs.next()) {
            userbd = rs.getString(1);
            passbd = rs.getString(2);
            mailbd = rs.getString(5);
            if (mailbd.equals(mail) && passbd.equals(pass)) {
                statusbd = true;
                User = userbd;
            }
        }
        return statusbd;
    }

    public boolean createUser(String username, String password, String mail) throws SQLException {
        Connection con = Sgbd.getConnection();
        Statement instruction = con.createStatement();
        String query = "insert into users(Username, Password, Mail)" + " values ('" + username + "','" + password + "','" + mail + "')";
        instruction.executeUpdate(query);
        return true;
    }
}
