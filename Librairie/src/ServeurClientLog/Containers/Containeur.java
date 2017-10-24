package ServeurClientLog.Containers;

import java.util.LinkedList;

public class Containeur<T> {
    private LinkedList<T> List;

    public Containeur() {
        List = new LinkedList<>();
    }

    public synchronized T get() throws InterruptedException {
        while (!isEmpty()) wait();
        return List.remove();
    }

    public synchronized boolean isEmpty() {
        return !List.isEmpty();
    }

    public synchronized void add(T r) {
        List.addLast(r);
        notify();
    }
}