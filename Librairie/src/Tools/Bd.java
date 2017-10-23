package Tools;

import LUGAP.NetworkObject.Table;
import com.sun.istack.internal.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.Vector;

import static java.sql.Connection.TRANSACTION_SERIALIZABLE;

public class Bd {
    private static Bd MySql;

    static {
        try {
            MySql = new Bd(BdType.MySql);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private Connection Connection;

    public Bd(BdType type) throws IOException, SQLException {
        this.Connection = createConnection(type);
        this.Connection.setAutoCommit(false);
        this.Connection.setTransactionIsolation(TRANSACTION_SERIALIZABLE);
    }

    public static Bd getMySql() {
        return MySql;
    }

    public static void main(String[] argv) throws IOException, SQLException {
        Bd b = new Bd(BdType.MySql);
        AfficheResultSet(b.Select("Login"));
    }

    public static Connection createConnection(BdType type) throws SQLException, IOException {
        String name = "";
        String confFile = "";
        switch (type) {
            case MySql:
                name = "MySql";
                confFile = "mysql.properties";
                break;
            case Oracle:
                name = "Oracle";
                confFile = "oracle.properties";
                break;
        }
        Properties p = new Properties();
        p.load(new FileInputStream(confFile));
        String url = p.getProperty("url");
        String user = p.getProperty("user");
        String passwd = p.getProperty("password");
        System.out.println("----- Recherche du driver " + name + " -----");
        try {
            Class.forName(p.getProperty("driver")).newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        System.out.println("Driver trouvé");
        Connection retour = DriverManager.getConnection(url, user, passwd);
        System.out.println("Connexion établie");
        System.out.print("----------------------------------");
        for (int i = 0; i < name.length(); i++)
            System.out.print("-");
        System.out.println();
        return retour;
    }

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
            for (int i = 1; i <= rsmf.getColumnCount(); i++)
                sb.append(rs.getObject(i)).append("|");
            sb.deleteCharAt(sb.length() - 1);
            System.out.println(sb);
        }
    }

    @NotNull
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
            for (int i = 1; i <= rsmd.getColumnCount(); i++)
                try {
                    //Sous MySql, n'importe quel type de données peut être directement converti en String
                    //Le passage via le type ne fonctionne pas
                    if (rsmd.getColumnTypeName(i) == "TINYINT")
                        temp.add(String.valueOf(rs.getBoolean(i)));
                    else if (rsmd.getColumnTypeName(i) == "FLOAT")
                        temp.add(String.valueOf(rs.getFloat(i)));
                    else
                        temp.add(rs.getString(i));
                } catch (SQLException e) {
                    System.out.println(Thread.currentThread().getName() + "> Exception: " + e.getMessage());
                }
            champs.add(temp);
        }
        return new Table(title, champs);
    }

    @NotNull
    public String SelectLogUser(@NotNull String User) throws SQLException {
        PreparedStatement s = Connection.prepareStatement("select Nom,Prenom from Agents NATURAL join Login where Username = ?");
        s.setString(1, User);
        if (s.execute()) {
            final Vector<String> strings = toTable(s.getResultSet()).getChamps().elementAt(0);
            return strings.elementAt(0) + " " + strings.elementAt(1);
        } else
            return "";
    }

    /**
     * @param numVol Numéro du vol à sélectionner
     * @return Un objet ResultSet contenant les résultats de la requête
     * @throws SQLException Exceptions qui sont générées par la BD
     */
    public ResultSet SelectBagageVol(@NotNull String numVol) throws SQLException {
        PreparedStatement s = Connection.prepareStatement("SELECT Bagages.* FROM Bagages NATURAL JOIN Billets NATURAL JOIN Vols WHERE NumeroVol = ? FOR UPDATE ");
        s.setString(1, numVol);
        return s.executeQuery();
    }

    public Savepoint setSavepoint() throws SQLException {
        return Connection.setSavepoint();
    }

    public void rollback() throws SQLException {
        Connection.rollback();
    }

    public void rollback(Savepoint s) throws SQLException {
        Connection.rollback(s);
    }

    public void commit() throws SQLException {
        Connection.commit();
    }

    public int UpdateVol(@NotNull VolField champ, @NotNull Object value, @NotNull String numBagage) throws SQLException {
        PreparedStatement ps = Connection.prepareStatement("UPDATE Bagages SET " + champ.toString() + " = ? WHERE NumeroBagage = ?");
        switch (champ) {
            case Reception:
            case Verifier:
                ps.setInt(1, Boolean.valueOf(String.valueOf(value))? 1 : 0);
                break;
            case Charger:
            case Remarque:
                ps.setObject(1, value);
                break;
        }
        ps.setString(2, numBagage);
        return ps.executeUpdate();
    }

    public ResultSet SelectTodayVols() throws SQLException {
        return Connection.createStatement().executeQuery("select * from Vols where HeureDepart = CURRENT_DATE");
    }

    public ResultSet Select(@NotNull String table) throws SQLException {
        //La table doit être hard codée
        return Connection.createStatement().executeQuery("select * from " + table);
    }
}