package Servlet;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Beans.ConnectionB;
import Enums.ConnectionResult;
import Enums.ErrorField;
import Enums.Form;
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

@WebServlet(name = "Servlet.LoginServlet", value = "/Main")

public class CaddieServlet extends HttpServlet {
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
        HttpSession session = request.getSession();
        String type = request.getParameter("type"); //signin or signup
        //ConnectionB connectionB = new ConnectionB();

        if(type != null){
            if(type.equals("add")){

            }
            if(type.equals("remove")){

            }
            if(type.equals("get")){
                try {
                    request.setAttribute("Billets", Sgbd.Select("VolReservable"));
                } catch (SQLException e) {
                    request.setAttribute("Exception", e);
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                    return;
                }
            }

        }
        request.getRequestDispatcher("/main.jsp").forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }


}



