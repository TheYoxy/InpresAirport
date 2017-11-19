package Beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReservationB implements Serializable{

    private String sessionId;
    private String numVol;
    private int nbrPlaces;
    private List<ReservationB> listReser;

    public ReservationB(){
        listReser = new ArrayList<>();
    }

    public ReservationB(String id, String vol, int places){
        sessionId = id;
        numVol = vol;
        nbrPlaces = places;
    }

    public String getNumVol() {
        return numVol;
    }

    public void setNumVol(String num) {
        numVol = num;
    }

    public String getSessionId() { return sessionId; }

    public void setSessionId(String id) { sessionId = id; }

    public int getNbrPlaces() { return nbrPlaces;}

    public void setNbrPlaces(int nbr){ nbrPlaces = nbr;}

    public ReservationB getReservation(int position){
        return listReser.get(position);
    }

    public String getNbrReservation(){
        return Integer.toString(listReser.size());
    }

    public void addReservation(String id, String vol, int places){
        listReser.add(new ReservationB(id, vol, places));
    }
}
