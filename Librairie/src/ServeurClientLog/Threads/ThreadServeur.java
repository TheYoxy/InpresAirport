package ServeurClientLog.Threads;

import java.io.IOException;
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
    private final Containeur<Socket> FileSocket;
    private final ThreadClient[] listChild;
    private ServerSocket SSocket = null;

    public ThreadServeur(int port, int nb_threads, Class<? extends Requete>... type) {
        this.Port = port;
        this.FileSocket = new Containeur<>();
        this.listChild = new ThreadClient[nb_threads];
        for (int i = 0; i < listChild.length; i++) listChild[i] = new ThreadClient(FileSocket, "Thread du pool n°" + String.valueOf(i), type);
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
                Socket cSocket = SSocket.accept();
                System.out.println("Connexion de " + Procedural.IpPort(cSocket) + '\n');
                FileSocket.add(cSocket);
            } catch (IOException e) {
                System.out.println("Erreur d'accept ! ? [" + e.getMessage() + "]\n");
                return;
            }
        }
    }
}
