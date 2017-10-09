package Serveur_Bagages.Threads;

import Librairie.Interfaces.Requete;
import Librairie.Interfaces.Tache;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadServeur extends Thread {
    private final static int NB_THREADS = 5;
    private int port;
    private Tache tachesAExecuter;
    private ServerSocket SSocket = null;

    public ThreadServeur(int p, Tache st) {
        port = p;
        tachesAExecuter = st;
    }

    @Override
    public void run() {
        try {
            SSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Erreur de port d'écoute ! ? [" + e + "]");
            System.exit(1);
        }
        // Démarrage du pool de threads
        for (int i = 0; i < NB_THREADS; i++) {
            ThreadClient thr = new ThreadClient(tachesAExecuter, "Thread du pool n°" + String.valueOf(i));
            thr.start();
        }

        Socket CSocket = null;
        while (!isInterrupted()) {
            try {
                System.out.println("************ Serveur en attente");
                CSocket = SSocket.accept();
            } catch (IOException e) {
                System.err.println("Erreur d'accept ! ? [" + e.getMessage() + "]");
                System.exit(1);
            }
            ObjectInputStream ois;
            Requete req = null;
            try {
                ois = new ObjectInputStream(CSocket.getInputStream());
                req = (Requete) ois.readObject();
                System.out.println("Requete lue par le serveur, instance de " +
                        req.getClass().getName());
            } catch (ClassNotFoundException e) {
                System.err.println("Erreur de def de classe [" + e.getMessage() + "]");
            } catch (IOException e) {
                System.err.println("Erreur ? [" + e.getMessage() + "]");
            }
            assert req != null;
            Runnable travail = req.createRunnable(CSocket);
            if (travail != null) {
                tachesAExecuter.recordTache(travail);
                System.out.println("Travail mis dans la file");
            } else System.out.println("Pas de mise en file");
        }
    }
}
