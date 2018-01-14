package Tools.Crypto.Keystore;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertificateException;

public class CustomKeyStore {
    public final static char[] PASS = "GR;Ps~\"[?3])N9j4".toCharArray();
    private static CustomKeyStore ourInstance = new CustomKeyStore();

    static {
        BouncyCastleProvider bcp = new BouncyCastleProvider();
        Provider[] providers = Security.getProviders();
        boolean find = false;
        for (int i = 0, providersLength = providers.length; i < providersLength && !find; i++) {
            Provider p = providers[i];
            if (p == bcp)
                find = true;
        }
        if (!find)
            Security.addProvider(bcp);
    }

    private KeyStore ks;

    private CustomKeyStore() {
        try {
            System.out.println("Instanciation du keystore");
            ks = KeyStore.getInstance("jks");
            System.out.println("Chargement du keystore");
            ks.load(new FileInputStream(System.getProperty("user.home") + "/.keystore"), PASS);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static CustomKeyStore getInstance() {
        return ourInstance;
    }

    public KeyStore getKs() {
        return ks;
    }
}
