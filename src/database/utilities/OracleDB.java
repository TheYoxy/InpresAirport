package database.utilities;

import Tools.FilesOperations;
import database.tables.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OracleDB {

    public Connection con;
    public Statement instruction;
    public FilesOperations file = new FilesOperations();

    public OracleDB()throws SQLException {

        file.load_Properties("oracle");
        String url = "jdbc:oracle:thin:@localhost:1521:xe";
        String user = file.getUsername();
        String passwd = file.getPassword();

        System.out.println("-------- Test de connexion Oracle ------");
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Oracle JDBC Driver Registered!");
        try {
            con = DriverManager.getConnection(url, user, passwd);
        }catch(SQLException e){
            throw e;
        }
        instruction = con.createStatement();
    }

    public List<Activites> get_Activites()
    {
        List<Activites> activites = new ArrayList();
        try{
            ResultSet res = instruction.executeQuery("select * from activites");
            while(res.next())
            {
                activites.add(new Activites(res.getString(1), res.getString(2), res.getDate(3).toString(), res.getString(4), res.getString(5)));
            }
        }
        catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return activites;
    }

    public List<Intervenant> get_Intervenant() {
        List<Intervenant> intervenant = new ArrayList<>();
        try{
            ResultSet res = instruction.executeQuery("select * from intervenant");
            while(res.next())
            {
                intervenant.add(new Intervenant(res.getString(1), res.getString(2), res.getString(3)));
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }

        return intervenant;
    }

}
