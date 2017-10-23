package LUGAP.NetworkObject;

import java.util.Vector;

public class Bagage {
    private final String NumeroBagage;
    private final double Poids;
    private final boolean Valise;
    private final String NumeroBillet;
    private final boolean Reception;
    private final char Charger;
    private final boolean Verifier;
    private final String Remarque;

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

    public double getPoids() {
        return Poids;
    }

    public boolean isValise() {
        return Valise;
    }

    public String getNumeroBillet() {
        return NumeroBillet;
    }

    public boolean isReception() {
        return Reception;
    }

    public boolean isVerifier() {
        return Verifier;
    }

    public char getCharger() {
        return Charger;
    }

    public String getRemarque() {
        return Remarque;
    }
}
