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
    private List<ReservationB> LReservation= null;
    private Bd Sgbd;
    private String numVol;
    private int nbrPlaces;
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
            if (type.equals("add")) {//Ajout d'un tuple dans la table reservation
                nbrPlaces = Integer.parseInt(request.getParameter("nbrPlaces"));
                numVol = request.getParameter("numVol");
                boolean exist = false;
                if (session.getAttribute("reservation") != null){
                    LReservation = (List<ReservationB>) session.getAttribute("reservation");

                    for(int i=0; i<LReservation.size() ; i++) {
                        if (LReservation.get(i).getNumVol().equals(numVol)){
                            nbrPlaces += LReservation.get(i).getNbrPlaces();
                            LReservation.get(i).setNbrPlaces(nbrPlaces );
                            exist = true;
                        }
                    }
                }
                if(!exist || session.getAttribute("reservation") == null)
                    LReservation.add(new ReservationB(session.getId(), request.getParameter("numVol"), Integer.parseInt(request.getParameter("nbrPlaces"))));
                session.setAttribute("reservation", LReservation);

                /****** Decompte du nombre de places pour le vol selectionne ******/
                //decompteplace(nbrPlaces, numVol);

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
            if(type.equals("payment")) {
                try {
                    if (session.getAttribute("reservation") != null) {
                        LReservation = (List<ReservationB>) session.getAttribute("reservation");
                        for (int i = 0; i < LReservation.size(); i++) {
                            Sgbd.InsertAchat((String)session.getAttribute("user"), LReservation.get(i).getNumVol() , Integer.toString(LReservation.get(i).getNbrPlaces()) );
                        }
                        LReservation = null;
                        session.setAttribute("payment", "success");
                    }
                }catch(SQLException e){
                    request.setAttribute("Exception", e);
                    request.getRequestDispatcher("/error.jsp").forward(request, response);
                }
                session.setAttribute("reservation", LReservation);
                request.getRequestDispatcher("/caddie.jsp").forward(request, response);
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



