package LUGAP;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import NetworkObject.Login;
import NetworkObject.Table;
import ServeurClientLog.Interfaces.Requete;
import TICKMAP.RequeteTICKMAP;
import Tools.Bd;
import Tools.BdType;
import Tools.DigestCalculator;

public class LUGAPThreadRequest implements Requete {
    private static final long serialVersionUID = 129L;

    @Override
    public Runnable createRunnable(Socket client) {
        return () -> {
            try {
                int challenge = 0;
                boolean log = false;
                Bd bd = null;
                ResultSet resultSet = null;
                ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                boolean boucle = true;
                while (boucle) {
                    RequeteLUGAP req = (RequeteLUGAP) ois.readObject();
                    ReponseLUGAP rep;
                    HeaderRunnable(req.getType().toString(), req.getFrom());
                    switch (req.getType()) {
                        case TryConnect:
                            challenge = new Random().nextInt();
                            rep = new ReponseLUGAP(TypeReponseLUGAP.OK, challenge);
                            System.out.println(Thread.currentThread().getName() + "> Digest salé généré: " + challenge);
                            Reponse(oos, rep);
                            break;
                        case Login:
                            try {
                                bd = new Bd(BdType.MySql, 5);
                                ResultSet rs = bd.Select("Login");
                                ResultSetMetaData rsmd = rs.getMetaData();
                                int user = -1, password = -1;
                                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                                    if (rsmd.getColumnName(i).equals("Username")) {
                                        user = i;
                                    } else if (rsmd.getColumnName(i).equals("Password")) {
                                        password = i;
                                    }
                                }
                                if (RequeteTICKMAP.test(user, password)) return;
                                rep = new ReponseLUGAP(TypeReponseLUGAP.UNKNOWN_LOGIN);
                                while (rs.next()) {
                                    if (rs.getString(user).equals(((Login) req.getParam()).getUser())) {
                                        byte envoye[] = ((Login) req.getParam()).getPassword();
                                        byte pass[] = DigestCalculator.hashPassword(rs.getString(password), challenge);
                                        System.out.println(Thread.currentThread().getName() + "> Utilisateur trouvé");

                                        System.out.println("-------------------------------------------------------------------");
                                        System.out.println(Thread.currentThread().getName() + "> Hash en string: ");
                                        System.out.println(Thread.currentThread().getName() + "> Hash envoyé:           " + new String(envoye));
                                        System.out.println(Thread.currentThread().getName() + "> Hash de l'utilisateur: " + new String(pass));
                                        System.out.println("-------------------------------------------------------------------");
                                        System.out.println(Thread.currentThread().getName() + "> Hash en tableau: ");
                                        System.out.println(Thread.currentThread().getName() + "> Hash envoyé:           " + Arrays.toString(envoye));
                                        System.out.println(Thread.currentThread().getName() + "> Hash de l'utilisateur: " + Arrays.toString(pass));
                                        System.out.println("-------------------------------------------------------------------");

                                        if (MessageDigest.isEqual(pass, ((Login) req.getParam()).getPassword())) {
                                            rep = new ReponseLUGAP(TypeReponseLUGAP.LOG, bd.SelectLogUser(rs.getString(user)));
                                            System.out.println(Thread.currentThread().getName() + "> Mot de passe correct");
                                            log = true;
                                            break;
                                        } else {
                                            rep = new ReponseLUGAP(TypeReponseLUGAP.BAD_PASSWORD);
                                            System.out.println(Thread.currentThread().getName() + "> Mot de passe incorrect");
                                            break;
                                        }
                                    }
                                }
                            } catch (SQLException e) {
                                System.out.println(Thread.currentThread().getName() + "> SQLException: " + e.getMessage());
                                rep = new ReponseLUGAP(TypeReponseLUGAP.NOT_OK);
                            } catch (IOException e) {
                                System.out.println(Thread.currentThread().getName() + "> IOException: " + e.getMessage());
                                rep = new ReponseLUGAP(TypeReponseLUGAP.NOT_OK);
                            }
                            if (rep.getCode() == TypeReponseLUGAP.UNKNOWN_LOGIN) {
                                System.out.println(Thread.currentThread().getName() + "> Utilisateur introuvable");
                            }
                            Reponse(oos, rep);
                            break;
                        case Logout:
                            if (log) {
                                log = false;
                                try {
                                    if (bd != null) {
                                        bd.Close(true);
                                        bd = null;
                                    }
                                    rep = new ReponseLUGAP(TypeReponseLUGAP.OK);
                                } catch (SQLException e) {
                                    e.printStackTrace(System.out);
                                    rep = new ReponseLUGAP(TypeReponseLUGAP.NOT_OK);
                                }
                            } else rep = new ReponseLUGAP(TypeReponseLUGAP.NOTLOGGED);
                            Reponse(oos, rep);
                            break;
                        case Disconnect:
                            boucle = false;
                            break;
                        case Request_Vols:
                            if (log) {
                                try {
                                    Table t = Bd.toTable(bd.SelectTodayVols());
                                    rep = new ReponseLUGAP(TypeReponseLUGAP.OK, t);
                                } catch (SQLException e) {
                                    System.out.println(Thread.currentThread().getName() + "> SQLException: " + e.getMessage());
                                    rep = new ReponseLUGAP(TypeReponseLUGAP.NOT_OK);
                                }
                            } else rep = new ReponseLUGAP(TypeReponseLUGAP.NOTLOGGED);
                            Reponse(oos, rep);
                            break;
                        case Request_Bagages_Vol:
                            if (log) {
                                try {
                                    bd.setTransactionIsolationLevel(Connection.TRANSACTION_SERIALIZABLE);
                                    final String vol = (String) req.getParam();
                                    System.out.println(Thread.currentThread().getName() + "> Id du vol demandé: " + vol);
                                    resultSet = bd.SelectBagageVol(vol);
                                    rep = new ReponseLUGAP(TypeReponseLUGAP.OK, Bd.toTable(resultSet));
                                } catch (SQLException e) {
                                    if (e.getErrorCode() == 1205)
                                        rep = new ReponseLUGAP(TypeReponseLUGAP.SQL_LOCK);
                                    else {
                                        rep = new ReponseLUGAP(TypeReponseLUGAP.NOT_OK);
                                        System.out.println(Thread.currentThread().getName() + "> SQLException: " + e.getMessage());
                                        System.out.println(Thread.currentThread().getName() + "> SQLException code: " + e.getErrorCode());
                                    }
                                }
                            } else rep = new ReponseLUGAP(TypeReponseLUGAP.NOTLOGGED);
                            Reponse(oos, rep);
                            break;
                        case Update_Bagage_Vol:
                            if (log) {
                                LinkedList<Object> l = (LinkedList<Object>) req.getParam();
                                //Blindage
                                if (l.size() < 3 && l.size() % 2 == 0) {
                                    System.out.println(Thread.currentThread().getName() + "> Taille incohérente");
                                    rep = new ReponseLUGAP(TypeReponseLUGAP.NOT_OK);
                                    Reponse(oos, rep);
                                    return;
                                }

                                System.out.println(Thread.currentThread().getName() + "> Clef primaire de l'objet modifié: " + l.get(0));
                                for (int i = 1; i < l.size(); i++) {
                                    System.out.println(Thread.currentThread().getName() + "> " + (i % 2 == 0
                                            ? "Position de l'élément modifié: "
                                            : "Valeur de l'élément modifié: ") + l.get(i));
                                }

                                try {
                                    Update(resultSet, l);
                                    resultSet = null;
                                    bd.commit();
                                    bd.setTransactionIsolationLevel(Connection.TRANSACTION_READ_COMMITTED);
                                    rep = new ReponseLUGAP(TypeReponseLUGAP.OK);
                                } catch (SQLException | UpdateException e) {
                                    e.printStackTrace(System.out);
                                    rep = new ReponseLUGAP(TypeReponseLUGAP.NOT_OK);
                                }
                            } else
                                rep = new ReponseLUGAP(TypeReponseLUGAP.NOTLOGGED);
                            Reponse(oos, rep);
                            break;
                        case Update_mobile:
                            if (log) {
                                List<List<String>> lls = (List<List<String>>) req.getParam();
                                for (List<String> ls : lls) {
                                    try {
                                        System.out.println("Mise à jour du bagage numéro " + ls.get(0));
                                        Update(resultSet, ls);
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                        Reponse(oos, new ReponseLUGAP(TypeReponseLUGAP.NOT_OK));
                                        return;
                                    } catch (UpdateException e) {
                                        e.printStackTrace();
                                        Reponse(oos, new ReponseLUGAP(TypeReponseLUGAP.NOT_OK));
                                        return;
                                    }
                                }
                                resultSet = null;
                                try {
                                    bd.commit();
                                    bd.setTransactionIsolationLevel(Connection.TRANSACTION_READ_COMMITTED);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                rep = new ReponseLUGAP(TypeReponseLUGAP.OK);
                            } else rep = new ReponseLUGAP(TypeReponseLUGAP.NOTLOGGED);
                            Reponse(oos, rep);
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        };
    }

    private void HeaderRunnable(String type, String from) {
        System.out.println();
        System.out.println(Thread.currentThread().getName() + "> Traitement d'une requête de " + type + " de " + from);
    }

    private void Reponse(final OutputStream outputStream, ReponseLUGAP rep) {
        System.out.println(Thread.currentThread().getName() + "> Réponse: " + rep);
        try {
            ((ObjectOutputStream) outputStream).writeObject(rep);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void Update(ResultSet rs, LinkedList<Object> list) throws UpdateException, SQLException {
        rs.beforeFirst();
        while (rs.next()) if (rs.getString(1).contentEquals(list.get(0).toString())) break;

        if (rs.isAfterLast()) throw new UpdateException();
        for (int i = 0; i < (list.size() - 1) / 2; i++) {
            switch ((int) list.get(i * 2 + 2)) {
                case 4:
                    rs.updateInt(5, Boolean.valueOf(String.valueOf(list.get(i * 2 + 1)))
                            ? 1
                            : 0);
                    break;
                case 5:
                    rs.updateObject(6, list.get(i * 2 + 1));
                    break;
                case 6:
                    rs.updateInt(7, Boolean.valueOf(String.valueOf(list.get(i * 2 + 1)))
                            ? 1
                            : 0);
                    break;
                case 7:
                    rs.updateObject(8, list.get(i * 2 + 1));
                    break;
                default:
                    throw new UpdateException();
            }
        }
        rs.updateRow();
    }

    private void Update(ResultSet rs, List<String> list) throws SQLException, UpdateException {
        rs.beforeFirst();
        while (rs.next()) if (rs.getString(1).contentEquals(list.get(0))) break;
        if (rs.isAfterLast()) throw new UpdateException();
        System.out.println("Update: " + list.get(0) + " -> " + list.get(1));
        rs.updateString(6, Boolean.valueOf(list.get(1))
                ? "C"
                : "N");
        rs.updateRow();
    }
}
