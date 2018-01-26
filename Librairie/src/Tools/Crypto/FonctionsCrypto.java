package Tools.Crypto;

import org.apache.commons.io.FilenameUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import NetworkObject.AESParams;

public class FonctionsCrypto {
    private static final String PROVIDER = "BC";
    private static final String SYMENCRYPTION = "Rijndael/CBC/PKCS5Padding";

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

    public static Object decrypt(byte[] lu, Cipher cipher) throws BadPaddingException, IllegalBlockSizeException, IOException, ClassNotFoundException {
        return new ObjectInputStream(new ByteArrayInputStream(cipher.doFinal(lu))).readObject();
    }

    public static byte[] encrypt(Object o, Cipher cipher) throws BadPaddingException, IllegalBlockSizeException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        byte[] b = baos.toByteArray();

        //        System.err.println(cipher.getAlgorithm() + "> " + Arrays.toString(b));
        //        System.err.println(cipher.getAlgorithm() + "> " + b.length);
        byte[] c = cipher.doFinal(b);

        //        System.err.println(cipher.getAlgorithm() + "> " + "Après");
        //        System.err.println(cipher.getAlgorithm() + "> " + Arrays.toString(c));
        //        System.err.println(cipher.getAlgorithm() + "> " + c.length);
        return c;
    }

    public static Cipher genDecryptCipher(AESParams params) throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException {
        return genDecryptCipher(params.getKey(), params.getInit());
    }

    public static Cipher genDecryptCipher(SecretKey secretKey, byte[] iv) throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
        Cipher c = Cipher.getInstance(SYMENCRYPTION, PROVIDER);
        c.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
        return c;
    }

    public static Cipher genEncryptCipher(AESParams params) throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException {
        return genEncryptCipher(params.getKey(), params.getInit());
    }

    public static Cipher genEncryptCipher(SecretKey secretKey, byte[] iv) throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
        Cipher c = Cipher.getInstance(SYMENCRYPTION, PROVIDER);
        c.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
        return c;
    }

    public static SecretKey genKey() throws NoSuchProviderException, NoSuchAlgorithmException {
        System.err.println("Génération d'une clef Rijndael/AES");
        KeyGenerator key = KeyGenerator.getInstance("Rijndael", PROVIDER);
        key.init(new SecureRandom());
        return key.generateKey();
    }

    public static Cipher loadPrivateKeyNoError(String filename, String storepass, String keyname) {
        try {
            return loadPrivateKey(filename, storepass, keyname);
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(42);
        return null;
    }

    public static Cipher loadPrivateKey(String filename, String storepass, String keyname) throws CertificateException, NoSuchPaddingException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, InvalidKeyException, IOException {
        return loadPrivateKey(filename, storepass, keyname, storepass);
    }

    public static Cipher loadPrivateKey(String filename, String storepass, String keyname, String keypass) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException {
        if (!filename.startsWith(System.getProperty("file.separator")))
            filename = System.getProperty("file.separator") + filename;
        KeyStore ks = FonctionsCrypto.loadKeyStore(filename, storepass.toCharArray());

        System.err.println("Chargement de la PrivateKey");
        PrivateKey pvk = (PrivateKey) ks.getKey(keyname, keypass.toCharArray());

        System.err.println("Création du Cipher sur base de la privatekey");
        System.err.println("Algorithm: " + pvk.getAlgorithm());
        Cipher cipher = Cipher.getInstance(pvk.getAlgorithm(), PROVIDER);

        System.err.println("Initialisation du cipher");
        cipher.init(Cipher.PRIVATE_KEY, pvk);
        return cipher;
    }

    public static KeyStore loadKeyStore(String filename, char[] pass) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        //if (!filename.startsWith(System.getProperty("file.separator")))
          //  filename = System.getProperty("file.separator") + filename;
        System.out.println(">filename: "+ filename);
        File f = new File(FonctionsCrypto.class.getResource(filename).getFile());

        if (!f.exists()) throw new FileNotFoundException(filename + " not found.");
        String type = FilenameUtils.getExtension(filename);

        if (type.isEmpty()) type = "jks";

        System.err.println("Initialisation du keystore");
        KeyStore ks = KeyStore.getInstance(type);

        System.err.println("Chargement du keystore");
        ks.load(new FileInputStream(f), pass);
        return ks;
    }

    public static Cipher loadPublicKey(String filename, String storepass, String keyname) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException {
        if (!filename.startsWith(System.getProperty("file.separator")))
            filename = System.getProperty("file.separator") + filename;
        KeyStore ks = FonctionsCrypto.loadKeyStore(filename, storepass.toCharArray());

        System.err.println("Chargement de la publicKey");
        Certificate certificate = ks.getCertificate(keyname);

        System.err.println("Création du Cipher pubic");
        Cipher cipher = Cipher.getInstance(certificate.getPublicKey().getAlgorithm(), PROVIDER);

        System.err.println("Initialisation du cipher");
        cipher.init(Cipher.PUBLIC_KEY, certificate);
        return cipher;
    }
}
