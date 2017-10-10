package database.tables;

public class Vols {

    private String NumVol;
    private String Destination;
    private String HeureArrivee;
    private String HeureDepart;
    private String HeureArriveeDestination;
    private String AvionUtilise;

    public Vols() {
        this.NumVol = "";
        this.Destination = "";
        this.HeureArrivee = "";
        this.HeureDepart = "";
        this.HeureArriveeDestination = "";
        this.AvionUtilise = "";
    }

    public Vols(String numvol, String dest, String hArr, String hdep, String hArrDest, String modAvion) {
        this.NumVol = numvol;
        this.Destination = dest;
        this.HeureArrivee = hArr;
        this.HeureDepart = hdep;
        this.HeureArriveeDestination = hArrDest;
        this.AvionUtilise = modAvion;
    }

    public String getNumVol() {
        return this.NumVol;
    }

    public String getDestination() {
        return this.Destination;
    }

    public String getHeureArrivee() {
        return this.HeureArrivee;
    }

    public String getHeureDepart() {
        return this.HeureDepart;
    }

    public String getHeureArriveeDestination() {
        return this.HeureArriveeDestination;
    }

    public String getAvionUtilise() {
        return this.AvionUtilise;
    }
}
