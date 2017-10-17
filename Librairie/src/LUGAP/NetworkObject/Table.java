package LUGAP.NetworkObject;

import java.io.Serializable;
import java.util.LinkedList;

public class Table implements Serializable{
    private LinkedList<String> Tete;
    private LinkedList<LinkedList<String>> Champs;

    public Table(LinkedList<String> tete, LinkedList<LinkedList<String>> champs) {
        Tete = tete;
        Champs = champs;
    }

    public LinkedList<String> getTete() {
        return Tete;
    }

    public LinkedList<LinkedList<String>> getChamps() {
        return Champs;
    }

}
