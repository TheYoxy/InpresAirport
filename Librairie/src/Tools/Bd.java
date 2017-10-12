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

    public ResultSet Select(String table) throws SQLException {
        PreparedStatement ps = Connection.prepareStatement("select * from ?");
        return ps.executeQuery();
    }
}