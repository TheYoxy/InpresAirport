package database.tables;

public class Intervenant {
    private String _nom;
    private String _prenom;
    private String _statut;

    public Intervenant(){
        _nom = "";
        _prenom = "";
        _statut = "";
    }

    public Intervenant(String nom, String prenom, String statut) {
        _nom = nom;
        _prenom = prenom;
        _statut = statut;
    }

    public String get_nom() {
        return _nom;
    }

    public String get_prenom() { return _prenom; }

    public String get_statut() { return _statut; }
}