package Librairie.Threads;

import Librairie.Interfaces.Tache;

public class ThreadClient extends Thread{
    private Tache tachesAExecuter;
    private String nom;
    private Runnable tacheEnCours;
    public ThreadClient(Tache st, String n )
    {
        tachesAExecuter = st;
        nom = n;
        this.setName(nom);
    }
    public void run()
    {
        while (!isInterrupted())
        {
            try
            {
                System.out.println("Tread client avant get");
                tacheEnCours = tachesAExecuter.getTache();
            }
            catch (InterruptedException e)
            {
                System.out.println("Interruption : " + e.getMessage());
            }
            System.out.println("run de tachesencours");
            tacheEnCours.run();
        }
    }
}
