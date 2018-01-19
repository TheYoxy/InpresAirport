package Protocole.SEBATRAP;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;

import NetworkObject.Bean.Carte;
import ServeurClientLog.Objects.ServeurRequete;
import Tools.Bd.Bd;
import Tools.Bd.BdType;
import Tools.Procedural;

public class STThreadRequest extends ServeurRequete {
    @Override
    public Runnable createRunnable(Socket client) {
        return () -> {
            ObjectInputStream ois;
            ObjectOutputStream oos;
            Bd bd;
            boolean boucle = true;
            boolean verif = false;
            Carte carte = null;
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
                            carte = (Carte) params[0];
                            System.out.println(Thread.currentThread().getName() + "> Vérification de la carte: " + carte);
                            ResultSet rs = bd.selectAlimCarte(carte.getNumeroCarte());
                            if (rs.next()) {
                                System.out.println(Thread.currentThread().getName() + "> Carte trouvée");
                                double restant = rs.getDouble(1);
                                prix = (double) params[1];
                                System.out.println(Thread.currentThread().getName() + "> Solde restant: " + restant);
                                System.out.println(Thread.currentThread().getName() + "> Prix du payement: " + prix);
                                if (prix < restant) {
                                    rep = new ReponseST(TypeReponseST.OK);
                                    System.out.println(Thread.currentThread().getName() + "> Attente d'une confirmation du payement");
                                    verif = true;
                                } else
                                    boucle = false;
                            } else
                                rep = new ReponseST(TypeReponseST.CARD_NOT_FOUND);
                        }
                        break;
                        case PAYEMENT:
                            if (verif) {
                                int nb = bd.payement(carte.getNumeroCarte(), prix);
                                bd.commit();
                                System.out.println(Thread.currentThread().getName() + "> Payement validé pour la carte: " + carte);
                                ResultSet rs = bd.selectLastTransaction();
                                if (!rs.next()) System.exit(-42);
                                String id = rs.getString(1);
                                System.out.println(Thread.currentThread().getName() + "> Id de la transaction: " + id);
                                rep = new ReponseST(TypeReponseST.OK, id);
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
                bd.close(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };
    }
}