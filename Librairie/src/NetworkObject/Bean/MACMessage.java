package NetworkObject.Bean;

import java.io.IOException;
import java.io.Serializable;

import javax.crypto.Mac;

import Tools.Crypto.HMAC.MacCalculator;

public class MACMessage implements Serializable {
    private Serializable param;
    private byte[] mac;

    public MACMessage(Mac hmac, Serializable param) throws IOException {
        this.param = param;
        mac = MacCalculator.calcObject(hmac, param);
    }

    public boolean compareObject(Mac hmac, Object o) throws IOException {
        return MacCalculator.compMacObject(hmac, mac, o);
    }

    public boolean authenticate(Mac hmac) throws IOException {
        return MacCalculator.compMacObject(hmac, mac, param);
    }

    public Serializable getParam() {
        return param;
    }
}
