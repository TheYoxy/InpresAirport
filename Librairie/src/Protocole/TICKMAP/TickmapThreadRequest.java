package Protocole.TICKMAP;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.Mac;

import NetworkObject.Bean.Login;
import NetworkObject.Bean.MACMessage;
import NetworkObject.Bean.Places;
import NetworkObject.Bean.Voyageur;
import NetworkObject.CryptedPackage;
import ServeurClientLog.Objects.ServeurRequete;
import Tools.AESCryptedSocket;
import Tools.Bd.Bd;
import Tools.Bd.BdType;
import Tools.Crypto.Digest.DigestCalculator;
import Tools.Crypto.FonctionsCrypto;
import Tools.Ids;
import Tools.Procedural;
import javafx.util.Pair;

public class TickmapThreadRequest extends ServeurRequete {
    private static final String keyname  = "appbillets";
    private static final String keystore = "Serveur_Billets.pkcs12";
    private static final String password = "azerty";
    private static final Cipher cipher   = FonctionsCrypto.loadPrivateKeyNoError(keystore, password, keyname);

    @Override
    public Runnable createRunnable(Socket client) {
        return () -> {
            int challenge = 0;
            // Machines a etat
            boolean log = false;
            // Fin machine à état

            Bd                 bd            = null;
            boolean            boucle        = true;
            AESCryptedSocket   cryptedSocket = null;
            Mac                hmac          = null;
            ObjectInputStream  ois;
            ObjectOutputStream oos;

            String     vol           = null;
            String[]   billets       = null;
            Voyageur[] listVoyageurs = null;
            Integer[]  places        = null;
            try {
                ois = new ObjectInputStream(client.getInputStream());
                oos = new ObjectOutputStream(client.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            while (boucle) {
                try {
                    RequeteTICKMAP req = (RequeteTICKMAP) ois.readObject();
                    ReponseTICKMAP rep;
                    HeaderRunnable(req, Procedural.StringIp(client));
                    switch (req.getType()) {
                        case TryConnect:
                            challenge = new Random().nextInt();
                            rep = new ReponseTICKMAP(TypeReponseTICKMAP.OK, challenge);
                            System.out.println(Thread.currentThread().getName() + "> Digest salé généré: " + challenge);
                            Reponse(oos, rep);
                            break;
                        case Login:
                            try {
                                bd = new Bd(BdType.MySql, 5);
                                ResultSet         rs   = bd.select("Login");
                                ResultSetMetaData rsmd = rs.getMetaData();
                                int               user = -1, password = -1;
                                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                                    if (rsmd.getColumnName(i).equals("Username")) {
                                        user = i;
                                    }
                                    else if (rsmd.getColumnName(i).equals("Password")) {
                                        password = i;
                                    }
                                }
                                if (Procedural.test(user, password)) return;
                                rep = new ReponseTICKMAP(TypeReponseTICKMAP.UNKNOWN_LOGIN);
                                while (rs.next()) {
                                    if (rs.getString(user).equals(((Login) req.getParam()).getUser())) {
                                        byte envoye[] = ((Login) req.getParam()).getPassword();
                                        byte pass[]   = DigestCalculator.hashPassword(rs.getString(password), challenge);

                                        System.out.println(Thread.currentThread().getName() + "> Utilisateur trouvé");
                                        System.out.println("-------------------------------------------------------------------");
                                        System.out.println(Thread.currentThread().getName() + "> Hash en string: ");
                                        System.out.println(Thread.currentThread().getName() + "> Hash envoyé:           " + new String(envoye));
                                        System.out.println(Thread.currentThread().getName() + "> Hash de l'utilisateur: " + new String(pass));
                                        System.out.println("-------------------------------------------------------------------");
                                        System.out.println(Thread.currentThread().getName() + "> Hash en tableau: ");
                                        System.out.println(Thread.currentThread().getName() + "> Hash envoyé:           " + Arrays.toString(envoye));
                                        System.out.println(Thread.currentThread().getName() + "> Hash de l'utilisateur: " + Arrays.toString(pass));
                                        System.out.println("-------------------------------------------------------------------");

                                        if (MessageDigest.isEqual(pass, ((Login) req.getParam()).getPassword())) {
                                            rep = new ReponseTICKMAP(TypeReponseTICKMAP.OK, bd.selectLogUser(rs.getString(user)));
                                            System.out.println(Thread.currentThread().getName() + "> Mot de passe correct");
                                            break;
                                        }
                                        else {
                                            rep = new ReponseTICKMAP(TypeReponseTICKMAP.BAD_PASSWORD);
                                            System.out.println(Thread.currentThread().getName() + "> Mot de passe incorrect");
                                            break;
                                        }
                                    }
                                }
                            } catch (SQLException e) {
                                System.out.println(Thread.currentThread().getName() + "> SQLException: " + e.getMessage());
                                rep = new ReponseTICKMAP(TypeReponseTICKMAP.NOT_OK);
                            } catch (IOException e) {
                                System.out.println(Thread.currentThread().getName() + "> IOException: " + e.getMessage());
                                rep = new ReponseTICKMAP(TypeReponseTICKMAP.NOT_OK);
                            }

                            if (rep.getCode() == TypeReponseTICKMAP.UNKNOWN_LOGIN) {
                                System.out.println(Thread.currentThread().getName() + "> Utilisateur introuvable");
                            }
                            Reponse(oos, rep);
                            if (rep.getCode() == TypeReponseTICKMAP.OK) {
                                DataInputStream       dis  = new DataInputStream(new BufferedInputStream(client.getInputStream()));
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                //Lecture de mon objet crypté
                                do {
                                    baos.write(dis.readByte());
                                } while (dis.available() > 0);
                                System.out.println(Thread.currentThread().getName() + "> Récupération des clefs");
                                //Transformation de mon objet

                                Cipher               cipher  = FonctionsCrypto.loadPrivateKey(keystore, password, keyname);
                                ByteArrayInputStream bais    = new ByteArrayInputStream(cipher.doFinal(baos.toByteArray()));
                                ObjectInputStream    tempois = new ObjectInputStream(bais);
                                System.out.println(Thread.currentThread().getName() + "> Décryptage des clefs");

                                CryptedPackage cryptedPackage = (CryptedPackage) tempois.readObject();
                                System.out.println(Thread.currentThread().getName() + "> Clefs reçues");
                                System.out.print(Thread.currentThread().getName() + "> Génération du HMAC: ");

                                cryptedSocket = new AESCryptedSocket(client, cryptedPackage.getParams());

                                hmac = Mac.getInstance("HMAC-SHA1", "BC");
                                hmac.init(cryptedPackage.getKey());
                                System.out.println("réussie");
                                rep = new ReponseTICKMAP(TypeReponseTICKMAP.OK, Bd.toTable(bd.selectWeekVols()));
                                Reponse(oos, rep);
                                log = true;

                            }
                            break;
                        case Ajout_Voyageurs: {
                            if (log) {
                                rep = new ReponseTICKMAP(TypeReponseTICKMAP.NOT_OK);
                                Places p = null;
                                try {
                                    List list = (List) cryptedSocket.readObject();
                                    vol = list.get(0).toString();
                                    ResultSet rs = bd.selectVolReservable(vol);
                                    listVoyageurs = (Voyageur[]) list.get(1);
                                    int count = listVoyageurs.length;
                                    if (rs.next()) {
                                        int nb = rs.getInt("PlacesDisponible");
                                        if (nb < count)
                                            rep = new ReponseTICKMAP(TypeReponseTICKMAP.FULL, count);
                                        else {
                                            rep = new ReponseTICKMAP(TypeReponseTICKMAP.OK);

                                            //Ajout des voyageurs dans la base
                                            for (Voyageur v : listVoyageurs) {
                                                bd.ajoutVoyageur(v);
                                            }

                                            bd.suppPlacesReservables(vol, count);
                                            ResultSet rss = bd.selectLieu(vol);
                                            rss.next();
                                            String lieu = rss.getString(1);

                                            double unit = rs.getDouble("Prix");
                                            System.out.println(Thread.currentThread().getName() + "> Prix unitaire: " + unit);
                                            System.out.println(Thread.currentThread().getName() + "> Nombre de places: " + count);
                                            double prix = count * unit;
                                            System.out.println(Thread.currentThread().getName() + "> Prix calculé: " + prix);
                                            Pair<List<String>, List<Integer>> pair        = Ids.genIdBillets(bd.selectBillets(vol), lieu, vol, count);
                                            List<String>                      listBillets = pair.getKey();
                                            List<Integer>                     listPlaces  = pair.getValue();
                                            billets = listBillets.toArray(new String[listBillets.size()]);
                                            places = listPlaces.toArray(new Integer[listPlaces.size()]);
                                            p = new Places(listBillets, prix);
                                            bd.commit();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Reponse(oos, rep);

                                if (rep.getCode() == TypeReponseTICKMAP.OK) {
                                    cryptedSocket.writeObject(p);
                                    MACMessage m = (MACMessage) ois.readObject();
                                    rep = (ReponseTICKMAP) m.getParam()[0];
                                    if (rep.getCode() == TypeReponseTICKMAP.OK) {
                                        if (!m.authenticate(hmac)) p = null;
                                        else
                                            System.out.println(Thread.currentThread().getName() + "> Validation authentifiée");
                                    }
                                }
                            }
                        }
                        break;
                        case Confirm_Payement:
                            if (log) {
                                MACMessage m = (MACMessage) req.getParam();
                                System.out.println(Thread.currentThread().getName() + "> MAC param: " + Arrays.toString(m.getParam()));
                                String facture = (String) m.getParam()[0];
                                System.out.println(Thread.currentThread().getName() + "> Numéro de facture: " + facture);
                                // TODO Envoi de messages d'échecs
                                if (m.authenticate(hmac)) {
                                    System.out.println(Thread.currentThread().getName() + "> Message authentifié");
                                    if (billets != null && listVoyageurs != null && vol != null) {
                                        if (billets.length == listVoyageurs.length && listVoyageurs.length == places.length) {
                                            // TODO Ajouter la transaction dans les factures
                                            // USERNAME = Celui qui possède la carte
                                            ResultSet rs = bd.selectTransaction(facture);
                                            if (!rs.next())
                                                Reponse(oos, new ReponseTICKMAP(TypeReponseTICKMAP.BAD_TRANSACTION));
                                            else {
                                                Timestamp instant = rs.getTimestamp(2);
                                                bd.insertFacture(facture, instant, vol, listVoyageurs.length);
                                                for (int i = 0; i < billets.length; i++) {
                                                    int idVoy = -1;
                                                    do {
                                                        rs = bd.selectVoyageurId(listVoyageurs[i]);
                                                        if (rs.next())
                                                            idVoy = rs.getInt(1);
                                                        else
                                                            bd.ajoutVoyageur(listVoyageurs[i]);
                                                    } while (idVoy == -1);
                                                    System.out.println(Thread.currentThread().getName() + "> Billet ajouté: " +
                                                            "\n\t Vol:      " + vol +
                                                            "\n\t Billet:   " + billets[i] +
                                                            "\n\t Place:    " + places[i] +
                                                            "\n\t Facture:  " + facture +
                                                            "\n\t Voyageur: " + idVoy);
                                                    bd.ajoutBillets(vol, billets[i], places[i], facture, idVoy);
                                                }
                                                bd.commit();
                                                Reponse(oos, new ReponseTICKMAP(TypeReponseTICKMAP.OK));
                                            }
                                        }
                                    }
                                }
                                else
                                    System.out.println(Thread.currentThread().getName() + "> Authentification du message échouée");
                            }
                            break;
                        case Logout:
                            if (log) {
                                log = false;
                                try {
                                    if (bd != null) {
                                        bd.close(true);
                                        bd = null;
                                    }
                                    rep = new ReponseTICKMAP(TypeReponseTICKMAP.OK);
                                } catch (SQLException e) {
                                    e.printStackTrace(System.out);
                                    rep = new ReponseTICKMAP(TypeReponseTICKMAP.NOT_OK);
                                }
                            }
                            else rep = new ReponseTICKMAP(TypeReponseTICKMAP.NOTLOGGED);
                            Reponse(oos, rep);
                            break;
                        case Disconnect:
                            boucle = false;
                            System.out.println(Thread.currentThread().getName() + "> Déconnexion de " + Procedural.StringIp(client));
                            break;
                    }
                } catch (Exception e) {
                    Logger.getGlobal().log(Level.WARNING, e.getClass().getName(), e);
                    if (e.getClass() != EOFException.class)
                        try {
                            Reponse(oos, ReponseTICKMAP.BAD);
                        } catch (IOException e1) {
                            return;
                        }
                    else
                        return;
                }
            }
        };
    }
}