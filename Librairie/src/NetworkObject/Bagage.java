package NetworkObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class Bagage {
    private String NumeroBagage;
    private double Poids;
    private boolean Valise;
    private String NumeroBillet;
    private boolean Reception;
    private char Charger;
    private boolean Verifier;
    private String Remarque;

    public Bagage(String numeroBagage, double poids, boolean valise, String numeroBillet, boolean reception, char charger, boolean verifier, String remarque) {
        NumeroBagage = numeroBagage;
        Poids = poids;
        Valise = valise;
        NumeroBillet = numeroBillet;
        Reception = reception;
        Charger = charger;
        Verifier = verifier;
        Remarque = remarque;
    }

    public static List<Bagage> FromTable(Table t) {
        List<Bagage> l = new LinkedList<>();
        for (Vector<String> v : t.getChamps()) l.add(FromVector(v));
        return l;
    }

    public static Bagage FromVector(Vector<String> v) {
        return new Bagage(v.elementAt(0),
                Double.parseDouble(v.elementAt(1)),
                Boolean.valueOf(v.elementAt(2)),
                v.elementAt(3),
                Boolean.valueOf(v.elementAt(4)),
                v.elementAt(5).charAt(0),
                Boolean.valueOf(v.elementAt(6)),
                v.elementAt(7));
    }

    public List<Object> toList() {
        return toList(this);
    }

    public static List<Object> toList(Bagage b) {
        LinkedList<Object> l = new LinkedList<>();
        l.add(b.getNumeroBagage());
        l.add(b.getPoids());
        l.add(b.isValise());
        l.add(b.getNumeroBillet());
        l.add(b.isReception());
        l.add(b.getCharger());
        l.add(b.isVerifier());
        l.add(b.getRemarque());
        return l;
    }

    public Vector<String> toVector() {
        Vector<String> retour = new Vector<>();
        retour.add(NumeroBagage);
        retour.add(String.valueOf(Poids));
        retour.add(String.valueOf(Valise));
        retour.add(NumeroBillet);
        retour.add(String.valueOf(Reception));
        retour.add(String.valueOf(Charger));
        retour.add(String.valueOf(Verifier));
        retour.add(Remarque);
        return retour;
    }

    public String getNumeroBagage() {
        return NumeroBagage;
    }

    public void setNumeroBagage(String numeroBagage) {
        NumeroBagage = numeroBagage;
    }

    public double getPoids() {
        return Poids;
    }

    public void setPoids(double poids) {
        Poids = poids;
    }

    public boolean isValise() {
        return Valise;
    }

    public void setValise(boolean valise) {
        Valise = valise;
    }

    public String getNumeroBillet() {
        return NumeroBillet;
    }

    public void setNumeroBillet(String numeroBillet) {
        NumeroBillet = numeroBillet;
    }

    public boolean isReception() {
        return Reception;
    }

    public void setReception(boolean reception) {
        Reception = reception;
    }

    public boolean isVerifier() {
        return Verifier;
    }

    public void setVerifier(boolean verifier) {
        Verifier = verifier;
    }

    public char getCharger() {
        return Charger;
    }

    public void setCharger(char charger) {
        Charger = charger;
    }

    public boolean isChager() {
        return Charger == 'C';
    }

    public String getRemarque() {
        return Remarque;
    }

    public void setRemarque(String remarque) {
        Remarque = remarque;
    }
}
