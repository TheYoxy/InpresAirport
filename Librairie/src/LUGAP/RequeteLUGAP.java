package LUGAP;

import LUGAP.NetworkObject.Login;
import ServeurClientLog.Interfaces.Requete;
import Tools.Bd;
import Tools.BdType;
import Tools.Procedural;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class RequeteLUGAP implements Requete {
    private TypeRequeteLUGAP Type;
    private String ChargeUtile;
    private Serializable Param;
    private Bd MySql = null;

    public RequeteLUGAP(TypeRequeteLUGAP t, Serializable Param, String chu) {
        this.Type = t;
        this.ChargeUtile = chu;
        this.Param = Param;
    }

    public Object getParam() {
        return Param;
    }

    public String getChargeUtile() {
        return ChargeUtile;
    }

    private void setBd() throws IOException, SQLException {
        MySql = new Bd(BdType.MySql);
    }

    private void setBd(Connection connection) throws SQLException {
        MySql = new Bd(connection);
    }

    @Override
    public Runnable createRunnable(final Socket s) {
        Runnable retour = null;
        try {
            this.setBd();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            return null;
        }
        switch (this.Type) {
/*
retour = () -> {
    System.out.println("Envoi à " + Procedural.StringIp(s) + ":" + s.getPort());
    ReponseLUGAP repo = new ReponseLUGAP(TypeReponseLUGAP.OK, getChargeUtile());
    ObjectOutputStream oos;
    try {
        oos = new ObjectOutputStream(s.getOutputStream());
        oos.writeObject(repo);
        oos.flush();
        System.out.println("Message envoyé");
    } catch (IOException e) {
        System.err.println("Erreur lors de l'envoi d'un message: " + e.getMessage());
    }
};*/
            case Login:
                retour = () -> {
                    System.out.println("Envoi à " + Procedural.StringIp(s) + ":" + s.getPort());
                    try {
                        ResultSet rs = MySql.Select("Login");
                        ResultSetMetaData rsmd = rs.getMetaData();
                        int user = -1, password = -1;
                        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                            if (rsmd.getColumnName(i).equals("username"))
                                user = i;
                            else if (rsmd.getColumnName(i).equals("password"))
                                password = i;
                        }
                        if (user == -1) {
                            System.out.println("User introuvable");
                            return;
                        } else if (password == -1) {
                            System.out.println("Password introuvable");
                            return;
                        }

                        ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                        ReponseLUGAP reponse = new ReponseLUGAP(TypeReponseLUGAP.UNKNOWN_LOGIN, "");
                        while (rs.next()) {
                            if (rs.getString(user).equals(((Login) Param).getUser())) {
                                if (rs.getString(password).equals(((Login) Param).getPassword())) {
                                    reponse = new ReponseLUGAP(TypeReponseLUGAP.LOG, "");
                                    break;
                                } else {
                                    reponse = new ReponseLUGAP(TypeReponseLUGAP.BAD_PASSWORD, "");
                                    break;
                                }
                            }
                        }
                        oos.writeObject(reponse);
                    } catch (SQLException e) {
                        System.out.println("SQLException: " + e.getLocalizedMessage());
                    } catch (IOException e) {
                        System.out.println("IOException: " + e.getLocalizedMessage());
                    }
                };
                break;
            case Logout:
                break;
            case Disconnect:
                break;
        }
        return retour;
    }

    @Override
    public boolean isDisconnect() {
        return this.Type == TypeRequeteLUGAP.Disconnect;
    }
}
