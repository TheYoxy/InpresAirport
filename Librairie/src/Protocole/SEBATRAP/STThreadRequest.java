package Protocole.SEBATRAP;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;

import ServeurClientLog.Interfaces.Reponse;
import ServeurClientLog.Interfaces.ServeurRequete;
import Tools.Bd.Bd;
import Tools.Bd.BdType;
import Tools.Procedural;

public class STThreadRequest implements ServeurRequete {
    private static final long serialVersionUID = 1235434345L;

    @Override
    public Runnable createRunnable(Socket client) {
        return () -> {
            ObjectInputStream ois;
            ObjectOutputStream oos;
            Bd bd;
            boolean boucle = true;
            try {
                bd = new Bd(BdType.MySql, 5);
                ois = new ObjectInputStream(client.getInputStream());
                oos = new ObjectOutputStream(client.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }

            while (boucle) {
                //                ReponseST rep = new ReponseST();
                try {
                    RequeteST req = (RequeteST) ois.readObject();

                    HeaderRunnable(req.getType().toString(), Procedural.StringIp(client));
                    switch (req.getType()) {
                        case VERIF: {
                            Serializable[] params = req.getParams();
                            String carte = (String) params[0];
                            ResultSet rs = bd.SelectAlimCarte(carte);
                            //                            if (!rs.next())
                            //                                rep = new ReponseST();
                        }
                        break;
                        case PAYEMENT:
                            boucle = false;
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace(System.out);

                }
            }
        };
    }

    private void HeaderRunnable(String type, String from) {
        System.out.println();
        System.out.println(Thread.currentThread().getName() + "> Traitement d'une requête de " + type + " de " + from);
    }

    private void Reponse(final ObjectOutputStream outputStream, Reponse rep) throws IOException {
        System.out.println(Thread.currentThread().getName() + "> Réponse: " + rep);
        outputStream.writeObject(rep);
    }

}
