package Tools;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class Bd {
    private Connection Connection;

    public Bd(BdType type) throws IOException, SQLException {
        this.Connection = createConnection(type);
    }

    public Bd(Connection connection) throws SQLException {
        if (!connection.isClosed())
            Connection = connection;
    }

    public static void main(String[] argv) throws IOException, SQLException {
        Bd b = new Bd(BdType.MySql);
        AfficheResultSet(b.Select("Login"));

        /*DatabaseMetaData dmd = b.Connection.getMetaData();
        AfficheResultSet(dmd.getTables("bd_airport", null, "%", null));*/
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

    public ResultSet Select(String table) throws SQLException {
        Statement s = Connection.createStatement();
        //La table doit être hard codée
        return s.executeQuery("select * from " + table);
    }
}