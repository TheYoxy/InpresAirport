package Tools.Crypto.Keystore;

import org.apache.commons.io.FilenameUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertificateException;

public class LoadedKeyStore {
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
    private char[] pass;

    public LoadedKeyStore(String filename, char[] pass) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        if (!filename.startsWith(System.getProperty("file.separator")))
            filename = System.getProperty("file.separator") + filename;

        File f = new File(getClass().getResource(filename).getFile());

        if (!f.exists()) throw new FileNotFoundException(filename + " not found.");
        String type = FilenameUtils.getExtension(filename);
        if (type.isEmpty()) type = "jks";
        System.out.println("Initialisation du keystore");
        ks = KeyStore.getInstance(type);
        System.out.println("Chargement du keystore");
        this.pass = pass;
        ks.load(new FileInputStream(f), pass);
    }

    public KeyStore getKs() {
        return ks;
    }

    public char[] getPass() {
        return pass;
    }
}
