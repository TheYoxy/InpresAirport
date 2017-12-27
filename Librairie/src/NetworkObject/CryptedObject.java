package NetworkObject;

import java.io.Serializable;

public class CryptedObject implements Serializable {
    private byte[] object;

    public CryptedObject(byte[] object) {
        this.object = object;
    }

    public byte[] getObject() {
        return object;
    }
}
