package ServeurClientLog.Threads;

import ServeurClientLog.Interfaces.Tache;

public class ThreadEsclave extends Thread {
    private String Nom;
    private Tache File;

    public ThreadEsclave(Tache t, String nom) {
        File = t;
        Nom = nom;
        this.setName(nom);
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                File.getTache().run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
