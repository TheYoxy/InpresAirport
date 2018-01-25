package Tools.Bd;

import java.sql.SQLException;

import NetworkObject.Bean.WebUser;

public abstract class BdMethods {
    public static boolean addWebUser(Bd bd, WebUser wu)
    throws SQLException {
        boolean log = bd.insertLogin(wu.getUsername(), wu.getPassword()),
                web = bd.insertWebUser(wu.getUsername(), wu.getMail(), wu.getNom(), wu.getPrenom());
        System.out.println(Thread.currentThread().getName() + "> Ajout du login: " + log);
        System.out.println(Thread.currentThread().getName() + "> Ajout du WebUser: " + web);
        return log && web;
    }
}
