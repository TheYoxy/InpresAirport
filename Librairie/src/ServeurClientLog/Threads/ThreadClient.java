package ServeurClientLog.Threads;

import ServeurClientLog.Containers.Containeur;
import ServeurClientLog.Interfaces.Events.StateChanged;
import ServeurClientLog.Interfaces.Requete;
import Tools.Procedural;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class ThreadClient extends Thread {
    private final Containeur<Socket> TachesAExecuter;
    private Socket Client;
    private ObjectInputStream Ois;
    private ObjectOutputStream Oos;
    private boolean Logged;
    private List<StateChanged> stateChangedList;
    private Class<? extends Requete> type;

    public ThreadClient(Containeur<Socket> st, String n, Class<? extends Requete> type) {
        TachesAExecuter = st;
        this.setName(n);
        stateChangedList = new LinkedList<>();
        this.type = type;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                Client = TachesAExecuter.get();
                fireState(true);
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
                    if (type.isAssignableFrom(req.getClass())) {
                        if (!Logged) {
                            if (req.isLogin()) req.createRunnable(Oos).run();
                            Logged = req.loginSucced();
                        } else {
                            boucle = !req.isDisconnect();
                            if (boucle) req.createRunnable(Oos).run();
                        }
                    } else {
                        System.out.println("Message reçu de type invalide");
                        System.out.println(req.getClass().toString() + " != " + type.toString());
                    }
                } catch (IOException | ClassNotFoundException e) {
                    if (e.getMessage() == null) boucle = false;
                    if (e.getMessage() != null && e.getMessage().contains("reset")) boucle = false;
                    if (e.getMessage() != null) System.out.println(this.getName() + "> " + e.getMessage());
                }
                if (!boucle) System.out.println(this.getName() + "> Déconnexion de " + Procedural.IpPort(Client));
            }
            try {
                Client.close();
                fireState(false);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } finally {
                fireState(false);
            }
        }
    }

    private void fireState(boolean state) {
        for (StateChanged sc : stateChangedList) sc.stateChanged(state);
    }

    public void addStateChangedListener(StateChanged sc) {
        stateChangedList.add(sc);
    }

    public void removeStateChangedListener(StateChanged sc) {
        stateChangedList.remove(sc);
    }
}
