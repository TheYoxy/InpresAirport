package Tools.Bd;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import NetworkObject.Bean.Table;
import NetworkObject.Bean.Voyageur;
import Tools.VolField;

public class Bd {

    private Connection Connection;

    /**
     * @param type
     * @throws IOException
     * @throws SQLException
     */
    public Bd(BdType type)
    throws IOException, SQLException {
        this.Connection = createConnection(type);
    }

    /**
     * @param type
     * @return
     * @throws SQLException
     * @throws IOException
     */
    public synchronized static Connection createConnection(BdType type)
    throws SQLException, IOException {
        String confFile = Bd.class.getResource("Properties").getFile() + File.separator;
        switch (type) {
            case MySql:
                confFile += "mysql.properties";
                break;
            case Oracle:
                confFile += "oracle.properties";
                break;
            default:
                System.exit(-1);
        }
        Properties p = new Properties();
        p.load(new FileReader(new File(confFile)));
        String url    = p.getProperty("url");
        String user   = p.getProperty("user");
        String passwd = p.getProperty("password");
        try {
            Class.forName(p.getProperty("driver")).newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(url, user, passwd);
    }

    /**
     * @param type
     * @param lockTime
     * @throws IOException
     * @throws SQLException
     */
    public Bd(BdType type, int lockTime)
    throws IOException, SQLException {
        this.Connection = createConnection(type);
        setInnoDB_Lock_Time(lockTime);
        this.Connection.setAutoCommit(false);
        setTransactionIsolationLevel(Connection.TRANSACTION_REPEATABLE_READ);
    }

    private synchronized void setInnoDB_Lock_Time(int time)
    throws SQLException {
        Statement s = this.Connection.createStatement();
        s.execute("SET SESSION innodb_lock_wait_timeout = " + (time - 1));
    }

    public void setTransactionIsolationLevel(int level)
    throws SQLException {
        Connection.setTransactionIsolation(level);
    }

    /**
     * @param rs
     * @throws SQLException
     */
    public static void afficheResultSet(ResultSet rs)
    throws SQLException {
        ResultSetMetaData rsmf = rs.getMetaData();
        StringBuilder     sb   = new StringBuilder();
        for (int i = 1; i <= rsmf.getColumnCount(); i++) {
            sb.append(rsmf.getColumnName(i)).append("|");
        }
        sb.deleteCharAt(sb.length() - 1);
        System.out.println(sb);
        while (rs.next()) {
            sb = new StringBuilder();
            for (int i = 1; i <= rsmf.getColumnCount(); i++) {
                sb.append(rs.getObject(i)).append("|");
            }
            sb.deleteCharAt(sb.length() - 1);
            System.out.println(sb);
        }
    }

    public static void main(String[] argv) {
        try {
            Bd b = new Bd(BdType.MySql, 5);
            b.setTransactionIsolationLevel(java.sql.Connection.TRANSACTION_SERIALIZABLE);
            Bd c = new Bd(BdType.MySql, 5);
            c.setTransactionIsolationLevel(java.sql.Connection.TRANSACTION_SERIALIZABLE);
            Bd a[] = new Bd[]{b, c};
            for (int i = 0, aLength = a.length; i < aLength; i++) {
                final Bd  anA = a[i];
                final int j   = i;
                new Thread(() -> {
                    long debut = System.currentTimeMillis();
                    try {
                        System.out.println(Thread.currentThread().getName() + "> Requête ");
                        String id = ((Integer) (j + 1)).toString();
                        //                        String id = "1";
                        System.out.println(Thread.currentThread().getName() + "> Id: " + id);
                        ResultSet rs = anA.selectBagageVol(id);
                        System.out.println(Thread.currentThread().getName() + "> select passé");
                        Table t = toTable(rs);
                        System.out.println(t);
                        System.out.println(Thread.currentThread().getName() + "> Fin requête");
                    } catch (SQLException e) {
                        e.printStackTrace(System.out);
                        System.out.println(Thread.currentThread().getName() + "> SQLException code: " + e.getErrorCode());
                    }
                    long fin = System.currentTimeMillis();
                    System.out.println(Thread.currentThread().getName() + "> Début: " + debut + " ms");
                    System.out.println(Thread.currentThread().getName() + "> Fin: " + fin + " ms");
                    System.out.println(Thread.currentThread().getName() + "> Temps d'execution: " + (fin - debut) + " ms");
                }).start();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param numVol Numéro du vol à sélectionner
     * @return Un objet ResultSet contenant les résultats de la requête
     * @throws SQLException Exceptions qui sont générées par la BD
     */
    public synchronized ResultSet selectBagageVol(String numVol)
    throws SQLException {
        PreparedStatement s = Connection.prepareStatement("SELECT Bagages.* FROM Bagages NATURAL JOIN Billets NATURAL JOIN Vol WHERE NumeroVol = ? FOR UPDATE", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
        s.setString(1, numVol);
        return s.executeQuery();
    }

    /**
     * @param rs
     * @return
     * @throws SQLException
     */
    public static Table toTable(ResultSet rs)
    throws SQLException {
        if (rs == null) return null;
        Vector<String>    title = new Vector<>();
        ResultSetMetaData rsmd  = rs.getMetaData();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            title.add(rsmd.getColumnName(i));
        }
        Vector<Vector<String>> champs = new Vector<>();
        rs.beforeFirst();
        while (rs.next()) {
            Vector<String> temp = new Vector<>();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                try {
                    //Sous MySql, n'importe quel type de données peut être directement converti en String
                    //Le passage via le type ne fonctionne pas
                    switch (rsmd.getColumnTypeName(i)) {
                        case "TINYINT":
                            temp.add(String.valueOf(rs.getBoolean(i)));
                            break;
                        case "FLOAT":
                            temp.add(String.valueOf(rs.getFloat(i)));
                            break;
                        default:
                            temp.add(rs.getString(i));
                            break;
                    }
                } catch (SQLException e) {
                    System.out.println(Thread.currentThread().getName() + "> Exception: " + e.getMessage());
                }
            }
            champs.add(temp);
        }
        return new Table(title, champs);
    }

    /**
     * Fonction qui va prendre toutes les valeurs d'un noeud d'un résultset, et les mettre dans une liste
     *
     * @param rs Resultset à analyser
     * @return null si le RésultSet est fermé<br/>
     * List des valeurs du noeud sur lequel pointe le résultset
     * @throws SQLException Toute exceptions pouvant être lancée
     */
    public static List toList(ResultSet rs)
    throws SQLException {
        List l = new LinkedList();
        if (rs.isClosed()) return null;
        else if (rs.isBeforeFirst()) rs.next();
        else if (rs.isAfterLast()) return null;
        ResultSetMetaData rsmd = rs.getMetaData();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) l.add(rs.getObject(i));
        return l;
    }

    public synchronized int ajoutBillets(String vol, String billet, int place, String facture, int voyageur)
    throws SQLException {
        PreparedStatement ps = Connection.prepareStatement("INSERT INTO Billets VALUES(?,?,?,?,?)");
        ps.setString(1, billet);
        ps.setString(2, vol);
        ps.setInt(3, place);
        ps.setString(4, facture);
        ps.setInt(5, voyageur);
        return ps.executeUpdate();
    }

    public synchronized int ajoutPlacesLibres(String numVol, int nbPlaces)
    throws SQLException {
        PreparedStatement ps = Connection.prepareStatement("UPDATE Vol SET PlacesDisponible = PlacesDisponible + ? WHERE NumeroVol LIKE ?");
        ps.setInt(1, nbPlaces);
        ps.setString(2, numVol);
        return ps.executeUpdate();
    }

    public synchronized int ajoutVoyageur(Voyageur v)
    throws SQLException {
        PreparedStatement ps = Connection.prepareStatement("INSERT INTO Voyageur(nom, prenom, naissance) VALUE (?,?,?)");
        ps.setString(1, v.getNom());
        ps.setString(2, v.getPrenom());
        ps.setDate(3, Date.valueOf(v.getNaissance()));
        return ps.executeUpdate();
    }

    public synchronized void close()
    throws SQLException {
        close(false);
    }

    public synchronized void close(boolean commit)
    throws SQLException {
        if (commit) {
            Connection.commit();
        }
        Connection.close();
    }

    public synchronized void commit()
    throws SQLException {
        Connection.commit();
    }

    public Connection getConnection() {
        return Connection;
    }

    public synchronized int insertAchat(String username, String vol, String places, double prix)
    throws SQLException {
        PreparedStatement ps = Connection.prepareStatement("INSERT INTO Facture(Username, NumeroVol, nbPlaces,prix) VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, username);
        ps.setString(2, vol);
        ps.setInt(3, Integer.parseInt(places));//a mettre en int !
        ps.setDouble(4, prix);
        int i = ps.executeUpdate();
        if (i == 0) return 0;
        else {
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            return rs.getInt(1);
        }
    }

    public synchronized String insertBillet(String numVol, int idFacture)
    throws SQLException {
        PreparedStatement ps = Connection.prepareStatement("SELECT NumeroBillet FROM Billets WHERE NumeroVol LIKE ?");
        ps.setString(1, numVol);
        ResultSet    r = ps.executeQuery();
        List<String> l = new LinkedList<>();
        while (r.next()) l.add(r.getString(1));
        l.sort(Comparator.naturalOrder());

        String numbillet = (l.size() != 0
                            ? String.format("%06d", Integer.parseInt(l.get(l.size() - 1).split("-")[0]) + 1) + "-" + numVol
                            : "000001-" + numVol);
        ps = Connection.prepareStatement("INSERT INTO Billets(NumeroBillet, NumeroVol,idFacture) VALUES (?,?,?)");
        ps.setString(1, numbillet);
        ps.setString(2, numVol);
        ps.setInt(3, idFacture);
        return ps.executeUpdate() != 0
               ? numbillet
               : null;
    }

    public synchronized int insertCarte(String numeroCarte, Double soldeDepart, int idVoyageur)
    throws SQLException {
        PreparedStatement ps = Connection.prepareStatement("INSERT INTO Carte VALUE (?,?,?)");
        ps.setString(1, numeroCarte);
        ps.setDouble(2, soldeDepart);
        ps.setInt(3, idVoyageur);
        return ps.executeUpdate();
    }

    public synchronized int insertFacture(String idFacture, Timestamp instant, String NumeroVol, int nbPlaces)
    throws SQLException {
        PreparedStatement ps = Connection.prepareStatement("INSERT INTO bd_airport.Facture(idFacture, instant, NumeroVol, nbPlaces) VALUES (?,?,?,?)");
        ps.setString(1, idFacture);
        ps.setTimestamp(2, instant);
        ps.setString(3, NumeroVol);
        ps.setInt(4, nbPlaces);
        return ps.executeUpdate();
    }

    public synchronized boolean insertReservation(String username, String vol, String nbrPlaces, String time)
    throws SQLException {
        PreparedStatement ps = Connection.prepareStatement("INSERT INTO Reservation(Username, NumeroVol, nbPlaces, timeReservation) VALUES (?,?,?,?)");
        Timestamp         ts = Timestamp.valueOf(time);

        ps.setString(1, username);
        ps.setString(2, vol);
        ps.setString(3, nbrPlaces);//a mettre en int !
        ps.setString(4, time); //A mettre en time
        return ps.executeUpdate() != 0;
    }

    public synchronized boolean insertUser(String username, String password, String mail)
    throws SQLException {
        PreparedStatement ps = Connection.prepareStatement("INSERT INTO WebUsers(Username, Password, Mail) VALUES (?,?,?)");
        ps.setString(1, username);
        ps.setString(2, password);
        ps.setString(3, mail);
        return ps.executeUpdate() != 0;
    }

    public synchronized int payement(String numeroCarte, double value)
    throws SQLException {
        PreparedStatement ps = Connection.prepareStatement("UPDATE Carte SET solde = solde - ? WHERE numeroCarte = ?");
        ps.setDouble(1, value);
        ps.setString(2, numeroCarte);
        return ps.executeUpdate();
    }

    public synchronized void rollback()
    throws SQLException {
        Connection.rollback();
    }

    public synchronized void rollback(Savepoint s)
    throws SQLException {
        Connection.rollback(s);
    }

    public synchronized ResultSet select(String table)
    throws SQLException {
        //La table doit être hard codée
        return Connection.createStatement().executeQuery("select * from " + table);
    }

    public synchronized ResultSet selectAlimCarte(String numeroCarte)
    throws SQLException {
        PreparedStatement ps = Connection.prepareStatement("SELECT solde FROM Carte WHERE numeroCarte = ?");
        ps.setString(1, numeroCarte);
        return ps.executeQuery();
    }

    public synchronized ResultSet selectBillets(String numVol)
    throws SQLException {
        PreparedStatement ps = Connection.prepareStatement("SELECT NumeroBillet FROM bd_airport.Billets NATURAL JOIN Vol WHERE NumeroVol LIKE ? ORDER BY NumeroBillet");
        ps.setString(1, numVol);
        return ps.executeQuery();
    }

    public synchronized ResultSet selectLastTransaction()
    throws SQLException {
        return Connection.prepareStatement("SELECT * FROM Transactions ORDER BY instant DESC LIMIT 1").executeQuery();
    }

    public synchronized ResultSet selectLieu(String numVol)
    throws SQLException {
        PreparedStatement ps = Connection.prepareStatement("SELECT Lieu FROM Vol WHERE NumeroVol LIKE ?");
        ps.setString(1, numVol);
        return ps.executeQuery();
    }

    public synchronized String selectLogUser(String User)
    throws SQLException {
        PreparedStatement s = Connection.prepareStatement("SELECT Nom,Prenom FROM Agents NATURAL JOIN Login WHERE Username = ? AND Poste = 'Bagagiste'");
        s.setString(1, User);
        if (s.execute()) {
            Vector<Vector<String>> v = toTable(s.getResultSet()).getChamps();
            if (!v.isEmpty()) {
                final Vector<String> strings = v.elementAt(0);
                return strings.elementAt(0) + " " + strings.elementAt(1);
            }
            return "";
        }
        else {
            return "";
        }
    }

    public synchronized ResultSet selectPlacesVols(String numVol)
    throws SQLException {
        PreparedStatement ps = Connection.prepareStatement("SELECT PlacesDisponible FROM Vol WHERE NumeroVol LIKE ? ");
        ps.setString(1, numVol);
        return ps.executeQuery();
    }

    public synchronized ResultSet selectTodayVols()
    throws SQLException {
        return Connection.createStatement().executeQuery("SELECT NumeroVol,Lieu,HeureDepart,Prix,Description FROM Vol WHERE HeureDepart BETWEEN CURRENT_DATE AND CURRENT_DATE + 1");
    }

    public synchronized ResultSet selectTransaction(String id)
    throws SQLException {
        PreparedStatement ps = Connection.prepareStatement("SELECT * FROM Transactions WHERE idFacture LIKE ?");
        ps.setString(1, id);
        ps.execute();
        return ps.getResultSet();
    }

    public synchronized ResultSet selectUserBillet(String billet)
    throws SQLException {
        PreparedStatement ps = Connection.prepareStatement("SELECT Prenom FROM WebUsers NATURAL JOIN Facture NATURAL JOIN Billets WHERE NumeroBillet LIKE ?");
        ps.setString(1, billet);
        return ps.executeQuery();
    }

    public synchronized ResultSet selectUserPassword(String user)
    throws SQLException {
        PreparedStatement ps = Connection.prepareStatement("SELECT password,Prenom FROM Login NATURAL JOIN Agents WHERE Username LIKE ?");
        ps.setString(1, user);
        return ps.executeQuery();
    }

    public synchronized ResultSet selectVolReservable(String numVol)
    throws SQLException {
        PreparedStatement ps = Connection.prepareStatement("SELECT * FROM Vol WHERE NumeroVol LIKE ?");
        ps.setString(1, numVol);
        return ps.executeQuery();
    }

    public synchronized ResultSet selectVolReservableNbPlaces(String numVol, int nbPlaces)
    throws SQLException {
        //Il est directement incrémenté
        PreparedStatement ps = Connection.prepareStatement("SELECT * FROM Vol WHERE NumeroVol LIKE ? AND PlacesDisponible >= ?");
        ps.setString(1, numVol);
        ps.setInt(2, nbPlaces);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            suppPlacesReservables(numVol, nbPlaces);
            rs.beforeFirst();
            return rs;
        }
        return null;
    }

    public synchronized int suppPlacesReservables(String numVol, int nbPlaces)
    throws SQLException {
        PreparedStatement ps = Connection.prepareStatement("UPDATE Vol SET PlacesDisponible = PlacesDisponible - ? WHERE NumeroVol LIKE ?");
        ps.setInt(1, nbPlaces);
        ps.setString(2, numVol);
        return ps.executeUpdate();
    }

    public synchronized ResultSet selectVoyageurId(Voyageur v)
    throws SQLException {
        PreparedStatement ps = Connection.prepareStatement("SELECT idVoyageur FROM Voyageur WHERE nom = ? AND prenom = ? AND naissance = ?");
        ps.setString(1, v.getNom());
        ps.setString(2, v.getPrenom());
        ps.setDate(3, Date.valueOf(v.getNaissance()));
        return ps.executeQuery();
    }

    public synchronized ResultSet selectWeekVols()
    throws SQLException {
        return Connection.createStatement().executeQuery("SELECT NumeroVol,Lieu,HeureDepart,Prix,Description FROM Vol WHERE HeureDepart BETWEEN current_date AND current_date + 7");
    }

    public synchronized void setAutoComit(boolean b)
    throws SQLException {
        Connection.setAutoCommit(b);
    }

    public synchronized Savepoint setSavepoint()
    throws SQLException {
        return Connection.setSavepoint();
    }

    /**
     * @param champ
     * @param value
     * @param numBagage
     * @return
     * @throws SQLException
     */
    public synchronized int updateBagage(VolField champ, Object value, String numBagage)
    throws SQLException {
        //Quand on passe via ?, ça ajoute des "" -> Obligé de le passer en dur
        PreparedStatement ps = Connection.prepareStatement("UPDATE Bagages SET " + champ.toString() + " = ? WHERE NumeroBagage = ?");
        switch (champ) {
            case Reception:
            case Verifier:
                ps.setInt(1, Boolean.valueOf(String.valueOf(value))
                             ? 1
                             : 0);
                break;
            case Charger:
            case Remarque:
                ps.setObject(1, value);
                break;
        }
        ps.setString(2, numBagage);
        return ps.executeUpdate();
    }
}
