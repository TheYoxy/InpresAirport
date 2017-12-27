package Tools.Crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

public abstract class PrivateKeyCipher {
    public static Cipher cipher;

    static {
        try {
            Security.addProvider(new BouncyCastleProvider());
            System.out.println("Instanciation du keystore");
            KeyStore ks = KeyStore.getInstance("jks");
            System.out.println("Chargement du keystore");
            ks.load(new FileInputStream(System.getProperty("user.home") + "/.keystore"), "GR;Ps~\"[?3])N9j4".toCharArray());
            System.out.println("Chargement de la PrivateKey");
            PrivateKey pvk = (PrivateKey) ks.getKey("appbillets", "GR;Ps~\"[?3])N9j4".toCharArray());
            System.out.println("Création du Cipher pour décrypter");
            cipher = Cipher.getInstance(pvk.getAlgorithm(), "BC");
            System.out.println("Initialisation du cipher");
            cipher.init(Cipher.PRIVATE_KEY, pvk);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (CertificateException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (KeyStoreException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
