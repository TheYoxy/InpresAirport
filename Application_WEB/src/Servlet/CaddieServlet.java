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

        request.getRequestDispatcher("/main.jsp").forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        ConnectionB connectionB = new ConnectionB();
        String type = request.getParameter("type"); //signin or signup
        String numVol;
        String username;
        String qt;
        String time ="";
        //ConnectionB connectionB = new ConnectionB();

        if(type != null){
            if(type.equals("add")){//Ajout d'un tuple dans la table reservation
                username = (String)session.getAttribute("user");
                numVol = request.getParameter("numVol");
                qt = request.getParameter("nbrPlaces");
                try {
                    if (Sgbd.InsertReservation(username, numVol, qt, time)) {
                        connectionB.setResult(ConnectionResult.SUCCES);
                        User = username;
                    } else
                        connectionB.setResult(ConnectionResult.FAIL);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                connectionB.setPlace(Form.SIGNIN);

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
    }


}



