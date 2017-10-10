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

    public void setNom(String nom) {
        Nom = nom;
    }

    public String getPrenom() {
        return this.Prenom;
    }

    public void setPrenom(String prenom) {
        Prenom = prenom;
    }

    public String getPoste() {
        return this.Poste;
    }

    public void setPoste(String poste) {
        Poste = poste;
    }
}
