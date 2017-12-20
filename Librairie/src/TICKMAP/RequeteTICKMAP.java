package TICKMAP;

import ServeurClientLog.Interfaces.Requete;

import java.io.ObjectOutputStream;

public class RequeteTICKMAP implements Requete {
    @Override
    public Runnable createRunnable(ObjectOutputStream oosClient) {
        return null;
    }

    @Override
    public boolean isLogin() {
        return false;
    }

    @Override
    public boolean loginSucced() {
        return false;
    }

    @Override
    public boolean isLogout() {
        return false;
    }

    @Override
    public boolean isDisconnect() {
        return false;
    }
}
