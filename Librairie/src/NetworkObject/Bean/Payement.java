package NetworkObject.Bean;

import java.io.Serializable;

public class Payement implements Serializable {
    private Carte carte;
    private double somme;

    public Payement(Carte carte, double somme) {

        this.carte = carte;
        this.somme = somme;
    }

    public Carte getCarte() {
        return carte;
    }

    public double getSomme() {
        return somme;
    }

    @Override
    public String toString() {
        return "payement{" +
                "carte=" + carte +
                ", somme=" + somme +
                '}';
    }
}
