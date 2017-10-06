package database.tables;

public class Billets {

    private String NumBillet;
    private String NumVol;

    public Billets(){
        this.NumBillet = "";
        this.NumVol = "";
    }

    public Billets(String numBillet, String numVol) {
        this.NumBillet = numBillet;
        this.NumVol = numVol;
    }

    public String getNumBillet() {
        return this.NumBillet;
    }

    public void setNumBillet(String value) {
        this.NumBillet = value;
    }

    public String getNumVol() {
        return this.NumVol;
    }

    public void setNumVol(String value) {
        this.NumVol = value;
    }

}
