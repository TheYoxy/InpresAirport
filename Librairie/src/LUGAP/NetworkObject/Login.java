package LUGAP.NetworkObject;

import java.io.Serializable;
import java.util.Arrays;

public class Login implements Serializable {
    private String User;
    private byte[] Password;

    public Login(String user, byte[] password) {
        this.User = user;
        this.Password = password;
    }

    public String getUser() {
        return User;
    }

    public byte[] getPassword() {
        return Password;
    }

    @Override
    public String toString() {
        return "Login: " + User + " | Password: " + Arrays.toString(Password);
    }
}
