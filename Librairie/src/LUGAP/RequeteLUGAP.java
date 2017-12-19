package LUGAP;

import NetworkObject.Login;
import NetworkObject.Table;
import ServeurClientLog.Interfaces.Requete;
import Tools.Bd;
import Tools.BdType;
import Tools.DigestCalculator;
import Tools.VolField;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class RequeteLUGAP implements Requete {
    private static final long serialVersionUID = 123L;
    private static final ThreadLocal<Integer> CHALLENGE = ThreadLocal.withInitial(() -> 0);
    private static final ThreadLocal<Boolean> LOG_STATUS = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<Bd> BD_THREAD_LOCAL = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<ResultSet> RESULT_SET_UPDATE = ThreadLocal.withInitial(() -> null);
    //    private static final ThreadLocal<Integer> CHALLENGE = new ThreadLocal<>();/*ThreadLocal.withInitial(() -> 0);*/
    //    private static final ThreadLocal<Boolean> LOG_STATUS = new ThreadLocal<>();/*ThreadLocal.withInitial(() -> false);*/
    //    private static final ThreadLocal<Bd> BD_THREAD_LOCAL = new ThreadLocal<>();/*ThreadLocal.withInitial(() -> null);*/
    //    private static final ThreadLocal<ResultSet> RESULT_SET_UPDATE = new ThreadLocal<>();/*ThreadLocal.withInitial(() -> null);*/
    //    static{
    //        CHALLENGE.set(0);
    //        LOG_STATUS.set(false);
    //        BD_THREAD_LOCAL.set(null);
    //        RESULT_SET_UPDATE.set(null);
    //    }

    private TypeRequeteLUGAP Type = null;
    private Serializable Param = null;
    private String From = "";

    private RequeteLUGAP(TypeRequeteLUGAP type) {
        this.Type = type;
    }

    public RequeteLUGAP(TypeRequeteLUGAP type, String from) {
        this(type);
        From = from;
    }

    public RequeteLUGAP(TypeRequeteLUGAP type, Serializable Param) {
        this(type);
        this.Param = Param;
    }

    public RequeteLUGAP(TypeRequeteLUGAP type, Serializable param, String from) {
        this(type, from);
        Param = param;
    }

    public Serializable getParam() {
        return Param;
    }

    private void setBd(Bd base) {
        BD_THREAD_LOCAL.set(base);
    }

    @Override
    public Runnable createRunnable(final ObjectOutputStream oosClient) {
        Runnable retour = null;
        switch (this.Type) {
            case TryConnect:
                retour = () -> {
                    HeaderRunnable();
                    CHALLENGE.set(new Random().nextInt());
                    ReponseLUGAP rep = new ReponseLUGAP(TypeReponseLUGAP.OK, CHALLENGE.get());
                    System.out.println(Thread.currentThread().getName() + "> Digest salé généré: " + CHALLENGE.get());
                    RequeteLUGAP.this.Reponse(oosClient, rep);
                };
                break;
            case Login:
                retour = () -> {
                    HeaderRunnable();
                    ReponseLUGAP rep;
                    try {
                        RequeteLUGAP.this.OpenBd();
                        ResultSet rs = BD_THREAD_LOCAL.get().Select("Login");
                        ResultSetMetaData rsmd = rs.getMetaData();
                        int user = -1, password = -1;
                        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                            if (rsmd.getColumnName(i).equals("Username")) {
                                user = i;
                            } else if (rsmd.getColumnName(i).equals("Password")) {
                                password = i;
                            }
                        }
                        if (user == -1) {
                            System.out.println(Thread.currentThread().getName() + "> (Server error) User introuvable");
                            return;
                        } else if (password == -1) {
                            System.out.println(Thread.currentThread().getName() + "> (Server error) Password introuvable");
                            return;
                        }
                        rep = new ReponseLUGAP(TypeReponseLUGAP.UNKNOWN_LOGIN);
                        while (rs.next()) {
                            if (rs.getString(user).equals(((Login) Param).getUser())) {
                                byte envoye[] = ((Login) Param).getPassword();
                                byte pass[] = DigestCalculator.hashPassword(rs.getString(password), CHALLENGE.get());
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

                                if (MessageDigest.isEqual(pass, ((Login) Param).getPassword())) {
                                    rep = new ReponseLUGAP(TypeReponseLUGAP.LOG, BD_THREAD_LOCAL.get().SelectLogUser(rs.getString(user)));
                                    System.out.println(Thread.currentThread().getName() + "> Mot de passe correct");
                                    LOG_STATUS.set(true);
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
                    RequeteLUGAP.this.Reponse(oosClient, rep);
                };
                break;
            case Logout:
                retour = () -> {
                    HeaderRunnable();
                    LOG_STATUS.set(false);
                    ReponseLUGAP rep;
                    try {
                        RequeteLUGAP.this.CloseBd();
                        rep = new ReponseLUGAP(TypeReponseLUGAP.OK);
                    } catch (SQLException e) {
                        e.printStackTrace(System.out);
                        rep = new ReponseLUGAP(TypeReponseLUGAP.NOT_OK);
                    }
                    RequeteLUGAP.this.Reponse(oosClient, rep);
                };
                break;
            case Disconnect:
                retour = () -> {
                    System.out.println();
                    System.out.println("Pas implémenté");
                };
                break;
            case Request_Vols:
                retour = () -> {
                    HeaderRunnable();
                    ReponseLUGAP rep;
                    try {
                        Table t = Bd.toTable(BD_THREAD_LOCAL.get().SelectTodayVols());
                        rep = new ReponseLUGAP(TypeReponseLUGAP.OK, t);
                    } catch (SQLException e) {
                        System.out.println(Thread.currentThread().getName() + "> SQLException: " + e.getMessage());
                        rep = new ReponseLUGAP(TypeReponseLUGAP.NOT_OK);
                    }
                    RequeteLUGAP.this.Reponse(oosClient, rep);
                };
                break;
            case Request_Bagages_Vol:
                retour = () -> {
                    HeaderRunnable();
                    ReponseLUGAP rep;
                    try {
                        BD_THREAD_LOCAL.get().setTransactionIsolationLevel(Connection.TRANSACTION_SERIALIZABLE);
                        final String vol = (String) Param;
                        System.out.println(Thread.currentThread().getName() + "> Id du vol demandé: " + vol);
                        ResultSet s = BD_THREAD_LOCAL.get().SelectBagageVol(vol);
                        rep = new ReponseLUGAP(TypeReponseLUGAP.OK, Bd.toTable(s));
                        RESULT_SET_UPDATE.set(s);
                    } catch (SQLException e) {
                        if (e.getErrorCode() == 1205) rep = new ReponseLUGAP(TypeReponseLUGAP.SQL_LOCK);
                        else {
                            rep = new ReponseLUGAP(TypeReponseLUGAP.NOT_OK);
                            System.out.println(Thread.currentThread().getName() + "> SQLException: " + e.getMessage());
                            System.out.println(Thread.currentThread().getName() + "> SQLException code: " + e.getErrorCode());
                        }
                    }
                    RequeteLUGAP.this.Reponse(oosClient, rep);
                };
                break;
            case Update_Bagage_Vol:
                retour = () -> {
                    HeaderRunnable();
                    ReponseLUGAP rep;
                    LinkedList<Object> l = (LinkedList<Object>) Param;
                    //Blindage
                    if (l.size() < 3 && l.size() % 2 == 0) {
                        System.out.println(Thread.currentThread().getName() + "> Taille incohérente");
                        rep = new ReponseLUGAP(TypeReponseLUGAP.NOT_OK);
                        RequeteLUGAP.this.Reponse(oosClient, rep);
                        return;
                    }

                    System.out.println(Thread.currentThread().getName() + "> Clef primaire de l'objet modifié: " + l.get(0));
                    for (int i = 1; i < l.size(); i++) {
                        System.out.println(Thread.currentThread().getName() + "> " + (i % 2 == 0
                                                                                      ? "Position de l'élément modifié: "
                                                                                      : "Valeur de l'élément modifié: ") + l.get(i));
                    }

                    try {
                        RequeteLUGAP.this.Update(RESULT_SET_UPDATE.get(), l);
                        RESULT_SET_UPDATE.set(null);
                        BD_THREAD_LOCAL.get().commit();
                        rep = new ReponseLUGAP(TypeReponseLUGAP.OK);
                    } catch (SQLException | UpdateException e) {
                        e.printStackTrace(System.out);
                        rep = new ReponseLUGAP(TypeReponseLUGAP.NOT_OK);
                    }
                    RequeteLUGAP.this.Reponse(oosClient, rep);
                };
                break;
            case Update_mobile:
                retour = () -> {
                    HeaderRunnable();
                    ReponseLUGAP rep;
                    List<List<String>> lls = (List<List<String>>) Param;
                    for (List<String> ls : lls) {
                        try {
                            System.out.println("Mise à jour du bagage numéro " + ls.get(0));
                            RequeteLUGAP.this.Update(RESULT_SET_UPDATE.get(), ls);
                        } catch (SQLException e) {
                            e.printStackTrace();
                            RequeteLUGAP.this.Reponse(oosClient, new ReponseLUGAP(TypeReponseLUGAP.NOT_OK));
                            return;
                        } catch (UpdateException e) {
                            e.printStackTrace();
                            RequeteLUGAP.this.Reponse(oosClient, new ReponseLUGAP(TypeReponseLUGAP.NOT_OK));
                            return;
                        }
                    }
                    RESULT_SET_UPDATE.set(null);
                    try {
                        BD_THREAD_LOCAL.get().commit();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    rep = new ReponseLUGAP(TypeReponseLUGAP.OK);
                    RequeteLUGAP.this.Reponse(oosClient, rep);
                };
                break;
        }
        return retour;
    }

    private void HeaderRunnable() {
        System.out.println();
        System.out.println(Thread.currentThread().getName() + "> Traitement d'une requête de " + Type.toString() + " de " + From);
    }

    private void Reponse(final ObjectOutputStream oos, ReponseLUGAP rep) {
        System.out.println(Thread.currentThread().getName() + "> Réponse: " + rep);
        try {
            oos.writeObject(rep);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void SqlUpdate(LinkedList<Object> l) throws UpdateException {
        for (int i = 0; i < (l.size() - 1) / 2; i++) {
            VolField champ;
            switch ((int) l.get(i * 2 + 2)) {
                case 4:
                    champ = VolField.Reception;
                    break;
                case 5:
                    champ = VolField.Charger;
                    break;
                case 6:
                    champ = VolField.Verifier;
                    break;
                case 7:
                    champ = VolField.Remarque;
                    break;
                default:
                    throw new UpdateException();
            }
            try {
                System.out.println(Thread.currentThread().getName() + "> Update de " + champ.name() + "(" + l.get(i * 2 + 2) + ") = " + l.get(i * 2 + 1));
                BD_THREAD_LOCAL.get().UpdateBagage(champ, l.get(i * 2 + 1), (String) l.get(0));
            } catch (SQLException e) {
                e.printStackTrace(System.out);
            }
        }
    }

    private synchronized void OpenBd() throws IOException, SQLException {
        BD_THREAD_LOCAL.set(new Bd(BdType.MySql, 5));
    }

    private synchronized void CloseBd() throws SQLException {
        if (BD_THREAD_LOCAL.get() != null) {
            BD_THREAD_LOCAL.get().Close(true);
            BD_THREAD_LOCAL.set(null);
        }
    }

    @Override
    public boolean isLogin() {
        return this.Type == TypeRequeteLUGAP.Login || this.Type == TypeRequeteLUGAP.TryConnect;
    }

    @Override
    public boolean loginSucced() {
        return LOG_STATUS.get();
    }

    @Override
    public boolean isLogout() {
        return this.Type == TypeRequeteLUGAP.Logout;
    }

    @Override
    public boolean isDisconnect() {
        return this.Type == TypeRequeteLUGAP.Disconnect;
    }
}
