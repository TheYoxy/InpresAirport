package ServeurClientLog.Interfaces;

import com.sun.istack.internal.NotNull;

public interface Tache {
    Runnable getTache() throws InterruptedException;
    boolean existTache();

    void addTache(@NotNull Runnable r);
}
