package ServeurClientLog.Interfaces;

import com.sun.istack.internal.NotNull;

import java.io.Serializable;
import java.net.Socket;

public interface Requete extends Serializable {
    @NotNull
    Runnable createRunnable(Socket s);

    boolean isDisconnect();
}
