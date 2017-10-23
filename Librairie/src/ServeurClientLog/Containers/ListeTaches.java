package ServeurClientLog.Containers;

import ServeurClientLog.Interfaces.Tache;

import java.util.LinkedList;

/**
 * Objet servant de queue pour les tâches que le serveur doit traiter
 */
public class ListeTaches implements Tache {
    private final LinkedList<Runnable> ListTaches;

    public ListeTaches() {
        ListTaches = new LinkedList<>();
    }

    @Override
    public synchronized Runnable getTache() throws InterruptedException {
        while (!existTache()) wait();
        return ListTaches.remove();
    }

    @Override
    public synchronized boolean existTache() {
        return !ListTaches.isEmpty();
    }

    @Override
    public synchronized void addTache(Runnable r) {
        ListTaches.addLast(r);
        notify();
    }
}
