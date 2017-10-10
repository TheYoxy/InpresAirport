package database.utilities;

import Tools.FilesOperations;
import database.tables.Agents;
import database.tables.Bagages;
import database.tables.Billets;
import database.tables.Vols;

import java.sql.*;
import java.util.LinkedList;

public class MySQLDB {

    private Connection con;
    private Statement instruction;

    public MySQLDB() throws SQLException {

        FilesOperations.load_Properties("mysql");
        String url = "jdbc:mysql://localhost/bd_airport?useSSL=false";
        String user = FilesOperations.getUsername();
        String passwd = FilesOperations.getPassword();
        System.out.println("-------- Test de connection mysql ------");
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        System.out.println("Mysql JDBC Driver instancié");
        try {
            con = DriverManager.getConnection(url, user, passwd);
        } catch (SQLException e) {
            System.out.println("Impossible d'établir la connexion!");
            throw e;
        }
        System.out.println("----------------------------------------------");
        instruction = con.createStatement();
    }

    public ResultSet executeQuery(String query) throws SQLException {
        return con.isClosed() ? null : instruction.executeQuery(query);
    }

    public LinkedList<Billets> get_Billets() {
        LinkedList<Billets> billets = new LinkedList<>();
        try {
            ResultSet res = instruction.executeQuery("SELECT * FROM Billets");
            while (res.next()) {
                billets.add(new Billets(res.getString("numBillet"), res.getString("numVol")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return billets;
    }

    public LinkedList<Vols> get_Vols() {
        LinkedList<Vols> vols = new LinkedList<>();
        try {
            ResultSet res = instruction.executeQuery("SELECT * FROM Vols");
            while (res.next()) {
                vols.add(new Vols(res.getString(1), res.getString(2), (res.getDate(3)).toString(), (res.getDate(4)).toString(), (res.getDate(5)).toString(), res.getString(6)));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return vols;
    }

    public LinkedList<Bagages> get_Bagages() {
        LinkedList<Bagages> bagages = new LinkedList<>();
        try {
            ResultSet res = instruction.executeQuery("SELECT * FROM Vols");
            while (res.next()) {
                bagages.add(new Bagages(res.getString(1), res.getDouble(2), res.getBoolean(3)));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return bagages;
    }

    public LinkedList<Agents> get_Agents() {
        LinkedList<Agents> agents = new LinkedList<>();
        try {
            ResultSet res = instruction.executeQuery("SELECT * FROM Agents");
            while (res.next()) {
                agents.add(new Agents(res.getString(1), res.getString(2), res.getString(3)));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return agents;
    }

}
