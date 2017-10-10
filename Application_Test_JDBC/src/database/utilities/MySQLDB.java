package database.utilities;

import Tools.FilesOperations;
import database.tables.*;

import java.sql.*;
import java.util.LinkedList;

public class MySQLDB {

    private Connection con;
    private Statement instruction;

    private String url ="";
    private String user ="";
    private String passwd ="";

    public MySQLDB() throws SQLException {

        FilesOperations.load_Properties("mysql");
        url = "jdbc:mysql://192.168.1.17/bd_airport?useSSL=false";
        user = FilesOperations.getUsername();
        passwd = FilesOperations.getPassword();
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

    public LinkedList<Billets> get_AnyBillets(String requete) throws SQLException{
        LinkedList<Billets> billets = new LinkedList<>();
        try {
            ResultSet res = instruction.executeQuery(requete);
            while (res.next()) {
                billets.add(new Billets(res.getString("numBillet"), res.getString("numVol")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }
        return billets;
    }

    public LinkedList<Agents> get_AnyAgents(String requete) throws SQLException{
        LinkedList<Agents> agents = new LinkedList<>();
        try {
            ResultSet res = instruction.executeQuery(requete);
            while (res.next()) {
                agents.add(new Agents(res.getString(1), res.getString(2), res.getString(3)));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }
        return agents;
    }

    public LinkedList<Vols> get_Vols() {
        LinkedList<Vols> vols = new LinkedList<>();
        try {
            ResultSet res = instruction.executeQuery("SELECT * FROM Vols");
            while (res.next()) {
                vols.add(new Vols(res.getString(1), res.getString(2), (res.getDate(3)).toString(), (res.getDate(4)).toString(), (res.getDate(5)).toString(), res.getInt(6)));
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

    public LinkedList<Avion> get_Avions() {
        LinkedList<Avion> retour = new LinkedList<>();
        try {
            ResultSet res = instruction.executeQuery("select * from Avion");
            while (res.next()) {
                retour.add(new Avion(res.getInt("id"), res.getString("modele"), res.getBoolean("vol")));
            }
        } catch (SQLException e) {
            System.out.println(e.getLocalizedMessage());
        }
        return retour;
    }
    public void add_Agent(Agents agent) {
        try {
            String query = " insert into Agents (nom, prenom, poste)"
                    + " values ('"+agent.getNom()+"','" + agent.getPrenom() + "','" + agent.getPoste()+ "')";
            instruction.executeUpdate(query);
        }catch(SQLException e){
            System.out.println();
        }
    }

    public void update_Agent(Agents agent) throws SQLException{
        try{
            String query = "update Agents set poste ='"+ agent.getPoste() + "' where prenom ='"+agent.getPrenom()+"' and nom ='"+agent.getNom()+"'";
            instruction.executeUpdate(query);
        }catch(SQLException e){
            System.out.println(e.getMessage());
            throw e;
        }
    }

}
