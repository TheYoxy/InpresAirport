package database.tables;

public class Avion {
    private int Id;
    private String Modele;
    private boolean Vol;

    public Avion(int id, String modele, boolean vol) {

        this.Id = id;
        Modele = modele;
        Vol = vol;
    }

    public Avion() {
        Modele = null;
        Vol = false;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        this.Id = id;
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
