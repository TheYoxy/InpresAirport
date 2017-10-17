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

    private void setBd(Bd base) {
        MySql = base;
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
        System.out.println(Thread.currentThread().getName() + "> Message de " + Procedural.StringIp(s) + ":" + s.getPort());
        switch (this.Type) {
            case Login:
                retour = () -> {
                    System.out.println(Thread.currentThread().getName() + "> Traitement d'une requête login de " + Procedural.StringIp(s) + ":" + s.getPort());
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
                            System.out.println(Thread.currentThread().getName() + "> User introuvable");
                            return;
                        } else if (password == -1) {
                            System.out.println(Thread.currentThread().getName() + "> Password introuvable");
                            return;
                        }

                        ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                        ReponseLUGAP reponse = new ReponseLUGAP(TypeReponseLUGAP.UNKNOWN_LOGIN, "");
                        while (rs.next()) {
                            if (rs.getString(user).equals(((Login) Param).getUser())) {
                                System.out.println(Thread.currentThread().getName() + "> Utilisateur trouvé");
                                if (rs.getString(password).equals(((Login) Param).getPassword())) {
                                    reponse = new ReponseLUGAP(TypeReponseLUGAP.LOG, "");
                                    System.out.println(Thread.currentThread().getName() + "> Mot de passe correct");
                                    break;
                                } else {
                                    reponse = new ReponseLUGAP(TypeReponseLUGAP.BAD_PASSWORD, "");
                                    System.out.println(Thread.currentThread().getName() + "> Mot de passe incorrect");
                                    break;
                                }
                            }
                        }
                        oos.writeObject(reponse);
                    } catch (SQLException e) {
                        System.out.println(Thread.currentThread().getName() + "> SQLException: " + e.getLocalizedMessage());
                    } catch (IOException e) {
                        System.out.println(Thread.currentThread().getName() + "> IOException: " + e.getLocalizedMessage());
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
