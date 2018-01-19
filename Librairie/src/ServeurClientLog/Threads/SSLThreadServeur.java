package ServeurClientLog.Threads;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

import ServeurClientLog.Objects.ServeurRequete;
import Tools.Crypto.FonctionsCrypto;
import Tools.Procedural;

public class SSLThreadServeur extends ThreadServeur {
    private SSLServerSocket serverSocket = null;
    private String keyStoreFile = null;
    private char[] keyStorePassword = null;

    /**
     * @param port
     * @param nb_threads
     * @param keyStoreFile     Nom du fichier contenant le keystore, qui doit <b>obligatoirement</b> se trouver dans le dossier Librairie/src/Tools/Crypto/Keystore/Keystores
     * @param keyStorePassword
     * @param types
     */
    public SSLThreadServeur(int port, int nb_threads, String keyStoreFile, String keyStorePassword, Class<? extends ServeurRequete>... types) {
        super(port, nb_threads, types);
        this.keyStoreFile = keyStoreFile;
        this.keyStorePassword = keyStorePassword.toCharArray();
    }

    @Override
    public void run() {
        //Initialisation de SSL
        KeyStore ks = null;
        try {
            ks = FonctionsCrypto.loadKeyStore(keyStoreFile, keyStorePassword);

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, keyStorePassword);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null); //SecureRandom à null -> Utilisation implicite d'un sécure random

            SSLServerSocketFactory sslServerSocketFactory = context.getServerSocketFactory();
            serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (KeyManagementException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (CertificateException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (KeyStoreException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        for (ThreadClient tc : listChild) tc.start();
        System.out.println(Thread.currentThread().getName() + "> Thread en écoute sur l'adresse: " + Procedural.StringIp(serverSocket.getInetAddress()) + ":" + serverSocket.getLocalPort());
        while (!isInterrupted()) {
            try {
                SSLSocket socket = (SSLSocket) serverSocket.accept();
                System.out.println(Thread.currentThread().getName() + "> Connexion de " + Procedural.IpPort(socket) + '\n');
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                ServeurRequete req = (ServeurRequete) ois.readObject();
                int i, typeLength;
                for (i = 0, typeLength = types.length; i < typeLength; i++) {
                    Class c = types[i];
                    if (c.isAssignableFrom(req.getClass())) {
                        fileSocket.add(req.createRunnable(socket));
                        break;
                    }
                }
                if (i == typeLength) {
                    System.out.println("L'objet envoyé par le client n'est pas accepté par le serveur.");
                    System.out.println("Type: " + req.getClass());
                }
            } catch (IOException e) {
                e.printStackTrace(System.out);
                return;
            } catch (ClassCastException e) {
                e.printStackTrace(System.out);
            } catch (ClassNotFoundException e) {
                e.printStackTrace(System.out);
            }

        }
    }
}
