package NetworkObject.Bean;

import java.io.Serializable;

public class Carte implements Serializable {
    private String nom;
    private String numeroCarte;

    public Carte(String nom, String numeroCarte) {

        this.nom = nom;
        this.numeroCarte = numeroCarte;
    }

    public String getNom() {
        return nom;
    }

    public String getNumeroCarte() {
        return numeroCarte;
    }

    @Override
    public String toString() {
        return "Carte{" +
                "nom='" + nom + '\'' +
                ", numeroCarte='" + numeroCarte + '\'' +
                '}';
    }
}
