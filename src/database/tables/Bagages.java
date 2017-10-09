package database.tables;

public class Bagages {

    private String numeroBagage;
    private double Poids;
    private boolean Valise;

    public Bagages(){
        numeroBagage = "";
        Poids = 0.0;
        Valise = true;
    }

    public Bagages(String numBag, Double pd, boolean val){
        numeroBagage = numBag;
        Poids = pd;
        Valise = val;
    }

    public String getNumBagage(){
        return this.numeroBagage;
    }
    
    public Double getPoids() {
        return this.Poids;
    }

    public String isValise() {
        if(this.Valise)
            return "valise";
        else
            return "bagage";
    }
}
