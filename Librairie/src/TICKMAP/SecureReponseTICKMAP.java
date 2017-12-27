package TICKMAP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import ServeurClientLog.Interfaces.TypeReponse;

public class SecureReponseTICKMAP implements ServeurClientLog.Interfaces.Reponse {
    private static final long serialVersionUID = 112L;
    private TypeReponseTICKMAP reponse = null;
    private byte[] param = null;

    public SecureReponseTICKMAP(TypeReponseTICKMAP reponse, Serializable param, Cipher cipher) throws BadPaddingException, IOException, IllegalBlockSizeException {
        this(reponse);
        setParam(param, cipher);
    }

    public SecureReponseTICKMAP(TypeReponseTICKMAP reponse) {
        this.reponse = reponse;
    }

    public void setParam(Serializable param, Cipher cipher) throws IOException, BadPaddingException, IllegalBlockSizeException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(param);
        this.param = cipher.doFinal(baos.toByteArray());
    }

    @Override
    public String toString() {
        return "SecureReponseTICKMAP{" +
                "reponse=" + reponse +
                ", param=" + Arrays.toString(param) +
                '}';
    }

    @Override
    public TypeReponse getCode() {
        return this.reponse;
    }
}
