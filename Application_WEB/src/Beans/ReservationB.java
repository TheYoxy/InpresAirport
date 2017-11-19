package Beans;

import java.io.Serializable;

public class ReservationB implements Serializable {

    private String SessionId = "";
    private String NumVol = "";
    private int NbrPlaces = 0;

    public ReservationB() {
    }

    public ReservationB(String id, String vol, int places) {
        this();
        SessionId = id;
        NumVol = vol;
        NbrPlaces = places;
    }

    public String getNumVol() {
        return NumVol;
    }

    public void setNumVol(String num) {
        NumVol = num;
    }

    public String getSessionId() {
        return SessionId;
    }

    public void setSessionId(String id) {
        SessionId = id;
    }

    public int getNbrPlaces() {
        return NbrPlaces;
    }

    public void setNbrPlaces(int nbr) {
        NbrPlaces = nbr;
    }
}
