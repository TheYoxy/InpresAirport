package LUGAP.NetworkObject;

import java.io.Serializable;
import java.util.Vector;

public class Table implements Serializable {
    private final Vector<String> Tete;
    private final Vector<Vector<String>> Champs;

    public Table(Vector<String> tete, Vector<Vector<String>> champs) {
        Tete = tete;
        Champs = champs;
    }

    public Vector<String> getTete() {
        return Tete;
    }

    public Vector<Vector<String>> getChamps() {
        return Champs;
    }
}
