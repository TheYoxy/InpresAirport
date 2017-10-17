package ServeurClientLog.Threads;

import ServeurClientLog.Containers.FileSocket;
import ServeurClientLog.Containers.ListeTaches;
import ServeurClientLog.Interfaces.Requete;
import ServeurClientLog.Interfaces.Tache;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ThreadClient extends Thread {
    private FileSocket TachesAExecuter;
    private Tache Queue;
    private String Nom;
    private Socket Client;
    private ThreadEsclave ThEsclave;

    public ThreadClient(FileSocket st, String n) {
        TachesAExecuter = st;
        Queue = new ListeTaches();
        Nom = n;
        this.setName(Nom);
        ThEsclave = new ThreadEsclave(Queue, Nom + " esclave");
        ThEsclave.start();
    }

    public void run() {
        while (!isInterrupted()) {
            try {
                //Le thread attends le socket
                Client = TachesAExecuter.getSocket();
            } catch (InterruptedException e) {
                System.out.println(this.getName() + "> Interruption : " + e.getMessage() + "\n");
            }

            boolean boucle = true;
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(Client.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
                boucle = false;
            }

            while (boucle) {
                try {
                    Requete req = (Requete) ois.readObject();
                    System.out.println("Ajout d'une requête à la file d'attente\n");
                    boucle = !req.isDisconnect();
                    if (boucle)
                        Queue.addTache(req.createRunnable(Client));
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println(e.getLocalizedMessage());
                }
            }

            try {
                Client.close();
            } catch (IOException e) {
                System.out.println(e.getLocalizedMessage());
            }
        }
    }
}