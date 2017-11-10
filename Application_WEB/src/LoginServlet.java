/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Tools.Bd;
import Tools.BdType;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Nicolas
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"}, initParams = { @WebInitParam(name = "TypeSgbd", value = "MySql")})

public class LoginServlet extends HttpServlet {
    int nbrConnexions;
    String status;
    String user = "admin";
    Bd sgbd;

    public void init (ServletConfig config) throws ServletException{
        super.init(config);
        nbrConnexions = 0;
        try {
            sgbd = new Bd(BdType.MySql);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        synchronized(this) {nbrConnexions++;}
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String email = "";
        String pass = "";
        String username = "";
;
        /*out.println("<HTML><HEAD><TITLE>LoginServlet</TITLE></HEAD><BODY>");
        out.println("<H1>Nouveau client</H1>");
        out.println("<p>nom :  "+ request.getParameter("mail") + " Pass : " + request.getParameter("pass") + "</p>");*/

        String type = request.getParameter("type"); //signin or signup

        if(type.equals("signin")) {
            email = request.getParameter("mail");
            pass = request.getParameter("pass");
            if(checkUser(email, pass)) //TO DO Creer cette fonction
            {
                status = "success";
            }
            else
            {
                out.println("Username or Password incorrect");
                status = "fail";

            }
        }

        if(type.equals("signup")){
            email = request.getParameter("mail");
            pass = request.getParameter("pass");
            username = request.getParameter("username");
            //Bd.CreateUser();
        }

        if(type.equals("logout"))
            status = "fail";

        doPost(request, response);
    }
    
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        //req.getSession().setAttribute("message", message);
        req.setAttribute("type",status );
        req.setAttribute("user",user );
        req.getRequestDispatcher("JSPLogin.jsp").forward(req, response);
    }

    public boolean checkUser(String mail, String pass){
        ResultSetMetaData rsmf;
        String userbd;
        String passbd;
        String mailbd;
        boolean statusbd = false;

        try {
            ResultSet rs = sgbd.Select("users");
            while (rs.next()) {
                userbd = rs.getString(1);
                passbd = rs.getString(2);
                mailbd = rs.getString(5);
                if(mailbd.equals(mail) && passbd.equals(pass))
                {
                    statusbd = true;
                    user = userbd;
                }
            }
        }catch(Exception e) {

        }

        return statusbd;
    }
   

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
