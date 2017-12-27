package ServeurClientLog.Interfaces;

import java.io.OutputStream;
import java.io.Serializable;

public interface Requete extends Serializable {
    Runnable createRunnable(OutputStream oosClient);

    boolean isLogin();

    boolean loginSucced();

    boolean isLogout();

    boolean isDisconnect();
}
