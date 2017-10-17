package ServeurClientLog.Threads;

import ServeurClientLog.Containers.FileSocket;
import Tools.Procedural;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Classe qui gère le thread du serveur (Thread mâitre du pool) , qui va écouter sur un port, et qui va ajouter des tâches à faire pour les
 * threadClient (Thread esclave du poool)
 */

public class ThreadServeur extends Thread {
    private final int NbThreads;
    private int Port;
    private FileSocket File;
    private ServerSocket SSocket = null;

    public ThreadServeur(int port, int nb_threads) {
        this.Port = port;
        this.File = new FileSocket();
        this.NbThreads = nb_threads;
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
        for (int i = 0; i < NbThreads; i++) {
            ThreadClient tcl = new ThreadClient(File, "Thread du pool n°" + String.valueOf(i));
            tcl.start();
        }

        while (!isInterrupted()) {
            try {
                Socket cSocket = SSocket.accept();
                System.out.println("Connexion de " + cSocket.getInetAddress().toString() + ":" + cSocket.getPort() + '\n');
                File.addSocket(cSocket);
            } catch (IOException e) {
                System.out.println("Erreur d'accept ! ? [" + e.getMessage() + "]\n");
                return;
            }
        }
    }
}