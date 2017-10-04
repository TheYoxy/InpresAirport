import java.sql.*;
//com.mysql.jdbc.driver
//oracle.jdbc.driver.OracleDriver


public class APPLICATION_TEST_JDBC {

    public static void main(String args[]) throws Exception {
        System.out.println("Test connexion My sql ");
        Class sqldriver = Class.forName("com.mysql.jdbc.driver");

        Connection con = DriverManager.getConnection("jdbc:odbc:BD_AIRTPORT", "", "");
        System.out.println("Connexion à la base de donnée réalisée");

        Statement instruction = con.createStatement();
        System.out.println("Creation instance instruction");

        ResultSet res = instruction.executeQuery("select * from Billets");
        System.out.println("Envoi de l'instruction...");

        int cpt = 0;
        while (res.next())

        {
            if (cpt == 0) {
                System.out.println("Parcours du curseur");
                cpt++;
            }
            String cs = res.getString("numBillet");
            System.out.println("Recuperation du numero de billet");
            System.out.println(cpt + ". " + cs );
        }//fin while

        instruction.executeUpdate("update Billets" + "set numBillet =aaaaaa-2536");
        System.out.println("Envoi de la modification");
        res = instruction.executeQuery("select* from Billets");
        System.out.println("Envoi de l'instruction Select");
    }//fin main
}
