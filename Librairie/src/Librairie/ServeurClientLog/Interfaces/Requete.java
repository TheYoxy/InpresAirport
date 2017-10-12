package Librairie.ServeurClientLog.Interfaces;

import com.sun.istack.internal.NotNull;

import java.net.Socket;

public interface Requete {
    @NotNull
    Runnable createRunnable(Socket s);

    boolean isDisconnect();
}
