package NetworkObject.Bean;

import java.io.Serializable;

public class WebUser implements Serializable {
    private String mail;
    private String nom;
    private String password;
    private String prenom;
    private String username;

    public WebUser(String nom, String prenom, String mail, String username, String password) {
        this.mail = mail;
        this.nom = nom;
        this.password = password;
        this.prenom = prenom;
        this.username = username;
    }

    public String getMail() {
        return mail;
    }

    public String getNom() {
        return nom;
    }

    public String getPassword() {
        return password;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getUsername() {
        return username;
    }
}
