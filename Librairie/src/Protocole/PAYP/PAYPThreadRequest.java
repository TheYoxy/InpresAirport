package Protocole.PAYP;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.crypto.Cipher;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import NetworkObject.Bean.Carte;
import NetworkObject.Bean.Payement;
import Protocole.SEBATRAP.ReponseST;
import Protocole.SEBATRAP.RequeteST;
import Protocole.SEBATRAP.STThreadRequest;
import Protocole.SEBATRAP.TypeReponseST;
import Protocole.SEBATRAP.TypeRequeteST;
import ServeurClientLog.Objects.ServeurRequete;
import Tools.Bd.Bd;
import Tools.Bd.BdType;
import Tools.Crypto.FonctionsCrypto;
import Tools.Procedural;
import Tools.PropertiesReader;

public class PAYPThreadRequest extends ServeurRequete {
    private static final String keyname        = "payement";
    //todo Properties avec le nom du keystore, password
    private static final String keystore       = "Serveur_Payement.pkcs12";
    private static final String mastercardName = PropertiesReader.getProperties("SERVER_MASTERCARD");
    private static final String password       = "azerty";
    private static final Cipher cipher         = FonctionsCrypto.loadPrivateKeyNoError(keystore, password, keyname);
    private static final int    port           = 26019 /*Integer.parseInt(PropertiesReader.getProperties("PORT_MASTERCARD"))*/;

    public static void main(String[] argv) {
        try {
            SSLSocket          socket = createSSLSocket(keystore, password, mastercardName, port);
            ObjectOutputStream oos    = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(new STThreadRequest());
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(new RequeteST(TypeRequeteST.PAYEMENT));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    private static SSLSocket createSSLSocket(String keyStoreFilename, String password, String servername, int port)
    throws IOException, KeyManagementException, NoSuchAlgorithmException, UnrecoverableKeyException,
           KeyStoreException, CertificateException {
        KeyStore ks = FonctionsCrypto.loadKeyStore(keyStoreFilename, password.toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, password.toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);

        SSLContext context = SSLContext.getInstance("TLS");
        context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());

        SSLSocketFactory factory = context.getSocketFactory();
        return (SSLSocket) factory.createSocket(servername, port);
    }

    @Override
    public Runnable createRunnable(Socket client) {
        return () -> {
            ObjectInputStream  ois;
            ObjectOutputStream oos;
            boolean            boucle = true;
            Bd                 bd;
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
                ReponsePAYP rep = new ReponsePAYP(TypeReponsePAYP.NOT_OK);
                try {
                    RequetePAYP req = (RequetePAYP) ois.readObject();
                    HeaderRunnable(req, Procedural.StringIp(client));
                    switch (req.getType()) {
                        case PAYEMENT:
                            try {
                                Serializable params = req.getParam();

                                assert cipher != null;
                                Payement p = (Payement) FonctionsCrypto.decrypt((byte[]) params, cipher);
                                System.out.println(Thread.currentThread().getName() + "> Reçu: " + p);

                                SSLSocket socket = createSSLSocket(keystore, password, mastercardName, port);
                                System.out.println(Thread.currentThread().getName() + "> Connexion au serveur Mastercard");

                                ObjectOutputStream SSLoos = new ObjectOutputStream(socket.getOutputStream());
                                SSLoos.writeObject(new STThreadRequest());

                                SSLoos = new ObjectOutputStream(socket.getOutputStream());
                                SSLoos.writeObject(new RequeteST(TypeRequeteST.VERIF, p.getCarte(), p.getSomme()));
                                System.out.println(Thread.currentThread().getName() + ">\t Demande de vérification pour " + p);

                                ObjectInputStream SSLois = new ObjectInputStream(socket.getInputStream());
                                ReponseST         repST  = (ReponseST) SSLois.readObject();
                                System.out.println(Thread.currentThread().getName() + ">\t Réponse: " + repST);

                                if (repST.getCode() == TypeReponseST.OK) {
                                    SSLoos.writeObject(new RequeteST(TypeRequeteST.PAYEMENT));
                                    System.out.println(Thread.currentThread().getName() + ">\t Confirmation du payement");

                                    repST = (ReponseST) SSLois.readObject();
                                    System.out.println(Thread.currentThread().getName() + ">\t Réponse: " + repST);
                                    System.out.println(Thread.currentThread().getName() + ">\t Params: " + repST.getParam());
                                    if (repST.getCode() == TypeReponseST.OK) {
                                        rep = new ReponsePAYP(TypeReponsePAYP.OK, repST.getParams());
                                        boucle = false;
                                    }
                                    else
                                        rep = new ReponsePAYP(TypeReponsePAYP.NOT_OK, repST.getCode());
                                }
                                else
                                    rep = new ReponsePAYP(TypeReponsePAYP.NOT_OK, repST.getCode());
                            } catch (Exception e) {
                                e.printStackTrace(System.out);
                            }
                            break;
                        case NEW_CARD:
                            try {
                                Serializable[] params = req.getParams();
                                if (params.length == 2) {
                                    Carte  c = (Carte) FonctionsCrypto.decrypt((byte[]) params[0], cipher);
                                    Double d = (Double) FonctionsCrypto.decrypt((byte[]) params[1], cipher);
                                    System.out.println(Thread.currentThread().getName() + "> Carte reçue: " + c);
                                    System.out.println(Thread.currentThread().getName() + "> Solde reçu : " + d);
                                    ResultSet rs = bd.selectVoyageurId(c.getVoyageur());
                                    if (rs.next()) {
                                        int idVoy = rs.getInt(1);
                                        bd.insertCarte(c.getNumeroCarte(), d, idVoy);
                                        rep = new ReponsePAYP(TypeReponsePAYP.OK);
                                        bd.commit();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace(System.out);
                            }
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
        };
    }
}
