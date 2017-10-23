package ServeurClientLog.Threads;

import ServeurClientLog.Interfaces.Tache;

public class ThreadEsclave extends Thread {
    private final String Nom;
    private final Tache File;

    public ThreadEsclave(Tache t, String nom) {
        File = t;
        Nom = nom;
        this.setName(nom);
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                Runnable r = File.getTache();
                System.out.println();
                r.run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
