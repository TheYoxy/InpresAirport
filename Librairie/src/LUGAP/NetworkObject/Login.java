package LUGAP.NetworkObject;

import java.io.Serializable;

public class Login implements Serializable {
    private String User;
    private String Password;

    public Login(String user, String password) {
        this.User = user;
        this.Password = password;
    }

    public String getUser() {
        return User;
    }

    public String getPassword() {
        return Password;
    }
}
