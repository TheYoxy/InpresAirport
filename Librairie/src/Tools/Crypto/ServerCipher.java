package Tools.Crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

import NetworkObject.AESParams;

public abstract class ServerCipher {
    private static final ThreadLocal<Cipher> CRYPT = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<Cipher> DECRYPT = ThreadLocal.withInitial(() -> null);

    public static void setCipher(AESParams params) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(params.getKey().getAlgorithm(), "BC");
        cipher.init(Cipher.ENCRYPT_MODE, params.getKey(), new IvParameterSpec(params.getInit()));
        CRYPT.set(cipher);

        cipher.init(Cipher.DECRYPT_MODE, params.getKey(), new IvParameterSpec(params.getInit()));
        DECRYPT.set(cipher);
    }

    public static Cipher getCryptCipher() {
        return CRYPT.get();
    }

    public static Cipher getDecryptCipher() {
        return DECRYPT.get();
    }
}
