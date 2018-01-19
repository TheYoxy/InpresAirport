package Tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

import NetworkObject.AESParams;
import Tools.Crypto.FonctionsCrypto;

public class AESCryptedSocket {
    private static final String ENCRYPTION = "Rijndael/CBC/PKCS5Padding";
    private static final String PROVIDER = "BC";
    private Socket s = null;
    private Key key = null;
    private Cipher chiffrement = null;
    private Cipher dechiffrement = null;
    private DataInputStream dis = null;
    private DataOutputStream dos = null;

    public AESCryptedSocket(Socket s, AESParams aesParams) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IOException, NoSuchProviderException, InvalidAlgorithmParameterException {
        this(s, aesParams.getKey(), aesParams.getInit());
    }

    public AESCryptedSocket(Socket s, Key key, byte[] init) throws NoSuchProviderException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IOException {
        this.s = s;
        this.key = key;
        init(init);
    }

    private void init(byte[] init) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException, IOException {
        chiffrement = Cipher.getInstance(ENCRYPTION, PROVIDER);
        chiffrement.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(init));
        dechiffrement = Cipher.getInstance(ENCRYPTION, PROVIDER);
        dechiffrement.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(init));
        dis = new DataInputStream(new BufferedInputStream(s.getInputStream()));
        dos = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
    }

    public Serializable readObject() throws IOException, ClassNotFoundException, BadPaddingException, IllegalBlockSizeException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        do {
            baos.write(dis.readByte());
        } while (dis.available() > 0);
        return (Serializable) FonctionsCrypto.decrypt(baos.toByteArray(), dechiffrement);
    }

    public void writeObject(Serializable e) throws IOException, BadPaddingException, IllegalBlockSizeException {
        dos.write(FonctionsCrypto.encrypt(e, chiffrement));
        dos.flush();
    }
}
