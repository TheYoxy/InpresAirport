package NetworkObject;

import java.io.Serializable;

import javax.crypto.SecretKey;

public class CryptedPackage implements Serializable {
    private SecretKey key;
    private AESParams params;

    public CryptedPackage(SecretKey key, AESParams params) {
        this.key = key;
        this.params = params;
    }

    public SecretKey getKey() {

        return key;
    }

    public AESParams getParams() {
        return params;
    }

    @Override
    public String toString() {
        return "CryptedPackage{" +
                "key=" + key +
                ", params=" + params +
                '}';
    }
}
