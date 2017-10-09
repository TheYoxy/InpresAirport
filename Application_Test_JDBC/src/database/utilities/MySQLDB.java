package database.utilities;

import database.tables.*;
import Tools.FilesOperations;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLDB {

    public Connection con;
    public Statement instruction;
    public FilesOperations file = new FilesOperations();

    public MySQLDB()throws SQLException {

        file.load_Properties("mysql");
        String url = "jdbc:mysql://localhost/bd_airport?useSSL=false";
        String user = file.getUsername();
        String passwd = file.getPassword();
        System.out.println("-------- Test de connection mysql ------");
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        System.out.println("Driver trouve !");
        try {
            con = DriverManager.getConnection(url, user, passwd);
        }catch (SQLException e) {
            System.out.println("Impossible d'établir la connexion!");
            throw e;
        }
        System.out.println("----------------------------------------------");
        instruction = con.createStatement();
    }

    public void GetnumBillets() throws SQLException {
        try {
            ResultSet res = instruction.executeQuery("select * from Billets");
            System.out.println("Envoi de l'instruction...");
        } catch (SQLException e){
            System.out.println( e.getMessage());
        }
    }

    public List<Billets> get_Billets()
    {
        List<Billets> billets = new ArrayList();
        try{
            ResultSet res = instruction.executeQuery("select * from Billets");
            while(res.next())
            {
                billets.add(new Billets(res.getString("numBillet"), res.getString("numVol")));
            }
        }
        catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return billets;
    }

    public List<Vols> get_Vols()
    {
        List<Vols> vols = new ArrayList();
        try{
            ResultSet res = instruction.executeQuery("select * from Vols");
            while(res.next())
            {
                vols.add(new Vols(res.getString(1), res.getString(2), (res.getDate(3)).toString(), (res.getDate(4)).toString(), (res.getDate(5)).toString(),res.getString(6)));
            }
        }
        catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return vols;
    }

    public List<Bagages> get_Bagages()
    {
        List<Bagages> bagages = new ArrayList();
        try{
            ResultSet res = instruction.executeQuery("select * from Vols");
            while(res.next())
            {
                bagages.add(new Bagages(res.getString(1),res.getDouble(2), res.getBoolean(3)));
            }
        }
        catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return bagages;
    }
    
    public List<Agents> get_Agents()
    {
        List<Agents> agents = new ArrayList();
        try{
            ResultSet res = instruction.executeQuery("select * from Agents");
            while(res.next())
            {
                agents.add(new Agents(res.getString(1), res.getString(2), res.getString(3)));
            }
        }
        catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return agents;
    }

    public List<Billets> get_any_Billets(String requete)
    {
        List<Billets> billets = new ArrayList();
        try{
            ResultSet res = instruction.executeQuery(requete);
            while(res.next())
            {
                billets.add(new Billets(res.getString("numBillet"), res.getString("numVol")));
            }
        }
        catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return billets;
    }

    public List<Vols> get_any_Vols(String requete)
    {
        List<Vols> vols = new ArrayList();
        try{
            ResultSet res = instruction.executeQuery(requete);
            while(res.next())
            {
                vols.add(new Vols(res.getString(1), res.getString(2), (res.getDate(3)).toString(), (res.getDate(4)).toString(), (res.getDate(5)).toString(),res.getString(6)));
            }
        }
        catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return vols;
    }

    public List<Bagages> get_any_Bagages(String requete) {

        List<Bagages> bagages = new ArrayList();
        try{
            ResultSet res = instruction.executeQuery(requete);
            while(res.next())
            {
                bagages.add(new Bagages(res.getString(1),res.getDouble(2), res.getBoolean(3)));
            }
        }
        catch(SQLException e) {
            System.out.println( e.getMessage());
        }
        return bagages;
    }
}
