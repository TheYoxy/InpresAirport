package Librairie;

import Librairie.Interfaces.Tache;

import java.util.LinkedList;

/**
 * Objet servant de queue pour les tâches que le serveur doit traiter
 */
public class ListeTaches implements Tache {
    private LinkedList<Runnable> listTaches;

    public ListeTaches() {
        listTaches = new LinkedList<>();
    }

    @Override
    public synchronized Runnable getTache() throws InterruptedException {
        System.out.println("getTache avant wait");
        while (!existTache()) wait();
        return listTaches.remove();
    }

    @Override
    public synchronized boolean existTache() {
        return !listTaches.isEmpty();
    }

    @Override
    public synchronized void recordTache(Runnable r) {
        listTaches.addLast(r);
        System.out.println("ListeTache: tache dans la file");
        notify();
    }
}
