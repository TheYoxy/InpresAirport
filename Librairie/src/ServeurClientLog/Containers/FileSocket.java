package ServeurClientLog.Containers;

import java.net.Socket;
import java.util.LinkedList;

public class FileSocket {
    private final LinkedList<Socket> ListSocket;

    public FileSocket() {
        ListSocket = new LinkedList<>();
    }

    public synchronized Socket getSocket() throws InterruptedException {
        while (!existTache()) wait();
        return ListSocket.remove();
    }

    public synchronized boolean existTache() {
        return !ListSocket.isEmpty();
    }

    public synchronized void addSocket(Socket r) {
        ListSocket.addLast(r);
        notify();
    }
}
