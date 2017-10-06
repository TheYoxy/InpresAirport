package database.tables;

public class Bagages {

    private double Poids;
    private boolean Valise;

    public Bagages(){
        Poids = 0.0;
        Valise = true;
    }

    public Bagages(Double pd, boolean val){
        Poids = pd;
        Valise = val;
    }

    public Double getPoids() {
        return this.Poids;
    }

    public boolean isValise() {
        return this.Valise;
    }
}
