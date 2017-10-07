package Librairie.Network;

import Librairie.Interfaces.Requete;

import java.io.Serializable;
import java.net.Socket;
import java.util.Hashtable;

public class RequeteLUGAP implements Requete, Serializable {
    public static final int REQUEST_1 = 1;
    public static final int REQUEST_2 = 2;
    public static Hashtable tableMail = new Hashtable();
    static {
        tableMail.put("a","a");
    }
    @Override
    public Runnable createRunnable(final Socket s) {
        return null;
    }
}
