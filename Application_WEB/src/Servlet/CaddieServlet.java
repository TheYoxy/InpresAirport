package Servlet;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Beans.ConnectionB;
import Beans.ReservationB;
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
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@WebServlet(name = "Servlet.CaddieServlet", value = "/Caddie")

public class CaddieServlet extends HttpServlet {
    private ReservationB reservation;
    private String User = "admin";
    private Bd Sgbd;
    private String numVol;
    private String username = "temp";
    private String time ="";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        reservation = new ReservationB();
        try {
            Sgbd = new Bd(BdType.MySql);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            //session = request.getSession();
            request.setAttribute("Vols", Sgbd.Select("VolReservable"));

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
        ConnectionB connectionB = new ConnectionB();
        String type = request.getParameter("type"); //signin or signup

        if(type != null){
            if(type.equals("add")){//Ajout d'un tuple dans la table reservation
                //username = (String)session.getAttribute("user");
                if(session.getAttribute("reservation") != null ){
                    reservation = (ReservationB)session.getAttribute("reservation");
                }
                reservation.addReservation(session.getId(), request.getParameter("numVol"), Integer.parseInt(request.getParameter("nbrPlaces")));
                session.setAttribute("reservation", reservation);

                /*try {;
                    if (Sgbd.InsertReservation(username, numVol, qt, getCurrentTimeStamp() )) {
                        connectionB.setResult(ConnectionResult.SUCCES);
                        User = username;
                    } else
                        connectionB.setResult(ConnectionResult.FAIL);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                connectionB.setPlace(Form.SIGNIN);*/

            }
            if(type.equals("remove")){

            }
            if(type.equals("get")){
                ResultSet rs;
                try {
                    //session = request.getSession();
                    request.setAttribute("Vols", Sgbd.Select("VolReservable"));
                    rs = Sgbd.Select("VolReservable");
                } catch (SQLException e) {
                    request.setAttribute("Exception", e);
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                    return;
                }
                request.getRequestDispatcher("/caddie.jsp").forward(request, response);
            }

        }

        doGet(request, response);
    }
    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }


}



