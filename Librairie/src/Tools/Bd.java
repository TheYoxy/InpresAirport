package Tools;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import LUGAP.NetworkObject.Table;

public class Bd {

    private Connection Connection;

    /**
     * @param type
     * @throws IOException
     * @throws SQLException
     */
    public Bd(BdType type) throws IOException, SQLException {
        this.Connection = createConnection(type);
        this.Connection.setAutoCommit(false);
    }

    /**
     * @param type
     * @param lockTime
     * @throws IOException
     * @throws SQLException
     */
    public Bd(BdType type, int lockTime) throws IOException, SQLException {
        this.Connection = createConnection(type);
        setInnoDB_Lock_Time(lockTime);
        this.Connection.setAutoCommit(false);
    }

    public static void main(String[] argv) {
        try {
            Bd b = new Bd(BdType.MySql, 5);
            b.setTransactionIsolationLevel(java.sql.Connection.TRANSACTION_SERIALIZABLE);
            Bd c = new Bd(BdType.MySql, 5);
            c.setTransactionIsolationLevel(java.sql.Connection.TRANSACTION_SERIALIZABLE);
            Bd a[] = new Bd[]{b, c};
            for (int i = 0, aLength = a.length; i < aLength; i++) {
                final Bd anA = a[i];
                final int j = i;
                new Thread(() -> {
                    long debut = System.currentTimeMillis();
                    try {
                        System.out.println(Thread.currentThread().getName() + "> Requête ");
                        String id = ((Integer) (j + 1)).toString();
//                        String id = "1";
                        System.out.println(Thread.currentThread().getName() + "> Id: " + id);
                        ResultSet rs = anA.SelectBagageVol(id);
                        System.out.println(Thread.currentThread().getName() + "> Select passé");
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
     * Fonction qui va prendre toutes les valeurs d'un noeud d'un résultset, et les mettre dans une liste
     *
     * @param rs Resultset à analyser
     * @return null si le RésultSet est fermé<br/>
     * List des valeurs du noeud sur lequel pointe le résultset
     * @throws SQLException Toute exceptions pouvant être lancée
     */
    public static List ToList(ResultSet rs) throws SQLException {
        List l = new LinkedList();
        if (rs.isClosed())
            return null;
        else if (rs.isBeforeFirst())
            rs.next();
        else if (rs.isAfterLast())
            return null;
        ResultSetMetaData rsmd = rs.getMetaData();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) l.add(rs.getObject(i));
        return l;
    }

    /**
     * @param type
     * @return
     * @throws SQLException
     * @throws IOException
     */
    public synchronized static Connection createConnection(BdType type) throws SQLException, IOException {
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
        String url = p.getProperty("url");
        String user = p.getProperty("user");
        String passwd = p.getProperty("password");
        try {
            Class.forName(p.getProperty("driver")).newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(url, user, passwd);
    }

    /**
     * @param rs
     * @throws SQLException
     */
    public static void AfficheResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmf = rs.getMetaData();
        StringBuilder sb = new StringBuilder();
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

    /**
     * @param rs
     * @return
     * @throws SQLException
     */
    public static Table toTable(ResultSet rs) throws SQLException {
        Vector<String> title = new Vector<>();
        ResultSetMetaData rsmd = rs.getMetaData();
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
                    if (rsmd.getColumnTypeName(i) == "TINYINT") {
                        temp.add(String.valueOf(rs.getBoolean(i)));
                    } else if (rsmd.getColumnTypeName(i) == "FLOAT") {
                        temp.add(String.valueOf(rs.getFloat(i)));
                    } else {
                        temp.add(rs.getString(i));
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
     * @param isolationLevel
     * @throws SQLException
     */
    public void setTransactionIsolationLevel(int isolationLevel) throws SQLException {
        this.Connection.setTransactionIsolation(isolationLevel);
    }

    private void setInnoDB_Lock_Time(int time) throws SQLException {
        Statement s = this.Connection.createStatement();
        s.execute("SET SESSION innodb_lock_wait_timeout = " + (time - 1));
    }

    /**
     * @param User
     * @return
     * @throws SQLException
     */
    public synchronized String SelectLogUser(String User) throws SQLException {
        PreparedStatement s = Connection.prepareStatement("Select Nom,Prenom FROM Agents NATURAL JOIn Login WHERE Username = ? AND Poste = 'Bagagiste'");
        s.setString(1, User);
        if (s.execute()) {
            final Vector<String> strings = toTable(s.getResultSet()).getChamps().elementAt(0);
            return strings.elementAt(0) + " " + strings.elementAt(1);
        } else {
            return "";
        }
    }

    /**
     * @param numVol Numéro du vol à sélectionner
     * @return Un objet ResultSet contenant les résultats de la requête
     * @throws SQLException Exceptions qui sont générées par la BD
     */
    public synchronized ResultSet SelectBagageVol(String numVol) throws SQLException {
        PreparedStatement s = Connection.prepareStatement("SELECT Bagages.* FROM Bagages NATURAL JOIN Billets NATURAL JOIN Vols WHERE Vols.NumeroVol = ? FOR UPDATE",
                ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_UPDATABLE);
        s.setString(1, numVol);
        return s.executeQuery();
    }

    public synchronized ResultSet SelectVolReservable(String numVol) throws SQLException {
        PreparedStatement ps = Connection.prepareStatement("SELECT * FROM VolReservable WHERE NumeroVol LIKE ?");
        ps.setString(1, numVol);
        return ps.executeQuery();
    }

    public synchronized ResultSet SelectVolReservableNbPlaces(String numVol, int nbPlaces) throws SQLException {
        //Il est directement incrémenté
        PreparedStatement ps = Connection.prepareStatement("SELECT * FROM VolReservable WHERE NumeroVol LIKE ? AND PlacesDisponible >= ? for UPDATE");
        ps.setString(1, numVol);
        ps.setInt(2, nbPlaces);
        ResultSet rs = ps.executeQuery();
        if (rs != null) SuppPlacesReservables(numVol, nbPlaces);
        return rs;
    }

    public synchronized int SuppPlacesReservables(String numVol, int nbPlaces) throws SQLException {
        PreparedStatement ps = Connection.prepareStatement("UPDATE VolReservable set PlacesDisponible = PlacesDisponible - ? where NumeroVol like ?");
        ps.setInt(1, nbPlaces);
        ps.setString(2, numVol);
        return ps.executeUpdate();
    }

    public synchronized int AjoutPlacesLibres(String numVol, int nbPlaces) throws SQLException {
        PreparedStatement ps = Connection.prepareStatement("Update VolReservable set PlacesDisponible = PlacesDisponible + ? where NumeroVol like ?");
        ps.setInt(1, nbPlaces);
        ps.setString(2, numVol);
        return ps.executeUpdate();
    }

    /**
     * @param champ
     * @param value
     * @param numBagage
     * @return
     * @throws SQLException
     */
    public synchronized int UpdateBagage(VolField champ, Object value, String numBagage) throws SQLException {
        //Quand on passe via ?, ça ajoute des "" -> Obligé de le passer en dur
        PreparedStatement ps = Connection.prepareStatement("UPDATE Bagages SET " + champ.toString() + " = ? WHERE NumeroBagage = ?");
        switch (champ) {
            case Reception:
            case Verifier:
                ps.setInt(1, Boolean.valueOf(String.valueOf(value)) ? 1 : 0);
                break;
            case Charger:
            case Remarque:
                ps.setObject(1, value);
                break;
        }
        ps.setString(2, numBagage);
        return ps.executeUpdate();
    }

    public synchronized Savepoint setSavepoint() throws SQLException {
        return Connection.setSavepoint();
    }

    public synchronized void rollback() throws SQLException {
        Connection.rollback();
    }

    public synchronized void rollback(Savepoint s) throws SQLException {
        Connection.rollback(s);
    }

    public synchronized void commit() throws SQLException {
        Connection.commit();
    }

    public synchronized void setAutoComit(boolean b) throws SQLException {
        Connection.setAutoCommit(b);
    }

    public synchronized ResultSet SelectTodayVols() throws SQLException {
        return Connection.createStatement().executeQuery("SELECT * FROM Vols WHERE HeureDepart BETWEEN CURRENT_DATE and CURRENT_DATE + 1");
    }

    public synchronized ResultSet Select(String table) throws SQLException {
        //La table doit être hard codée
        return Connection.createStatement().executeQuery("select * from " + table);
    }

    public synchronized void Close() throws SQLException {
        Close(false);
    }

    public synchronized void Close(boolean commit) throws SQLException {
        if (commit) {
            Connection.commit();
        }
        Connection.close();
    }

    public synchronized boolean InsertUser(String username, String password, String mail) throws SQLException {
        PreparedStatement ps = Connection.prepareStatement("insert into Users(Username, Password, Mail) values (?,?,?)");
        ps.setString(1, username);
        ps.setString(2, password);
        ps.setString(3, mail);
        if (ps.execute()) {
            commit();
            return true;
        }
        return false;
    }

    public synchronized boolean InsertReservation(String username, String vol, String nbrPlaces, String time) throws SQLException {
        PreparedStatement ps = Connection.prepareStatement("insert into Reservation(Username, NumeroVol, nbPlaces, timeReservation) values (?,?,?,?)");
        Timestamp ts = Timestamp.valueOf(time);

        ps.setString(1, username);
        ps.setString(2, vol);
        ps.setString(3, nbrPlaces);//a mettre en int !
        ps.setString(4, time); //A mettre en time
        if (ps.executeUpdate() != 0) {
            commit();
            return true;
        }
        return false;
    }

    public synchronized boolean InsertAchat(String username, String vol, String places) throws SQLException{
        int randomNum = 1 + (int)(Math.random() * 999999);

        PreparedStatement ps = Connection.prepareStatement("insert into Acheter(id,Username, NumeroVol, nbPlaces) values (?,?,?,?)");
        ps.setString(1, Integer.toString(randomNum));
        ps.setString(2,username);
        ps.setString(3,vol);
        ps.setString(4,places);//a mettre en int !
        if (ps.execute()) {
            commit();
            return true;
        }
        return false;
    }

    public synchronized  boolean InsertBillet(String numVol) throws SQLException{
        int randomNum = 1 + (int)(Math.random() * 999);
        String numbillet = numVol + "X" + Integer.toString(randomNum) ;
        //TODO Fix du numéro
        PreparedStatement ps = Connection.prepareStatement("insert into Billets(NumeroBillet, NumeroVol) values (?,?)");
        ps.setString(1,numbillet);
        ps.setString(2, numVol);
        if (ps.execute()) {
            commit();
            return true;
        }
        return false;
    }

    public Connection getConnection() {
        return Connection;
    }
}
