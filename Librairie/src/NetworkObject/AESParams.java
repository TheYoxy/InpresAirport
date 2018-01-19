package NetworkObject;

import java.io.Serializable;
import java.util.Arrays;

import javax.crypto.SecretKey;

public class AESParams implements Serializable {
    private SecretKey key;
    private byte[] init;

    public AESParams(SecretKey key, byte[] init) {

        this.key = key;
        this.init = init;
    }

    public SecretKey getKey() {
        return key;
    }

    public byte[] getInit() {
        return init;
    }

    @Override
    public String toString() {
        return "AESParams{" +
                "\tkey=" + key + ",\n" +
                "\tinit=" + Arrays.toString(init) + "\n" +
                '}';
    }
}
