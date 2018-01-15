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
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;

import NetworkObject.Bean.Login;
import NetworkObject.Bean.MACMessage;
import NetworkObject.Bean.Places;
import NetworkObject.Bean.Voyageur;
import NetworkObject.CryptedPackage;
import ServeurClientLog.Interfaces.Reponse;
import ServeurClientLog.Interfaces.ServeurRequete;
import Tools.AESCryptedSocket;
import Tools.Bd.Bd;
import Tools.Bd.BdType;
import Tools.Crypto.Digest.DigestCalculator;
import Tools.Crypto.PrivateKey.PrivateKeyCipher;
import Tools.Ids;
import Tools.Procedural;

public class TickmapThreadRequest implements ServeurRequete {
    @Override
    public Runnable createRunnable(Socket client) {
        return () -> {
            int challenge = 0;
            // Machines a etat
            boolean log = false;
            // Fin machine à état
            Places p = null;

            Bd bd = null;
            boolean boucle = true;
            AESCryptedSocket cryptedSocket = null;
            Mac hmac = null;
            ObjectInputStream ois;
            ObjectOutputStream oos;
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
                    HeaderRunnable(req.getType().toString(), Procedural.StringIp(client));
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
                                ResultSet rs = bd.Select("Login");
                                ResultSetMetaData rsmd = rs.getMetaData();
                                int user = -1, password = -1;
                                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                                    if (rsmd.getColumnName(i).equals("Username")) {
                                        user = i;
                                    } else if (rsmd.getColumnName(i).equals("Password")) {
                                        password = i;
                                    }
                                }
                                if (Procedural.test(user, password)) return;
                                rep = new ReponseTICKMAP(TypeReponseTICKMAP.UNKNOWN_LOGIN);
                                while (rs.next()) {
                                    if (rs.getString(user).equals(((Login) req.getParam()).getUser())) {
                                        byte envoye[] = ((Login) req.getParam()).getPassword();
                                        byte pass[] = DigestCalculator.hashPassword(rs.getString(password), challenge);

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
                                            rep = new ReponseTICKMAP(TypeReponseTICKMAP.OK, bd.SelectLogUser(rs.getString(user)));
                                            System.out.println(Thread.currentThread().getName() + "> Mot de passe correct");
                                            break;
                                        } else {
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
                            if (rep.getCode() != TypeReponseTICKMAP.NOT_OK) {
                                DataInputStream dis = new DataInputStream(new BufferedInputStream(client.getInputStream()));
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                //Lecture de mon objet crypté
                                do {
                                    baos.write(dis.readByte());
                                } while (dis.available() > 0);
                                System.out.println(Thread.currentThread().getName() + "> Récupération des clefs");
                                //Transformation de mon objet
                                ByteArrayInputStream bais = new ByteArrayInputStream(PrivateKeyCipher.cipher.doFinal(baos.toByteArray()));
                                ObjectInputStream tempois = new ObjectInputStream(bais);
                                System.out.println(Thread.currentThread().getName() + "> Décryptage des clefs");
                                CryptedPackage cryptedPackage = (CryptedPackage) tempois.readObject();
                                System.out.println(Thread.currentThread().getName() + "> Clefs reçues");
                                System.out.print(Thread.currentThread().getName() + "> Génération du HMAC: ");
                                cryptedSocket = new AESCryptedSocket(client, cryptedPackage.getParams());
                                hmac = Mac.getInstance("HMAC-SHA1", "BC");
                                hmac.init(cryptedPackage.getKey());
                                System.out.println("réussie");
                                rep = new ReponseTICKMAP(TypeReponseTICKMAP.OK, Bd.toTable(bd.SelectWeekVols()));
                                Reponse(oos, rep);
                                log = true;
                            }
                            break;
                        case Ajout_Voyageurs: {
                            if (log) {
                                rep = new ReponseTICKMAP(TypeReponseTICKMAP.NOT_OK);
                                try {
                                    List list = (List) cryptedSocket.readObject();
                                    String vol = list.get(0).toString();
                                    ResultSet rs = bd.SelectVolReservable(vol);
                                    Voyageur[] listVoyageurs = (Voyageur[]) list.get(1);
                                    int count = listVoyageurs.length;
                                    if (rs.next()) {
                                        int nb = rs.getInt("PlacesDisponible");
                                        if (nb < count)
                                            rep = new ReponseTICKMAP(TypeReponseTICKMAP.FULL, count);
                                        else {
                                            rep = new ReponseTICKMAP(TypeReponseTICKMAP.OK);
                                            bd.SuppPlacesReservables(vol, count);
                                            ResultSet rss = bd.SelectLieu(vol);
                                            rss.next();
                                            String lieu = rss.getString(1);
                                            p = new Places(Ids.genIdBillets(bd.SelectBillets(vol), lieu, vol, count), nb * rs.getDouble("Prix"));
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Reponse(oos, rep);

                                if (rep.getCode() == TypeReponseTICKMAP.OK) {
                                    cryptedSocket.writeObject(p);
                                    MACMessage m = (MACMessage) ois.readObject();
                                    rep = (ReponseTICKMAP) m.getParam();
                                    if (rep.getCode() == TypeReponseTICKMAP.OK)
                                        if (!m.authenticate(hmac)) p = null;
                                }
                            }
                        }
                        break;
                        case Confirm_Payement:
                            if (log && p != null) {
                                MACMessage m = (MACMessage) req.getParam();
                                if (m.authenticate(hmac)) {
                                    // Enregistrement des infos de payement & co
                                }
                            }
                            break;
                        case Logout:
                            if (log) {
                                log = false;
                                try {
                                    if (bd != null) {
                                        bd.Close(true);
                                        bd = null;
                                    }
                                    rep = new ReponseTICKMAP(TypeReponseTICKMAP.OK);
                                } catch (SQLException e) {
                                    e.printStackTrace(System.out);
                                    rep = new ReponseTICKMAP(TypeReponseTICKMAP.NOT_OK);
                                }
                            } else rep = new ReponseTICKMAP(TypeReponseTICKMAP.NOTLOGGED);
                            Reponse(oos, rep);
                            break;
                        case Disconnect:
                            boucle = false;
                            System.out.println(Thread.currentThread().getName() + "> Déconnexion de " + Procedural.StringIp(client));
                            break;
                    }
                } catch (IOException | ClassNotFoundException | BadPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | NoSuchProviderException | SQLException e) {
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

    private void HeaderRunnable(String type, String from) {
        System.out.println();
        System.out.println(Thread.currentThread().getName() + "> Traitement d'une requête de " + type + " de " + from);
    }

    private void Reponse(final ObjectOutputStream outputStream, Reponse rep) throws IOException {
        System.out.println(Thread.currentThread().getName() + "> Réponse: " + rep);
        outputStream.writeObject(rep);
    }
}