package database.tables;

public class Vols {

    private String NumVol;
    private String Destination;
    private String HeureArrivée;
    private String HeureDépart;
    private String HeureArrivéeDestination;
    private String AvionUtilisé;

    public Vols(){
        this.NumVol = "";
        this.Destination = "";
        this.HeureArrivée = "";
        this.HeureDépart = "";
        this.HeureArrivéeDestination = "";
        this.AvionUtilisé = "";
    }

    public Vols(String numvol, String dest, String hArr, String hdep, String hArrDest, String modAvion) {
        this.NumVol = numvol;
        this.Destination = dest;
        this.HeureArrivée = hArr;
        this.HeureDépart = hdep;
        this.HeureArrivéeDestination = hArrDest;
        this.AvionUtilisé = modAvion;
    }

    public String getNumVol() {
        return this.NumVol;
    }
    
    public String getDestination() {
        return this.Destination;
    }

    public String getHeureArrivée() {
        return this.HeureArrivée;
    }

    public String getHeureDépart() {
        return this.HeureDépart;
    }

    public String getHeureArrivéeDestination() {
        return this.HeureArrivéeDestination;
    }

    public String getAvionUtilisé() {
        return this.AvionUtilisé;
    }
}
