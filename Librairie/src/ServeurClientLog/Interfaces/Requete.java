package ServeurClientLog.Interfaces;

import com.sun.istack.internal.NotNull;

import java.io.ObjectOutputStream;
import java.io.Serializable;

public interface Requete extends Serializable {
    @NotNull
    Runnable createRunnable(ObjectOutputStream oosClient);

    boolean isDisconnect();
}
