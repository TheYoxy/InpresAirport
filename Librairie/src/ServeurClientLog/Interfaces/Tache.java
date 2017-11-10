package ServeurClientLog.Interfaces;

public interface Tache {

    Runnable getTache();

    boolean existTache();

    void addTache(Runnable r);
}
