package database.tables;

public class Bagages {

    private String NumeroBagages;
    private double Poids;
    private boolean Valise;

    public Bagages() {
        NumeroBagages = "";
        Poids = 0.0;
        Valise = true;
    }

    public Bagages(String numBag, Double pd, boolean val) {
        NumeroBagages = numBag;
        Poids = pd;
        Valise = val;
    }

    public String getNumBagage() {
        return this.NumeroBagages;
    }

    public Double getPoids() {
        return this.Poids;
    }

    public String isValise() {
        if (this.Valise)
            return "valise";
        else
            return "bagage";
    }
}
