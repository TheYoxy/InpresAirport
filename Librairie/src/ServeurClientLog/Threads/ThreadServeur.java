package ServeurClientLog.Threads;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import ServeurClientLog.Objects.Containeur;
import ServeurClientLog.Objects.ServeurRequete;
import Tools.Procedural;

/**
 * Classe qui gère le thread du serveur (Thread maître du pool) , qui va écouter sur un port, et qui va ajouter des tâches à faire pour les
 * threadClient (Thread esclave du poool)
 */
public class ThreadServeur extends Thread {
    protected final int port;
    protected final Containeur<Runnable> fileSocket;
    protected final Class<? extends ServeurRequete> types[];
    protected final ThreadClient[] listChild;
    private ServerSocket SSocket = null;

    public ThreadServeur(int port, int nb_threads, Class<? extends ServeurRequete>... types) {
        this.port = port;
        this.fileSocket = new Containeur<>();
        this.listChild = new ThreadClient[nb_threads];
        this.types = types;
        for (int i = 0; i < listChild.length; i++)
            listChild[i] = new ThreadClient(fileSocket, "Thread du pool n°" + String.valueOf(i));
        setName("Thread maitre du pool");
    }

    public ThreadClient[] getListChild() {
        return listChild;
    }

    @Override
    public void run() {
        try {
            SSocket = new ServerSocket(port);
            System.out.println(Thread.currentThread().getName() + "> Thread en écoute sur " + Procedural.StringIp(SSocket) + ":" + SSocket.getLocalPort() + "\n");
        } catch (IOException e) {
            System.out.println(Thread.currentThread().getName() + "> Erreur de port d'écoute ! ? [" + e + "]\n");
            return;
        }

        // Démarrage du pool de threads
        for (ThreadClient tc : listChild) tc.start();

        while (!isInterrupted()) {
            try {
                Socket socket = SSocket.accept();
                System.out.println(Thread.currentThread().getName() + "> Connexion de " + Procedural.IpPort(socket) + '\n');
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                ServeurRequete req = (ServeurRequete) ois.readObject();
                int i, typeLength;

                for (i = 0, typeLength = types.length; i < typeLength; i++) {
                    Class c = types[i];
                    if (c.isAssignableFrom(req.getClass())) {
                        fileSocket.add(req.createRunnable(socket));
                        break;
                    }
                }

                if (i == typeLength) {
                    System.out.println("L'objet envoyé par le client n'est pas accepté par le serveur.");
                    System.out.println("Type: " + req.getClass());
                }
            } catch (IOException e) {
                System.out.println("Erreur d'accept ! ? [" + e.getMessage() + "]\n");
                return;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
