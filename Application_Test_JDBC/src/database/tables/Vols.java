package database.tables;

public class Vols {

    private final String NumVol;
    private final String Destination;
    private final String HeureArrivee;
    private final String HeureDepart;
    private final String HeureArriveeDestination;
    private final int AvionUtilise;

    public Vols() {
        this.NumVol = "";
        this.Destination = "";
        this.HeureArrivee = "";
        this.HeureDepart = "";
        this.HeureArriveeDestination = "";
        this.AvionUtilise = -1;
    }

    public Vols(String numvol, String dest, String hArr, String hdep, String hArrDest, int modAvion) {
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

    public int getAvionUtilise() {
        return this.AvionUtilise;
    }
}
