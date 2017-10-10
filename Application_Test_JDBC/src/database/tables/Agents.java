package database.tables;

public class Agents {

    private String Nom;
    private String Prenom;
    private String Poste;

    public Agents() {
        this.Nom = "";
        this.Prenom = "";
        this.Poste = "";
    }

    public Agents(String nom, String prenom, String poste) {
        this.Nom = nom;
        this.Prenom = prenom;
        this.Poste = poste;
    }

    public String getNom() {
        return this.Nom;
    }

    public String getPrenom() {
        return this.Prenom;
    }

    public String getPoste() {
        return this.Poste;
    }
}
