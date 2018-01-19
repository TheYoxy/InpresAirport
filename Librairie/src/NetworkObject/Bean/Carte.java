package NetworkObject.Bean;

import java.io.Serializable;

public class Carte implements Serializable {
    private String   numeroCarte;
    private Voyageur voyageur;

    public Carte(String nom, String numeroCarte) {
        this.voyageur = new Voyageur(nom, null, null);
        this.numeroCarte = numeroCarte;
    }

    public Carte(Voyageur v, String numeroCarte) {
        this.voyageur = v;
        this.numeroCarte = numeroCarte;
    }

    public String getNom() {
        return this.voyageur.getNom();
    }

    public String getNumeroCarte() {
        return numeroCarte;
    }

    public Voyageur getVoyageur() {
        return voyageur;
    }

    @Override
    public String toString() {
        return "Carte{" +
                "voyageur='" + voyageur + '\'' +
                ", numeroCarte='" + numeroCarte + '\'' +
                '}';
    }
}
