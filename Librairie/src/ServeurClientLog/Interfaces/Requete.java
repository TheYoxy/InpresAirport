package ServeurClientLog.Interfaces;

import java.io.ObjectOutputStream;
import java.io.Serializable;

public interface Requete extends Serializable {
    Runnable createRunnable(ObjectOutputStream oosClient);

    boolean isLogin();

    boolean loginSucced();

    boolean isLogout();

    boolean isDisconnect();
}
