package NetworkObject.Bean;

import java.io.Serializable;
import java.time.LocalDate;

public class Voyageur implements Serializable {
    private String nom;
    private String prenom;
    private LocalDate naissance;

    private Voyageur() {
        nom = "";
        prenom = "";
        naissance = null;
    }

    public Voyageur(String nom, String prenom, LocalDate naissance) {

        this.nom = nom;
        this.prenom = prenom;
        this.naissance = naissance;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public LocalDate getNaissance() {
        return naissance;
    }

    @Override
    public String toString() {
        return nom + ' ' + prenom;
    }
}
