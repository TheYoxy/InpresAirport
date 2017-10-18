package ServeurClientLog.Threads;

import ServeurClientLog.Containers.FileSocket;
import ServeurClientLog.Containers.ListeTaches;
import ServeurClientLog.Interfaces.Requete;
import ServeurClientLog.Interfaces.Tache;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ThreadClient extends Thread {
    private FileSocket TachesAExecuter;
    private Tache Queue;
    private String Nom;
    private Socket Client;
    private ObjectInputStream Ois;
    private ObjectOutputStream Oos;
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
                System.out.println(this.getName() + "> Interruption : " + e.getMessage());
            }
            boolean boucle = true;
            try {
                Ois = new ObjectInputStream(Client.getInputStream());
                Oos = new ObjectOutputStream(Client.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
                boucle = false;
            }

            while (boucle) {
                try {
                    Requete req = (Requete) Ois.readObject();
                    System.out.println("Ajout d'une requête à la file d'attente");
                    boucle = !req.isDisconnect();
                    if (boucle)
                        Queue.addTache(req.createRunnable(Oos));
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println(this.getName() + "> " + e.getMessage());
                }
            }

            try {
                Client.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
