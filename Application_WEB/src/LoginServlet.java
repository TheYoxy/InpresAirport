/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
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

    public void init (ServletConfig config) throws ServletException{
        super.init(config);
        nbrConnexions = 0;
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        synchronized(this) {nbrConnexions++;}
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        /*out.println("<HTML><HEAD><TITLE>LoginServlet</TITLE></HEAD><BODY>");
        out.println("<H1>Nouveau client</H1>");
        out.println("<p>nom :  "+ request.getParameter("mail") + " Pass : " + request.getParameter("pass") + "</p>");*/

        String type = request.getParameter("type"); //signin or signup
        String email = request.getParameter("mail");
        String pass = request.getParameter("pass");

        if(type.equals("signup")){
            String username = request.getParameter("username");
            //Bd.CreateUser();
        }

        if(checkUser(email, pass)) //TO DO Creer cette fonction
        {
            status = "success";
        }
        else
        {
            out.println("Username or Password incorrect");
            status = "fail";

        }

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
        //Bd bd = new Bd(MySql, "", "1234", "nico");
        return false;
    }
   

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    //@Override
    /*protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }*/

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
