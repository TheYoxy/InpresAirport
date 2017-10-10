package database.tables;

public class Avion {
    private String Modele;
    private boolean Vol;

    public String getModele() {
        return Modele;
    }

    public void setModele(String modele) {
        Modele = modele;
    }

    public boolean isVol() {
        return Vol;
    }

    public void setVol(boolean vol) {
        this.Vol = vol;
    }
}
