package LUGAP.NetworkObject;

import java.io.Serializable;

public class Login implements Serializable {
    private String User;
    private String Password;
    //TODO Passage du password via le hash

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

    @Override
    public String toString() {
        return "Login: " + User + " | Password: " + Password;
    }
}
