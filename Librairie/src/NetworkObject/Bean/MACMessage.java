package NetworkObject.Bean;

import java.io.IOException;
import java.io.Serializable;

import javax.crypto.Mac;

import Tools.Crypto.HMAC.MacCalculator;

public class MACMessage implements Serializable {
    private byte[]         mac;
    private Serializable[] params;

    public MACMessage(Mac hmac, Serializable... param)
    throws IOException {
        this.params = param;
        mac = MacCalculator.calcObject(hmac, param);
    }

    public boolean authenticate(Mac hmac)
    throws IOException {
        return MacCalculator.compMacObject(hmac, mac, params);
    }

    public boolean compareObject(Mac hmac, Object o)
    throws IOException {
        return MacCalculator.compMacObject(hmac, mac, o);
    }

    public Serializable getParam() { return params[0];}

    public Serializable[] getParams() {
        return params;
    }
}
