package database.utilities;

import database.tables.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLDB {

    public Connection con;
    public Statement instruction;

    public MySQLDB() throws Exception {
        String dbname = "";

        Class sqldriver = Class.forName("com.mysql.jdbc.driver");
        con = DriverManager.getConnection("jdbc:odbc:" + dbname, "", "");
        instruction = con.createStatement();
    }

    public void GetnumBillets() throws SQLException {
        try {
            ResultSet res = instruction.executeQuery("select * from Billets");
            System.out.println("Envoi de l'instruction...");
        } catch (SQLException e){

        }
    }

    public List<Billets> get_Billets()
    {
        List<Billets> billets = new ArrayList<Billets>();
        try{
            ResultSet res = instruction.executeQuery("select * from Billets");
            while(res.next())
            {
                billets.add(new Billets(res.getString("numBillet"), res.getString("numVol")));
            }
        }
        catch(SQLException e) {
            //exception due au resultset
        }
        return billets;
    }

    public List<Vols> get_Vols()
    {
        List<Vols> vols = new ArrayList<Vols>();
        try{
            ResultSet res = instruction.executeQuery("select * from Vols");
            while(res.next())
            {
                vols.add(new Vols(res.getString(1), (res.getDate(2)).toString(), (res.getDate(3)).toString(), (res.getDate(4)).toString(),res.getString(5)));
            }
        }
        catch(SQLException e) {
            //exception due au resultset
        }
        return vols;
    }

    public List<Bagages> get_Bagages()
    {
        List<Bagages> bagages = new ArrayList<Bagages>();
        try{
            ResultSet res = instruction.executeQuery("select * from Vols");
            while(res.next())
            {
                bagages.add(new Bagages(res.getDouble(1), res.getBoolean(2)));
            }
        }
        catch(SQLException e) {
            //exception due au resultset
        }
        return bagages;
    }

    public List<Billets> get_any_Billets(String requete)
    {
        List<Billets> billets = new ArrayList<Billets>();
        try{
            ResultSet res = instruction.executeQuery(requete);
            while(res.next())
            {
                billets.add(new Billets(res.getString("numBillet"), res.getString("numVol")));
            }
        }
        catch(SQLException e) {
            //exception due au resultset
        }
        return billets;
    }

    public List<Vols> get_any_Vols(String requete)
    {
        List<Vols> vols = new ArrayList<Vols>();
        try{
            ResultSet res = instruction.executeQuery(requete);
            while(res.next())
            {
                vols.add(new Vols(res.getString(1), (res.getDate(2)).toString(), (res.getDate(3)).toString(), (res.getDate(4)).toString(),res.getString(5)));
            }
        }
        catch(SQLException e) {
            //exception due au resultset
        }
        return vols;
    }

    public List<Bagages> get_any_Bagages(String requete) {

        List<Bagages> bagages = new ArrayList<Bagages>();
        try{
            ResultSet res = instruction.executeQuery(requete);
            while(res.next())
            {
                bagages.add(new Bagages(res.getDouble(1), res.getBoolean(2)));
            }
        }
        catch(SQLException e) {
            //exception due au resultset
        }
        return bagages;
    }

    /*public static Connection ConnexionMySql() throws SQLException {
        String url = "jdbc:mysql://localhost/Athletisme?useSSL=false";
        String user = "floryan";
        String passwd = "";
        System.out.println("-------- Mysql JDBC Connection Testing ------");
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            return null;
        }
        System.out.println("Driver found !");
        Connection connection;
        connection = DriverManager.getConnection(url, user, passwd);
        if (connection != null)
            System.out.println("You made it, take the control of your database now!");
        else
            System.out.println("Failed to make connection!");
        System.out.println("----------------------------------------------");
        return connection;
    }*/
}
