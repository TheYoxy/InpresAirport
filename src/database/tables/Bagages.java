package database.tables;

public class Bagages {

    private double _poids;
    private boolean _valise;

    public Bagages(){
        _poids = 0.0;
        _valise = true;
    }

    public Bagages(Double pd, boolean val){
        _poids = pd;
        _valise = val;
    }

    public Double get_poids() {
        return _poids;
    }

    public boolean get_type() {
        return _valise;
    }
}
