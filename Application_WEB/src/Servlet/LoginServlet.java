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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

@WebServlet(name = "Servlet.LoginServlet", value = "/Caddie")

public class LoginServlet extends HttpServlet {
    private String Status;
    private String User = "admin";
    private Bd Sgbd;

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
        try {
            request.setAttribute("Vols", Sgbd.Select("VolReservable"));
        } catch (SQLException e) {
            request.setAttribute("Exception", e);
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String email;
        String pass;
        String username;
        String type = request.getParameter("type"); //signin or signup
        if (type != null) {
            if (type.equals("logout")) {
                Status = "fail";
                session.invalidate();
                doGet(request,response);
                return;
            }
            username = request.getParameter("username");
            pass = request.getParameter("pass");
            email = request.getParameter("mail");
            try {
                switch (type) {
                    case "signin":
                        Status = checkUser(email, pass) ? "success" : "fail";
                        break;
                    case "signup":
                        if (Sgbd.InsertUser(username, pass, email)) {
                            Status = "success";
                            User = username;
                        } else
                            Status = "fail";
                        break;
                }
            } catch (SQLException e) {
                request.setAttribute("Exception", e);
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }
            session.setAttribute("type", Status);
            session.setAttribute("user", User);
            session.setAttribute("mail", request.getParameter("mail"));
            doGet(request, response);
        }
    }

    public boolean checkUser(String mail, String pass) throws SQLException {
        String userbd;
        String passbd;
        String mailbd;
        ResultSet rs = Sgbd.Select("Users");
        ResultSetMetaData rsmf = rs.getMetaData();
        while (rs.next()) {
            userbd = rs.getString(1);
            passbd = rs.getString(2);
            mailbd = rs.getString(5);
            if (mailbd.equals(mail) && passbd.equals(pass)) {
                User = userbd;
                return true;
            }
        }
        return false;
    }
}
