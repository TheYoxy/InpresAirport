package Servlet;

import Tools.Bd;
import Tools.BdType;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(name = "Servlet", value = "/Caddie")
public class Servlet extends javax.servlet.http.HttpServlet {
    private static ResultSet SelectVols() {
        try {
            Bd mysql = new Bd(BdType.MySql);
            return mysql.Select("VolReservable");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        request.setAttribute("Vols", SelectVols());
        this.getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
    }
}
