package database.tables;

public class Vols {

    private String _destination;
    private String _heureArrivee;
    private String _heureDepart;
    private String _heureArriveeDest;
    private String _modeleAvion;

    public Vols(){
        _destination = "";
        _heureArrivee = "";
        _heureDepart = "";
        _heureArriveeDest = "";
        _modeleAvion = "";
    }

    public Vols(String dest, String hArr, String hdep, String hArrDest, String modAvion) {
        _destination = dest;
        _heureArrivee = hArr;
        _heureDepart = hdep;
        _heureArriveeDest = hArrDest;
        _modeleAvion = modAvion;
    }

    public String get_Dest() {
        return _destination;
    }

    public String get_HeureArrivee() {
        return _heureArrivee;
    }

    public String get_heureDepart() {
        return _heureDepart;
    }

    public String get_heureArriveeDest() {
        return _heureArriveeDest;
    }

    public String get_modeleAvion() {
        return _modeleAvion;
    }
}
