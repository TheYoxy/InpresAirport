package Tools.Crypto.HMAC;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Mac;

public class MacCalculator {
    public static boolean compMacObject(Mac hmac, byte[] mac, Object o) throws IOException {
        byte[] b = calcObject(hmac, o);
        System.out.println(Arrays.toString(b));
        System.out.println(Arrays.toString(mac));
        return MessageDigest.isEqual(mac, b);
    }

    public static byte[] calcObject(Mac hmac, Object o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        return hmac.doFinal(baos.toByteArray());
    }
}
