package database.tables;

public class Billets {

    private String _numBillet;
    private String _numVol;

    public Billets(){
        _numBillet = "";
        _numVol = "";
    }

    public Billets(String numB, String numV) {
        _numBillet = numB;
        _numVol = numV;
    }

    public String getNumBillet() {
        return _numBillet;
    }

    public void setNumBillet(String value) {
        _numBillet = value;
    }

    public String getNumVol() {
        return _numVol;
    }

    public void setNumVol(String value) {
        _numVol = value;
    }

}
