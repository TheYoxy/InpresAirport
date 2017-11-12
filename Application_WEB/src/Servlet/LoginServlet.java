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
import java.io.PrintWriter;
import java.sql.*;

/**
 * @author Nicolas
 */
@WebServlet(name = "Servlet.LoginServlet", value = "/Caddie")

public class LoginServlet extends HttpServlet {
    int NbrConnexions;
    String Status;
    String User = "admin";
    Bd Sgbd;

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
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
        String email;
        String pass;
        String username = "";

        session.setAttribute("mail", request.getParameter("mail"));
        String type = request.getParameter("type"); //signin or signup

        if (type.equals("signin")) {
            email = (String) session.getAttribute("mail");
            pass = request.getParameter("pass");
            if (checkUser(email, pass)) //TO DO Creer cette fonction
            {
                Status = "success";
            } else {
                out.println("Username or Password incorrect");
                Status = "fail";

            }
        }

        if (type.equals("signup")) {
            email = (String) session.getAttribute("mail");
            pass = request.getParameter("pass");
            username = request.getParameter("username");
            if (createUser(username, pass, email)) {
                Status = "success";
                User = username;
            }
        }

        if (type.equals("logout")) {
            Status = "fail";
            session.invalidate();
        }
        try {
            request.setAttribute("Vols", Sgbd.Select("VolReservable"));
        } catch (SQLException e) {
            request.setAttribute("Exception", e);
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }
        request.getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
//        doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        //req.getSession().setAttribute("message", message);
        req.setAttribute("type", Status);
        req.setAttribute("user", User);
        req.getRequestDispatcher("LoginHeader.jsp").forward(req, response);
    }

    public boolean checkUser(String mail, String pass) {
        ResultSetMetaData rsmf;
        String userbd;
        String passbd;
        String mailbd;
        boolean statusbd = false;

        try {
            ResultSet rs = Sgbd.Select("users");
            rsmf = rs.getMetaData();
            while (rs.next()) {
                userbd = rs.getString(1);
                passbd = rs.getString(2);
                mailbd = rs.getString(5);
                if (mailbd.equals(mail) && passbd.equals(pass)) {
                    statusbd = true;
                    User = userbd;
                }
            }
        } catch (Exception e) {
            this.getServletContext().setAttribute("Exception", e);
            this.getServletContext().getRequestDispatcher("/error.jsp");
            return false;
        }
        return statusbd;
    }

    public boolean createUser(String username, String password, String mail) {
        Connection con = Sgbd.getConnection();
        try {
            Statement Instruction = con.createStatement();
            String query = "insert into users(Username, Password, Mail)" + " values ('" + username + "','" + password + "','" + mail + "')";
            Instruction.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }
}
