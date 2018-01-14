package NetworkObject.Bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class Vol implements Serializable {
    private String NumeroVol;
    private String Destination;
    private Timestamp HeureDepart;
    private Timestamp HeureArrivee;
    private Timestamp HeureArriveeDestination;
    private int IdAvion;

    private Vol() {
        NumeroVol = "";
        Destination = "";
        HeureDepart = null;
        HeureArrivee = null;
        HeureArriveeDestination = null;
        IdAvion = -1;
    }

    private Vol(String numeroVol, String destination, Timestamp heureDepart, Timestamp heureArrivee, Timestamp heureArriveeDestination, int idAvion) {
        NumeroVol = numeroVol;
        Destination = destination;
        HeureDepart = heureDepart;
        HeureArrivee = heureArrivee;
        HeureArriveeDestination = heureArriveeDestination;
        IdAvion = idAvion;
    }

    public static List<Vol> fromTableList(Table t) throws ParseException {
        List<Vol> l = new LinkedList<>();
        for (Vector<String> v : t.getChamps()) l.add(fromVector(v));
        return l;
    }

    public static Vol fromVector(Vector<String> t) {
        Vol v;
        if (t.size() == 6) {
            v = new Vol(t.get(0),
                    t.get(1),
                    Timestamp.valueOf(t.get(2)),
                    Timestamp.valueOf(t.get(3)),
                    Timestamp.valueOf(t.get(4)),
                    Integer.parseInt(t.get(5)));
        } else
            throw new IllegalArgumentException("Size of vector is incorrect. (Requires 6)");
        return v;
    }

    public static Vol[] fromTableTable(Table t) throws ParseException {
        ArrayList<Vol> al = new ArrayList<>();
        for (Vector<String> v : t.getChamps()) al.add(fromVector(v));
        return al.toArray(new Vol[al.size()]);
    }

    public String getNumeroVol() {
        return NumeroVol;
    }

    public String getDestination() {
        return Destination;
    }

    public Date getHeureDepart() {
        return HeureDepart;
    }

    public Date getHeureArrivee() {
        return HeureArrivee;
    }

    public Date getHeureArriveeDestination() {
        return HeureArriveeDestination;
    }

    public int getIdAvion() {
        return IdAvion;
    }

    @Override
    public String toString() {
        return "Vol: " + NumeroVol + " (" + Destination + ")";
    }
}
