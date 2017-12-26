package TICKMAP;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;

import LUGAP.TypeReponseLUGAP;
import NetworkObject.Login;
import ServeurClientLog.Interfaces.Requete;
import Tools.Bd;
import Tools.BdType;
import Tools.DigestCalculator;

public class RequeteTICKMAP implements Requete {
    private static final long serialVersionUID = 123L;
    private static final ThreadLocal<Integer> CHALLENGE = ThreadLocal.withInitial(() -> 0);
    private static final ThreadLocal<Bd> BD_THREAD_LOCAL = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<Boolean> LOG_STATUS = ThreadLocal.withInitial(() -> false);

    private TypeRequeteTICKMAP Type = null;
    private Serializable Param = null;
    private String From = "";

    public RequeteTICKMAP(TypeRequeteTICKMAP type, Serializable param) {
        this(type);
        this.Param = param;
    }

    private RequeteTICKMAP(TypeRequeteTICKMAP type) {
        this.Type = type;
    }

    public RequeteTICKMAP(TypeRequeteTICKMAP type, Serializable Param, String From) {
        this(type, From);
        this.Param = Param;
    }

    public RequeteTICKMAP(TypeRequeteTICKMAP type, String from) {
        this(type);
        this.From = from;
    }

    public Serializable getParam() {
        return Param;
    }

    private void setBd(Bd base) {
        BD_THREAD_LOCAL.set(base);
    }

    @Override
    public Runnable createRunnable(ObjectOutputStream oosClient) {
        Runnable retour = null;
        switch (this.Type) {
            case TryConnect:
                retour = () -> {
                    CHALLENGE.set(new SecureRandom().nextInt());
                    ReponseTICKMAP rep = new ReponseTICKMAP(TypeReponseTICKMAP.OK, CHALLENGE.get());
                    System.out.println(Thread.currentThread().getName() + "> Digest salé généré: " + CHALLENGE.get());
                    Reponse(oosClient, rep);
                };
                break;
            case Login:
                retour = () -> {
                    ReponseTICKMAP rep;
                    try {
                        OpenBd();
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
                        if (test(user, password)) return;
                        rep = new ReponseTICKMAP(TypeReponseTICKMAP.UNKNOWN_LOGIN);
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
                                    rep = new ReponseTICKMAP(TypeReponseTICKMAP.OK, BD_THREAD_LOCAL.get().SelectLogUser(rs.getString(user)));
                                    System.out.println(Thread.currentThread().getName() + "> Mot de passe correct");
                                    LOG_STATUS.set(true);
                                    break;
                                } else {
                                    rep = new ReponseTICKMAP(TypeReponseTICKMAP.BAD_PASSWORD);
                                    System.out.println(Thread.currentThread().getName() + "> Mot de passe incorrect");
                                    break;
                                }
                            }
                        }
                    } catch (SQLException e) {
                        System.out.println(Thread.currentThread().getName() + "> SQLException: " + e.getMessage());
                        rep = new ReponseTICKMAP(TypeReponseTICKMAP.NOT_OK);
                    } catch (IOException e) {
                        System.out.println(Thread.currentThread().getName() + "> IOException: " + e.getMessage());
                        rep = new ReponseTICKMAP(TypeReponseTICKMAP.NOT_OK);
                    }
                    if (rep.getCode() == TypeReponseLUGAP.UNKNOWN_LOGIN) {
                        System.out.println(Thread.currentThread().getName() + "> Utilisateur introuvable");
                    }
                    Reponse(oosClient, rep);
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
    public boolean isLogin() {
        return this.Type == TypeRequeteTICKMAP.Login || this.Type == TypeRequeteTICKMAP.TryConnect;
    }

    @Override
    public boolean loginSucced() {
        return LOG_STATUS.get();
    }

    @Override
    public boolean isLogout() {
        return this.Type == TypeRequeteTICKMAP.Logout;
    }

    @Override
    public boolean isDisconnect() {
        return this.Type == TypeRequeteTICKMAP.Logout;
    }

    private void Reponse(final ObjectOutputStream oos, ReponseTICKMAP rep) {
        System.out.println(Thread.currentThread().getName() + "> Réponse: " + rep);
        try {
            oos.writeObject(rep);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void OpenBd() throws IOException, SQLException {
        BD_THREAD_LOCAL.set(new Bd(BdType.MySql, 5));
    }

    public static boolean test(int user, int password) {
        if (user == -1) {
            System.out.println(Thread.currentThread().getName() + "> (Server error) User introuvable");
            return true;
        } else if (password == -1) {
            System.out.println(Thread.currentThread().getName() + "> (Server error) Password introuvable");
            return true;
        }
        return false;
    }

    private void HeaderLog() {
        System.out.println();
        System.out.println(Thread.currentThread().getName() + "> Traitement d'une requête de " + Type.toString() + " de " + From);
    }

    private synchronized void CloseBd() throws SQLException {
        if (BD_THREAD_LOCAL.get() != null) {
            BD_THREAD_LOCAL.get().Close(true);
            BD_THREAD_LOCAL.set(null);
        }
    }
}
