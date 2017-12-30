package ServeurClientLog.Interfaces;

import java.io.Serializable;
import java.net.Socket;

public interface Requete extends Serializable {
    Runnable createRunnable(Socket client);
}
