package ServeurClientLog.Threads;

import ServeurClientLog.Containers.Containeur;
import ServeurClientLog.Interfaces.Requete;
import Tools.Procedural;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ThreadClient extends Thread {
    private final Containeur<Socket> TachesAExecuter;
    private final String Nom;
    private Socket Client;
    private ObjectInputStream Ois;
    private ObjectOutputStream Oos;
    private boolean Logged;

    public ThreadClient(Containeur<Socket> st, String n) {
        TachesAExecuter = st;
        Nom = n;
        this.setName(Nom);
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                Client = TachesAExecuter.get();
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
                        if (boucle) req.createRunnable(Oos).run();
                        else System.out.println(this.getName() + "> Déconnexion de " + Procedural.IpPort(Client));
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
