package ServeurClientLog.Threads;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import ServeurClientLog.Containers.Containeur;
import ServeurClientLog.Interfaces.Requete;
import Tools.Procedural;

/**
 * Classe qui gère le thread du serveur (Thread maître du pool) , qui va écouter sur un port, et qui va ajouter des tâches à faire pour les
 * threadClient (Thread esclave du poool)
 */
public class ThreadServeur extends Thread {
    private final int Port;
    private final Containeur<Runnable> FileSocket;
    private final ThreadClient[] listChild;
    private ServerSocket SSocket = null;
    private Class<? extends Requete> types[];

    public ThreadServeur(int port, int nb_threads, Class<? extends Requete>... type) {
        this.Port = port;
        this.FileSocket = new Containeur<>();
        this.listChild = new ThreadClient[nb_threads];
        types = type;
        for (int i = 0; i < listChild.length; i++)
            listChild[i] = new ThreadClient(FileSocket, "Thread du pool n°" + String.valueOf(i));
    }

    public ThreadClient[] getListChild() {
        return listChild;
    }

    @Override
    public void run() {
        try {
            SSocket = new ServerSocket(Port);
            System.out.println("Serveur en écoute sur " + Procedural.StringIp(SSocket) + ":" + SSocket.getLocalPort() + "\n");
        } catch (IOException e) {
            System.out.println("Erreur de port d'écoute ! ? [" + e + "]\n");
            return;
        }

        // Démarrage du pool de threads
        for (ThreadClient tc : listChild) tc.start();

        while (!isInterrupted()) {
            try {
                Socket socket = SSocket.accept();
                System.out.println("Connexion de " + Procedural.IpPort(socket) + '\n');
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Requete req = (Requete) ois.readObject();
                int i, typeLength;
                for (i = 0, typeLength = types.length; i < typeLength; i++) {
                    Class c = types[i];
                    if (c.isAssignableFrom(req.getClass())) {
                        FileSocket.add(req.createRunnable(socket));
                        break;
                    }
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
