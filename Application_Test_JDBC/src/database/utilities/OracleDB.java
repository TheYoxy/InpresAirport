package database.utilities;

import Tools.FilesOperations;
import database.tables.Activites;
import database.tables.Intervenant;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;

public class OracleDB {

    public Connection con;
    public Statement instruction;

    private String url ="";
    private String user ="";
    private String passwd ="";

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
            con = DriverManager.getConnection(url, user, passwd);
        } catch (SQLException e) {
            System.out.println("Impossible d'établir la connexion");
            throw e;
        }
        instruction = con.createStatement();
    }

    public void Connect() throws SQLException{
        try {
            con = DriverManager.getConnection(url, user, passwd);
        } catch (SQLException e) {
            System.out.println("Impossible d'établir la connexion!");
            throw e;
        }
        instruction = con.createStatement();
    }

    public void Close(){
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet executeQuery(String query) throws SQLException {
        return con.isClosed() ? null : instruction.executeQuery(query);
    }

    public LinkedList<Activites> get_Activites() {
        LinkedList<Activites> activites = new LinkedList<>();
        try {
            ResultSet res = instruction.executeQuery("SELECT * FROM activites");
            while (res.next()) {
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                String str = df.format(res.getDate("DATEP"));
                activites.add(new Activites(res.getString("COURS"), res.getString("TYPE"), str, res.getString("DESCRIPTION"), res.getString("REFERENCE")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return activites;
    }

    public LinkedList<Intervenant> get_Intervenant() {
        LinkedList<Intervenant> intervenant = new LinkedList<>();
        try {
            ResultSet res = instruction.executeQuery("SELECT * FROM intervenant");
            while (res.next()) {
                intervenant.add(new Intervenant(res.getString(1), res.getString(2), res.getString(3)));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return intervenant;
    }

}
