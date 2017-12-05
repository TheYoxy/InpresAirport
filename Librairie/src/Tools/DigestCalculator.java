package Tools;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class DigestCalculator {
    private static MessageDigest md;

    static {
        //addProvider en local, car on travaille sur différentes machines
        Security.addProvider(new BouncyCastleProvider());
        try {
            md = MessageDigest.getInstance("SHA-1", "BC");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace(System.out);
            System.exit(-1);
        }
    }

    public static byte[] hashPassword(String password, int challenge) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeUTF(password);
        dos.writeInt(challenge);
        dos.writeUTF(String.valueOf(LocalDate.now()));
        return md.digest(baos.toByteArray());
    }

    public static byte[] hashPassword(String password, byte[] challenge) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeUTF(password);
        dos.write(challenge);
        dos.writeUTF(String.valueOf(LocalDate.now()));
        return md.digest(baos.toByteArray());
    }

    public static byte[] digestMessage(List l) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        for (Object o : l) {
            try {
                if (o instanceof Integer)
                    dos.writeInt((Integer) o);
                else
                    dos.writeBytes(o.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        byte[] b = md.digest(baos.toByteArray());
        return b;
    }
}
