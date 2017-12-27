package TICKMAP;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import NetworkObject.AESParams;
import ServeurClientLog.Interfaces.Requete;
import Tools.Bd;
import Tools.Crypto.PrivateKeyCipher;
import Tools.Crypto.ServerCipher;

public class SecureRequeteTICKMAP implements Requete {
    private static final long serialVersionUID = 1234L;
    private static final ThreadLocal<Integer> CHALLENGE = ThreadLocal.withInitial(() -> 0);
    private static final ThreadLocal<Bd> BD_THREAD_LOCAL = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<Boolean> LOG_STATUS = ThreadLocal.withInitial(() -> false);

    private TypeRequeteTICKMAP type = null;
    private String from = "";
    private byte[] param = null;

    public SecureRequeteTICKMAP(TypeRequeteTICKMAP type, Serializable param, String from, Cipher cipher) throws IOException, BadPaddingException, IllegalBlockSizeException {
        this(type);
        this.from = from;
        encrypt(param, cipher);
    }

    private SecureRequeteTICKMAP(TypeRequeteTICKMAP type) {
        this.type = type;
    }

    private void encrypt(Serializable param, Cipher cipher) throws IOException, BadPaddingException, IllegalBlockSizeException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(param);
        this.param = cipher.doFinal(baos.toByteArray());
    }

    @Override
    public Runnable createRunnable(ObjectOutputStream output) {
        Runnable retour = null;
        switch (this.type) {
            case Handshake:
                retour = () -> {
                    try {
                        System.out.println(Thread.currentThread().getName() + "> Message de type Handshake reçu");
                        AESParams aesParams = (AESParams) decrypt(PrivateKeyCipher.cipher);
                        System.out.println(Thread.currentThread().getName() + "> Création du cipher avec la clef de session");
                        ServerCipher.setCipher(aesParams);
                        output.writeObject(new ReponseTICKMAP(TypeReponseTICKMAP.OK));
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    } catch (InvalidAlgorithmParameterException e) {
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (NoSuchProviderException e) {
                        e.printStackTrace();
                    }
                };
                break;
            case Logout:
                break;
            case Disconnect:
                break;
        }
        return retour;
    }

    private Serializable decrypt(Cipher cipher) throws BadPaddingException, IllegalBlockSizeException, IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(cipher.doFinal(this.param)));
        return (Serializable) ois.readObject();
    }

    @Override
    public boolean isLogin() {
        return false;
    }

    @Override
    public boolean loginSucced() {
        return true;
    }

    @Override
    public boolean isLogout() {
        return this.type == TypeRequeteTICKMAP.Logout;
    }

    @Override
    public boolean isDisconnect() {
        return this.type == TypeRequeteTICKMAP.Logout;
    }
}
