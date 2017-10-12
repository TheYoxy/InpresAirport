package Librairie.ServeurClientLog.Threads;

import Librairie.ServeurClientLog.Containers.FileSocket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Classe qui gère le thread du serveur (Thread mâitre du pool) , qui va écouter sur un port, et qui va ajouter des tâches à faire pour les
 * threadClient (Thread esclave du poool)
 */

public class ThreadServeur extends Thread {
    private final static int NB_THREADS = 5;
    private int Port;
    private FileSocket File;
    private ServerSocket SSocket = null;

    public ThreadServeur(int p) {
        Port = p;
        File = new FileSocket();
    }

    @Override
    public void run() {
        try {
            SSocket = new ServerSocket(Port);
        } catch (IOException e) {
            System.err.println("Erreur de port d'écoute ! ? [" + e + "]");
            System.exit(1);
        }
        // Démarrage du pool de threads
        for (int i = 0; i < NB_THREADS; i++) {
            ThreadClient tcl = new ThreadClient(File, "Thread du pool n°" + String.valueOf(i));
            tcl.start();
        }

        while (!isInterrupted()) {
            try {
                Socket cSocket = SSocket.accept();
                System.out.println("Connexion de " + cSocket.getInetAddress().toString() + ":" + cSocket.getPort());
                File.addSocket(cSocket);
            } catch (IOException e) {
                System.err.println("Erreur d'accept ! ? [" + e.getMessage() + "]");
                System.exit(1);
            }
        }
    }
}
