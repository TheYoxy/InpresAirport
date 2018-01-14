package NetworkObject.Bean;

import java.io.Serializable;
import java.util.List;

public class Places implements Serializable {
    private List<String> numPlaces;
    private double prix;

    public Places(List<String> numPlaces, double prix) {
        this.numPlaces = numPlaces;
        this.prix = prix;
    }

    public List<String> getNumPlaces() {
        return numPlaces;
    }

    public double getPrix() {
        return prix;
    }
}
