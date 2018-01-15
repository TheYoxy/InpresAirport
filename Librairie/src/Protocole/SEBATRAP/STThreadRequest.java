package Protocole.SEBATRAP;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;

import ServeurClientLog.Interfaces.Reponse;
import ServeurClientLog.Interfaces.ServeurRequete;
import ServeurClientLog.Objects.Requete;
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
            boolean verif = false;
            String carte = null;
            double prix = 0;

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
                ReponseST rep = new ReponseST(TypeReponseST.NOT_OK);
                try {
                    RequeteST req = (RequeteST) ois.readObject();
                    HeaderRunnable(req, Procedural.StringIp(client));
                    switch (req.getType()) {
                        case VERIF: {
                            Serializable[] params = req.getParams();
                            carte = (String) params[0];
                            System.out.println(Thread.currentThread().getName() + "> Carte: " + carte);
                            ResultSet rs = bd.SelectAlimCarte(carte);
                            if (rs.next()) {
                                double restant = rs.getDouble(1);
                                prix = (double) params[1];
                                if (prix < restant) {
                                    rep = new ReponseST(TypeReponseST.OK);
                                    verif = true;
                                } else
                                    boucle = false;
                            } else
                                rep = new ReponseST(TypeReponseST.CARD_NOT_FOUND);
                        }
                        break;
                        case PAYEMENT:
                            if (verif) {
                                ResultSet rs = bd.SelectAlimCarte(carte);
                                rs.next();
                                double avant = rs.getDouble("solde");

                                int nb = bd.Payement(carte, prix);

                                rs = bd.SelectAlimCarte(carte);
                                rs.next();
                                double apres = rs.getDouble("solde");
                                bd.commit();

                                System.out.println(Thread.currentThread().getName() + "> " + avant + " -> " + apres);
                                System.out.println(Thread.currentThread().getName() + "> Nombre d'update: " + nb);
                                rep = new ReponseST(TypeReponseST.OK);
                            }
                            boucle = false;
                            break;
                    }
                } catch (Exception e) {
                    if (e.getClass() == EOFException.class) {
                        boucle = false;
                        continue;
                    }
                    e.printStackTrace(System.out);
                } finally {
                    try {
                        Reponse(oos, rep);
                    } catch (IOException e) {
                        e.printStackTrace();
                        boucle = false;
                    }
                }
            }

            try {
                bd.Close(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };
    }

    private void HeaderRunnable(Requete req, String from) {
        System.out.println("====================");
        System.out.println(Thread.currentThread().getName() + "> Traitement d'une requête de " + req.getType().toString() + " de " + from);
        System.out.println(Thread.currentThread().getName() + "> Message reçu: " + req);
    }

    private void Reponse(final ObjectOutputStream outputStream, Reponse rep) throws IOException {
        System.out.println(Thread.currentThread().getName() + "> Réponse: " + rep);
        outputStream.writeObject(rep);
        System.out.println("====================\n");
    }
}
