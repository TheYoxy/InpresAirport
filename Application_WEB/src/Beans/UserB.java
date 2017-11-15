package Beans;

import java.io.Serializable;

public class UserB implements Serializable{
    private String Username;
    private String Mail;

    public UserB() {
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getMail() {
        return Mail;
    }

    public void setMail(String mail) {
        Mail = mail;
    }
}
