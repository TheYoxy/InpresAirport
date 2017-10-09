package database.tables;

public class Avion {
    private String Modele;
    private boolean vol;

    public String getModele() {
        return Modele;
    }

    public void setModele(String modele) {
        Modele = modele;
    }

    public boolean isVol() {
        return vol;
    }
    public void setVol(boolean vol) {
        this.vol = vol;
    }
}
