package database.utilities;

import Tools.FilesOperations;
import database.tables.Activites;
import database.tables.Intervenant;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class OracleDB {

    public Connection con;
    public Statement instruction;
    public FilesOperations file = new FilesOperations();

    public OracleDB() throws SQLException {

        file.load_Properties("oracle");
        String url = "jdbc:oracle:thin:@//localhost:1521/orcl";
        String user = file.getUsername();
        String passwd = file.getPassword();

        System.out.println("-------- Test de connexion Oracle ------");
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        System.out.println("Oracle JDBC Driver Registered!");
        try {
            con = DriverManager.getConnection(url, user, passwd);
        } catch (SQLException e) {
            throw e;
        }
        instruction = con.createStatement();
    }

    public List<Activites> get_Activites() {
        List<Activites> activites = new ArrayList();
        try {
            ResultSet res = instruction.executeQuery("select * from activites");
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

    public List<Intervenant> get_Intervenant() {
        List<Intervenant> intervenant = new ArrayList<>();
        try {
            ResultSet res = instruction.executeQuery("select * from intervenant");
            while (res.next()) {
                intervenant.add(new Intervenant(res.getString(1), res.getString(2), res.getString(3)));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return intervenant;
    }

}
