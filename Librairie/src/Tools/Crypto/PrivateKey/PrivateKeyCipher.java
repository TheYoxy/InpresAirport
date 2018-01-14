package Tools.Crypto.PrivateKey;

import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import Tools.Crypto.Keystore.CustomKeyStore;

public abstract class PrivateKeyCipher {
    public static Cipher cipher;

    static {
        try {
            KeyStore ks = CustomKeyStore.getInstance().getKs();
            System.out.println("Chargement de la Tools.Crypto.PrivateKey");
            PrivateKey pvk = (PrivateKey) ks.getKey("appbillets", "GR;Ps~\"[?3])N9j4".toCharArray());
            System.out.println("Création du Cipher pour décrypter");
            cipher = Cipher.getInstance(pvk.getAlgorithm(), "BC");
            System.out.println("Initialisation du cipher");
            cipher.init(Cipher.PRIVATE_KEY, pvk);
        } catch (NoSuchAlgorithmException e) {
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
