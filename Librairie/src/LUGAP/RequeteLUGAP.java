package LUGAP;

import ServeurClientLog.Interfaces.Requete;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class RequeteLUGAP implements Requete, Serializable {
    private TypeRequeteLUGAP Type;
    private String ChargeUtile;
    private Socket Socket;

    public RequeteLUGAP(TypeRequeteLUGAP t, String chu) {
        this.Type = t;
        this.ChargeUtile = chu;
    }

    public String getChargeUtile() {
        return ChargeUtile;
    }

    @Override
    public Runnable createRunnable(final Socket s) {
        Runnable retour = null;
        switch (this.Type) {
            case req1:
                retour = () -> {
                    System.out.println("Envoi à " + s.getInetAddress().toString() + ":" + s.getPort());
                    ReponseLUGAP repo = new ReponseLUGAP(TypeReponseLUGAP.OK, getChargeUtile());
                    ObjectOutputStream oos;
                    try {
                        oos = new ObjectOutputStream(s.getOutputStream());
                        oos.writeObject(repo);
                        oos.flush();
                        System.out.println("Message envoyé");
                    } catch (IOException e) {
                        System.err.println("Erreur lors de l'envoi d'un message: " + e.getMessage());
                    }
                };
                break;
            case Login:
                break;
            case Logout:
                break;
            case Disconnect:
                break;
        }
        return retour;
    }

    @Override
    public boolean isDisconnect() {
        return this.Type == TypeRequeteLUGAP.Disconnect;
    }
}
