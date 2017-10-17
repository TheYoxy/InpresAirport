package LUGAP;

import LUGAP.NetworkObject.Login;
import LUGAP.NetworkObject.Table;
import ServeurClientLog.Interfaces.Requete;
import Tools.Bd;
import Tools.BdType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;

public class RequeteLUGAP implements Requete {
    private TypeRequeteLUGAP Type = null;
    private String ChargeUtile = null;
    private Serializable Param = null;
    private Bd MySql = null;
    private String From = null;

    public RequeteLUGAP(TypeRequeteLUGAP type, String chargeUtile) {
        Type = type;
        ChargeUtile = chargeUtile;
    }

    public RequeteLUGAP(TypeRequeteLUGAP type, String chargeUtile, Serializable param, String from) {
        Type = type;
        ChargeUtile = chargeUtile;
        Param = param;
        From = from;
    }

    public RequeteLUGAP(TypeRequeteLUGAP type, String chargeUtile, String from) {
        Type = type;
        ChargeUtile = chargeUtile;
        From = from;
    }

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

    public Runnable createRunnable(final ObjectOutputStream oosClient) {
        Runnable retour = null;
        this.setBd(Bd.getMySql());
        System.out.println(Thread.currentThread().getName() + "> Message " + From);
        switch (this.Type) {
            case Login:
                retour = () -> {
                    System.out.println(Thread.currentThread().getName() + "> Traitement d'une requête login de " + From);
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
                        oosClient.writeObject(reponse);
                    } catch (SQLException e) {
                        System.out.println(Thread.currentThread().getName() + "> SQLException: " + e.getMessage());
                    } catch (IOException e) {
                        System.out.println(Thread.currentThread().getName() + "> IOException: " + e.getMessage());
                    }
                };
                break;
            case Logout:
                break;
            case Disconnect:
                break;
            case Request_Vols:
                retour = () -> {
                    System.out.println(Thread.currentThread().getName() + "> Traitement d'une requête Request_vols de " + From);
                    try {
                        ResultSet rs = MySql.Select("Vols");
                        Vector<String> title = new Vector<>();
                        ResultSetMetaData rsmd = rs.getMetaData();
                        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                            title.add(rsmd.getColumnName(i));
                        }
                        Vector<Vector<String>> champs = new Vector<>();
                        while (rs.next()) {
                            Vector<String> temp = new Vector<>();
                            for (int i = 1; i < rsmd.getColumnCount(); i++) {
                                try {
                                    switch (rsmd.getColumnType(i)) {
                                        case Types.VARCHAR:
                                            temp.add(rs.getString(i));
                                            break;
                                        case Types.INTEGER:
                                        case Types.NUMERIC:
                                            temp.add(String.valueOf(rs.getInt(i)));
                                            break;
                                        case Types.DATE:
                                            temp.add(rs.getDate(i).toString());
                                            break;
                                        case Types.NULL:
                                            temp.add("");
                                            break;
                                        default:
                                            System.out.println(Thread.currentThread().getName() + "> Unknown type: " + rsmd.getColumnTypeName(i));
                                            break;
                                    }
                                } catch (SQLException e) {
                                    System.out.println(Thread.currentThread().getName() + "> Exception: " + e.getMessage());
                                }
                            }
                        }
                        Table t = new Table(title, champs);
                        ReponseLUGAP rep = new ReponseLUGAP(TypeReponseLUGAP.OK, "", t);
                        oosClient.writeObject(rep);
                    } catch (SQLException e) {
                        //TODO Renvoi erreur si exception
                        System.out.println(Thread.currentThread().getName() + "> SQLException: " + e.getMessage());
                    } catch (IOException e) {
                        System.out.println(Thread.currentThread().getName() + "> IOException: " + e.getMessage());
                    }
                };
                break;
        }
        return retour;
    }

    @Override
    public boolean isDisconnect() {
        return this.Type == TypeRequeteLUGAP.Disconnect;
    }
}
