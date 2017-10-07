package Librairie.Interfaces;

public interface Tache {
    Runnable getTache() throws InterruptedException;
    boolean existTache();
    void recordTache(Runnable r);
}
