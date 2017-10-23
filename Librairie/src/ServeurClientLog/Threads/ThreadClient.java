package ServeurClientLog.Threads;

import ServeurClientLog.Containers.FileSocket;
import ServeurClientLog.Containers.ListeTaches;
import ServeurClientLog.Interfaces.Requete;
import ServeurClientLog.Interfaces.Tache;
import Tools.Procedural;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ThreadClient extends Thread {
    private final FileSocket TachesAExecuter;
    private final Tache Queue;
    private final String Nom;
    private Socket Client;
    private ObjectInputStream Ois;
    private ObjectOutputStream Oos;
    private final ThreadEsclave ThEsclave;
    private boolean Logged;

    public ThreadClient(FileSocket st, String n) {
        TachesAExecuter = st;
        Queue = new ListeTaches();
        Nom = n;
        this.setName(Nom);
        ThEsclave = new ThreadEsclave(Queue, Nom + " esclave");
        ThEsclave.start();
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
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
                    if (!Logged) {
                        if (req.isLogin()) req.createRunnable(Oos).run();
                        Logged = req.loginSucced();
                    } else {
                        boucle = !req.isDisconnect();
                        if (boucle) {
                            System.out.println(this.getName() + "> Ajout d'une requête à la file d'attente");
                            Queue.addTache(req.createRunnable(Oos));
                        } else
                            System.out.println(this.getName() + "> Déconnexion de " + Procedural.IpPort(Client));
                    }
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
