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
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

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
        PrintWriter out = response.getWriter();
        String email;
        String pass;
        String username = "";
        /*out.println("<HTML><HEAD><TITLE>Servlet.LoginServlet</TITLE></HEAD><BODY>");
        out.println("<H1>Nouveau client</H1>");
        out.println("<p>nom :  "+ request.getParameter("mail") + " Pass : " + request.getParameter("pass") + "</p>");*/

        String type = request.getParameter("type"); //signin or signup

        if (type != null) {
            switch (type) {
                case "signin":
                    email = request.getParameter("mail");
                    pass = request.getParameter("pass");
                    if (checkUser(email, pass)) //TO DO Creer cette fonction
                    {
                        Status = "success";
                    } else {
                        out.println("Username or Password incorrect");
                        Status = "fail";
                    }
                    break;
                case "signup":
                    email = request.getParameter("mail");
                    pass = request.getParameter("pass");
                    username = request.getParameter("username");
                    //Bd.CreateUser();
                    break;
                case "logout":
                    Status = "fail";
                    break;
            }
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
        /*StringBuilder sb = new StringBuilder();
        ResultSetMetaData rsmf;
        try {
            ResultSet rs = Sgbd.Select("login");
            rsmf = rs.getMetaData();
            while (rs.next()) {
                for (int i = 1; i <= rsmf.getColumnCount(); i++) {//username, password
                    sb.append(rs.getObject(i)).append("|");
                }
            }
        }catch(Exception e)
        {

        }*/
        //Bd bd = new Bd(MySql, "", "1234", "nico");
        return true;
    }
}
