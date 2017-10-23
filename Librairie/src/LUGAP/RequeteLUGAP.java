package LUGAP;

import LUGAP.NetworkObject.Login;
import LUGAP.NetworkObject.Table;
import ServeurClientLog.Interfaces.Requete;
import Tools.Bd;
import Tools.BdType;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Random;

public class RequeteLUGAP implements Requete {
    private static final ThreadLocal<Integer> Rand = ThreadLocal.withInitial(() -> 0);
    private static final ThreadLocal<Boolean> Logged = ThreadLocal.withInitial(() -> false);
    private static MessageDigest Md;

    static {
        //addProvider en local, car on travaille sur différentes machines
        Security.addProvider(new BouncyCastleProvider());
        try {
            Md = MessageDigest.getInstance("SHA-1", "BC");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private TypeRequeteLUGAP Type = null;
    private Serializable Param = null;
    private Bd MySql = null;
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

    public static byte[] hashPassword(String password, int challenge) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeUTF(password);
        dos.writeInt(challenge);
        return Md.digest(baos.toByteArray());
    }

    public Serializable getParam() {
        return Param;
    }

    private void setBd() throws IOException, SQLException {
        MySql = new Bd(BdType.MySql);
    }

    private void setBd(Bd base) {
        MySql = base;
    }

    @Override
    public Runnable createRunnable(final ObjectOutputStream oosClient) {
        Runnable retour = null;
        this.setBd(Bd.getMySql());
        System.out.println(Thread.currentThread().getName() + "> Message " + From);
        switch (this.Type) {
            case TryConnect:
                retour = () -> {
                    System.out.println(Thread.currentThread().getName() + "> Traitement d'une requête trylogin de " + From);
                    Rand.set(new Random().nextInt());
                    try {
                        System.out.println(Thread.currentThread().getName() + "> Digest salé généré: " + Rand.get());
                        oosClient.writeObject(new ReponseLUGAP(TypeReponseLUGAP.OK, Rand.get()));
                    } catch (IOException e) {
                        e.printStackTrace();
                        //TODO Gestion erreurs
                    }
                };
                break;
            case Login:
                retour = () -> {
                    System.out.println(Thread.currentThread().getName() + "> Traitement d'une requête login de " + From);
                    try {
                        ResultSet rs = MySql.Select("Login");
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
                        ReponseLUGAP reponse = new ReponseLUGAP(TypeReponseLUGAP.UNKNOWN_LOGIN);
                        while (rs.next()) {
                            if (rs.getString(user).equals(((Login) Param).getUser())) {
                                byte envoye[] = ((Login) Param).getPassword();
                                byte pass[] = hashPassword(rs.getString(password), Rand.get());
                                System.out.println(Thread.currentThread().getName() + "> Utilisateur trouvé");

                                System.out.println(Thread.currentThread().getName() + "> Hash en string: ");
                                System.out.println(Thread.currentThread().getName() + "> Hash envoyé:           " + new String(envoye));
                                System.out.println(Thread.currentThread().getName() + "> Hash de l'utilisateur: " + new String(pass));

                                System.out.println(Thread.currentThread().getName() + "> Hash en tableau: ");
                                System.out.println(Thread.currentThread().getName() + "> Hash envoyé:           " + Arrays.toString(envoye));
                                System.out.println(Thread.currentThread().getName() + "> Hash de l'utilisateur: " + Arrays.toString(pass));

                                if (MessageDigest.isEqual(pass, ((Login) Param).getPassword())) {
                                    reponse = new ReponseLUGAP(TypeReponseLUGAP.LOG, MySql.SelectLogUser(rs.getString(user)));
                                    System.out.println(Thread.currentThread().getName() + "> Mot de passe correct");
                                    Logged.set(true);
                                    break;
                                } else {
                                    reponse = new ReponseLUGAP(TypeReponseLUGAP.BAD_PASSWORD);
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
                retour = () -> {
                    System.out.println(Thread.currentThread().getName() + "> Traitement d'une requête de logout de " + From);
                    Logged.set(false);
                    try{
                        oosClient.writeObject(new ReponseLUGAP(TypeReponseLUGAP.OK));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                };
                break;
            case Request_Vols:
                retour = () -> {
                    System.out.println(Thread.currentThread().getName() + "> Traitement d'une requête Request_vols de " + From);
                    try {
                        oosClient.writeObject(new ReponseLUGAP(TypeReponseLUGAP.OK, Bd.toTable(MySql.SelectTodayVols())));
                    } catch (SQLException e) {
                        System.out.println(Thread.currentThread().getName() + "> SQLException: " + e.getMessage());
                        try {
                            oosClient.writeObject(new ReponseLUGAP(TypeReponseLUGAP.NOT_OK));
                        } catch (IOException e1) {
                            System.out.println(Thread.currentThread().getName() + "> IOException: " + e.getMessage());
                        }
                    } catch (IOException e) {
                        System.out.println(Thread.currentThread().getName() + "> IOException: " + e.getMessage());
                        try {
                            oosClient.writeObject(new ReponseLUGAP(TypeReponseLUGAP.NOT_OK));
                        } catch (IOException e1) {
                            System.out.println(Thread.currentThread().getName() + "> IOException: " + e.getMessage());
                        }
                    }
                };
                break;
            case Request_Bagages_Vol:
                retour = () -> {
                    try {
                        Table t = Bd.toTable(MySql.SelectBagageVol((String) getParam()));
                        oosClient.writeObject(new ReponseLUGAP(TypeReponseLUGAP.OK, t));
                    } catch (SQLException e) {
                        System.out.println(Thread.currentThread().getName() + "> SQLException: " + e.getMessage());
                        try {
                            oosClient.writeObject(new ReponseLUGAP(TypeReponseLUGAP.NOT_OK));
                        } catch (IOException e1) {
                            System.out.println(Thread.currentThread().getName() + "> IOException: " + e.getMessage());
                        }
                    } catch (IOException e) {
                        System.out.println(Thread.currentThread().getName() + "> IOException: " + e.getMessage());
                        try {
                            oosClient.writeObject(new ReponseLUGAP(TypeReponseLUGAP.NOT_OK));
                        } catch (IOException e1) {
                            System.out.println(Thread.currentThread().getName() + "> IOException: " + e.getMessage());
                        }
                    }
                };
                break;
        }
        return retour;
    }

    @Override
    public boolean isLogin() {
        return this.Type == TypeRequeteLUGAP.Login || this.Type == TypeRequeteLUGAP.TryConnect;
    }

    @Override
    public boolean loginSucced() {
        return Logged.get();
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
