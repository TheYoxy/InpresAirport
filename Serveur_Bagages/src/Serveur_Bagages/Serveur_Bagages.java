package Serveur_Bagages;

import Librairie.ListeTaches;
import Serveur_Bagages.Threads.ThreadServeur;

public class Serveur_Bagages {
    public static void main(String[] args) {
        ThreadServeur ts = new ThreadServeur(10000, new ListeTaches());
        ts.run();
        try {
            ts.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
