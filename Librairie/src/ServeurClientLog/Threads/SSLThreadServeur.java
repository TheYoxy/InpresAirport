package ServeurClientLog.Threads;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

import ServeurClientLog.Interfaces.Requete;
import Tools.Crypto.Keystore.CustomKeyStore;
import Tools.Procedural;

public class SSLThreadServeur extends ThreadServeur {
    private SSLServerSocket serverSocket = null;

    public SSLThreadServeur(int port, int nb_threads, Class<? extends Requete>... types) {
        super(port, nb_threads, types);
    }

    @Override
    public void run() {
        super.run();
        //Initialisation de SSL
        KeyStore ks = CustomKeyStore.getInstance().getKs();
        try {
            SSLContext context = SSLContext.getInstance("SSLv3");
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, CustomKeyStore.PASS);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);
            context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
            SSLServerSocketFactory sslServerSocketFactory = context.getServerSocketFactory();
            serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (KeyStoreException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (KeyManagementException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        for (ThreadClient tc : listChild) tc.start();

        while (!isInterrupted()) {
            try {
                SSLSocket socket = (SSLSocket) serverSocket.accept();
                System.out.println(Thread.currentThread().getName() + "> Connexion de " + Procedural.IpPort(socket) + '\n');
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Requete req = (Requete) ois.readObject();
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
                System.out.println("Erreur d'accept ! ? [" + e.getMessage() + "]\n");
                return;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
