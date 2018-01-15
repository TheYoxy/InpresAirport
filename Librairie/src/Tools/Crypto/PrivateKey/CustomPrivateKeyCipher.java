package Tools.Crypto.PrivateKey;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import Tools.Crypto.Keystore.LoadedKeyStore;

public class CustomPrivateKeyCipher {
    private Cipher cipher;

    public CustomPrivateKeyCipher(String filename, String storepass, String keyname) throws CertificateException, NoSuchPaddingException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, InvalidKeyException, IOException {
        this(filename, storepass, keyname, storepass);
    }

    public CustomPrivateKeyCipher(String filename, String storepass, String keyname, String keypass) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException {
        if (!filename.startsWith(System.getProperty("file.separator")))
            filename = System.getProperty("file.separator") + filename;
        KeyStore ks = new LoadedKeyStore(filename, storepass.toCharArray()).getKs();
        System.out.println("Chargement de la PrivateKey");
        PrivateKey pvk = (PrivateKey) ks.getKey(keyname, keypass.toCharArray());
        System.out.println("Création du Cipher pour décrypter");
        cipher = Cipher.getInstance(pvk.getAlgorithm(), "BC");
        System.out.println("Initialisation du cipher");
        cipher.init(Cipher.PRIVATE_KEY, pvk);
    }

    public Cipher getCipher() {
        return cipher;
    }
}
