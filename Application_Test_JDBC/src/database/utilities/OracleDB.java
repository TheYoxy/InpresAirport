package database.utilities;

import Tools.FilesOperations;
import database.tables.Activites;
import database.tables.Intervenant;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.LinkedList;

public class OracleDB {

    private Connection Con;
    private Statement Instruction;

    private String Url = "";
    private String User = "";
    private String Passwd = "";

    public OracleDB() throws SQLException {

        FilesOperations.load_Properties("oracle");
        String url = "jdbc:oracle:thin:@//localhost:1521/orcl";
        String user = FilesOperations.getUsername();
        String passwd = FilesOperations.getPassword();

        System.out.println("-------- Test de connexion Oracle ------");
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        System.out.println("Oracle JDBC Driver instancié");
        try {
            Con = DriverManager.getConnection(url, user, passwd);
        } catch (SQLException e) {
            System.out.println("Impossible d'établir la connexion");
            throw e;
        }
        Instruction = Con.createStatement();
    }

    public void Connect() throws SQLException {
        try {
            Con = DriverManager.getConnection(Url, User, Passwd);
        } catch (SQLException e) {
            System.out.println("Impossible d'établir la connexion!");
            throw e;
        }
        Instruction = Con.createStatement();
    }

    public void Close() {
        try {
            Con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet executeQuery(String query) throws SQLException {
        return Con.isClosed() ? null : Instruction.executeQuery(query);
    }

    public LinkedList<Activites> get_Activites() {
        LinkedList<Activites> activites = new LinkedList<>();
        try {
            ResultSet res = Instruction.executeQuery("SELECT * FROM activites");
            while (res.next()) {
                activites.add(new Activites(res.getString("COURS"),
                        res.getString("TYPE"),
                        new SimpleDateFormat("dd/MM/yyyy").format(res.getDate("DATEP")),
                        res.getString("DESCRIPTION"),
                        res.getString("REFERENCE")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return activites;
    }

    public LinkedList<Intervenant> get_Intervenant() {
        LinkedList<Intervenant> intervenant = new LinkedList<>();
        try {
            ResultSet res = Instruction.executeQuery("SELECT * FROM intervenant");
            while (res.next()) {
                intervenant.add(new Intervenant(res.getString(1), res.getString(2), res.getString(3)));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return intervenant;
    }
}
