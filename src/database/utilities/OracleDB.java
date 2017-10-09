package database.utilities;

import Tools.FilesOperations;

import java.sql.*;

public class OracleDB {

    public Connection con;
    public Statement instruction;
    public FilesOperations file = new FilesOperations("oracle");

    public Connection ConnexionOracle() throws SQLException {
        String url = "jdbc:oracle:thin:@localhost:1521:xe";
        String user = file.getUsername();
        String passwd = file.getPassword();

        System.out.println("-------- Oracle JDBC Connection Testing ------");
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your Oracle JDBC Driver?");
            e.printStackTrace();
            return null;
        }

        System.out.println("Oracle JDBC Driver Registered!");

        Connection connection;
        connection = DriverManager.getConnection(url, user, passwd);
        if (connection != null) System.out.println("You made it, take control your database now!");
        else System.out.println("Failed to make connection!");
        System.out.println("----------------------------------------------");
        return connection;
    }
}
