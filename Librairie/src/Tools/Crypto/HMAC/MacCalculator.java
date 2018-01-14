package Tools.Crypto.HMAC;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;

import javax.crypto.Mac;

public class MacCalculator {
    public static boolean compMacObject(Mac hmac, byte[] mac, Object o) throws IOException {
        return MessageDigest.isEqual(mac, calcObject(hmac, o));
    }

    public static byte[] calcObject(Mac hmac, Object o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        return hmac.doFinal(baos.toByteArray());
    }
}
