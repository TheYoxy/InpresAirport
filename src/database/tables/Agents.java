package database.tables;

public class Agents {

    private String _nom;
    private String _prenom;
    private String _poste;

    public Agents(){
        _nom = "";
        _prenom = "";
        _poste = "";
    }

    public Agents(String nom, String prenom, String poste) {
        _nom = nom;
        _prenom = prenom;
        _poste = poste;
    }

    public String get_nom() {
        return _nom;
    }

    public String get_prenom() { return _prenom; }

    public String get_poste() { return _poste; }
}
