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

    public int getColumnCount() {
        return Tete.size();
    }

    public void removeColumn(int x) {
        Tete.removeElementAt(x);
        for (Vector<String> v : Champs) v.removeElementAt(x);
    }

    public Vector<String> getTete() {
        return Tete;
    }

    public Vector<Vector<String>> getChamps() {
        return Champs;
    }

    @Override
    public String toString() {
        StringBuilder retour = new StringBuilder();
        retour.append("|");
        for (String s : Tete) {
            retour.append(s).append("|");
        }
        retour.append("\n");
        for (Vector<String> vs : Champs) {
            retour.append("|");
            for (String s : vs) {
                retour.append(s).append("|");
            }
            retour.append("\n");
        }
        return retour.toString();
    }
}
