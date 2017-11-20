package Beans;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class ReservationB implements Serializable {

    private String NumVol = "";
    private int NbrPlaces = 0;
    private List InfosVol = new LinkedList<>();

    public ReservationB() {
    }

    public ReservationB(String numVol, int nbrPlaces, List infosVol) {
        NumVol = numVol;
        NbrPlaces = nbrPlaces;
        InfosVol = infosVol;
    }

    public List getInfosVol() {
        return InfosVol;
    }

    public void setInfosVol(List infosVol) {
        InfosVol = infosVol;
    }

    public String getNumVol() {
        return NumVol;
    }

    public void setNumVol(String num) {
        NumVol = num;
    }

    public int getNbrPlaces() {
        return NbrPlaces;
    }

    public void setNbrPlaces(int nbr) {
        NbrPlaces = nbr;
    }
}
