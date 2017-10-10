package database.tables;

public class Avion {
    private int id;
    private String Modele;
    private boolean Vol;

    public Avion(int id, String modele, boolean vol) {

        this.id = id;
        Modele = modele;
        Vol = vol;
    }

    public Avion() {
        Modele = null;
        Vol = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
