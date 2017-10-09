package Application_Bagages;

import Librairie.LUGAP.ReponseLUGAP;
import Librairie.LUGAP.RequeteLUGAP;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Application_Bagages {
    //Fonction qui envoie un objet au serveur
    //A modifier
    public static void envoi_message()
    {
        String cu = "Coucou";
        RequeteLUGAP req = new RequeteLUGAP(RequeteLUGAP.req1, "Coucou");
        try {
            Socket cli = new Socket(InetAddress.getLocalHost(), 10000);
            System.out.println("Socket: " + cli.getInetAddress().toString() + ":" + cli.getPort());
            ObjectOutputStream oos = new ObjectOutputStream(cli.getOutputStream());
            oos.writeObject(req);
            System.out.println("Envoi réussi");
            oos.flush();
            ReponseLUGAP r;
            ObjectInputStream ois = new ObjectInputStream(cli.getInputStream());
            System.out.println("Attente réponse");
            r = (ReponseLUGAP) ois.readObject();
            System.out.println("Réponse :" + r.getCode() + " ," + r.getChargeUtile());
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
