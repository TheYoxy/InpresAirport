package ServeurClientLog.Interfaces;

public interface Tache {

    Runnable getTache() throws InterruptedException;

    boolean existTache();

    void addTache(Runnable r);
}
