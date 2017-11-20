package Servlet;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Beans.ConnectionB;
import Beans.ReservationB;
import Tools.Bd;
import Tools.BdType;


@WebServlet(name = "Servlet.CaddieServlet", value = "/Caddie")

public class CaddieServlet extends HttpServlet {
    private List<ReservationB> LReservation;
    private String User = "admin";
    private Bd Sgbd;
    private String numVol;
    private String username = "temp";
    private String time = "";

    public static String getCurrentTimeStamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());//dd/MM/yyyy
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        LReservation = new LinkedList<>();
        try {
            Sgbd = new Bd(BdType.MySql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        ConnectionB connectionB = new ConnectionB();
        String type = request.getParameter("type"); //signin or signup
        if (type != null) {
            if (type.equals("add")) {
                String numVol = request.getParameter("numVol");
                int nbPlaces = Integer.parseInt(request.getParameter("nbrPlaces"));
//                if (numVol == null)
                //TODO REDIRECT
                // /Ajout d'un tuple dans la table reservation
                if (session.getAttribute("reservation") != null)
                    LReservation = (List<ReservationB>) session.getAttribute("reservation");
                else
                    LReservation = new LinkedList<>();

                boolean found = false;
                for (ReservationB rb : LReservation)
                    if (rb.getNumVol().equals(numVol)) {
                        found = true;
                        rb.setNbrPlaces(rb.getNbrPlaces() + nbPlaces);
                    }

                if (!found)
                    try {
                        ResultSet rs = Sgbd.SelectVolReservableNbPlaces(numVol, nbPlaces);
                        if (rs != null)
                            LReservation.add(new ReservationB(numVol, nbPlaces, Bd.ToList(rs)));
                        else {
                            //TODO Info que le nombre de vol sélectionné est supérieur au nombre restant de tickets
                            return;
                        }
                    } catch (SQLException e) {
                        request.setAttribute("Exception", e);
                        request.getRequestDispatcher("/error.jsp").forward(request, response);
                        return;
                    }
                session.setAttribute("reservation", LReservation);

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
            if (type.equals("remove")) {
                numVol = request.getParameter("vol");
                if (session.getAttribute("reservation") != null){
                    LReservation = (List<ReservationB>) session.getAttribute("reservation");
                    for(int i=0; i<LReservation.size() ; i++){
                        if(LReservation.get(i).getNumVol().equals(numVol))
                            LReservation.remove(i);
                    }
                }
                getVols(request,response);
                request.getRequestDispatcher("/caddie.jsp").forward(request, response);
            }
            if (type.equals("get")) {

                getVols(request,response);
                request.getRequestDispatcher("/caddie.jsp").forward(request, response);
                return;
            }
        }
        request.getRequestDispatcher("/").forward(request, response);
    }

    public void getVols(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{

        try {
            request.setAttribute("Vols", Sgbd.Select("VolReservable"));
        } catch (SQLException e) {
            request.setAttribute("Exception", e);
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
}



