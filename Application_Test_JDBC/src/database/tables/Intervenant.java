package database.tables;

public class Intervenant {
    private final String Nom;
    private final String Prenom;
    private final String Status;

    public Intervenant() {
        Nom = "";
        Prenom = "";
        Status = "";
    }

    public Intervenant(String nom, String prenom, String statut) {
        Nom = nom;
        Prenom = prenom;
        Status = statut;
    }

    public String getNom() {
        return Nom;
    }

    public String getPrenom() {
        return Prenom;
    }

    public String getStatus() {
        return Status;
    }
}