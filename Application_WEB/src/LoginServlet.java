/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServletsUtiles;

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
@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"}, initParams = {
    @WebInitParam(name = "TypeSgbd", value = "MySql")})
public class LoginServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
        public void init (ServletConfig config) throws ServletException{
        super.init(config);
        //nbrConnections = 0;
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //synvhronised(this) {nbreConnexions++;}
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        //Bd bd = new Bd(MySql, "", "1234", "nico");

        /*String type = request.getParameter("type"); //signin or signup
        String email = request.getParameter("email");
        String pass = request.getParameter("pass");*/

        out.println("<HTML><HEAD><TITLE>LoginServlet</TITLE></HEAD><BODY>");
        out.println("<H1>Nouveau client</H1>");
        out.println("<p>nom :  "+ request.getParameter("mail") + " Pass : " + request.getParameter("pass") + "</p>");
        /*if(type.equals("new")){
            String pass = request.getParameter("username");
            //Bd.CreateUser();
        }

        if(Bd.checkUser(email, pass)) //TO DO Creer cette fonction
        {
            RequestDispatcher rs = request.getRequestDispatcher("Welcome");
            rs.forward(request, response);
        }
        else
        {
            out.println("Username or Password incorrect");
            RequestDispatcher rs = request.getRequestDispatcher("index.html");
            rs.include(request, response);
        }*/
        //doPost(request, response);
    }
    
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        //String id = req.getParameter("realname");
        //String password = req.getParameter("mypassword");
        //req.setAttribute("userName ", userName );    
        response.sendRedirect("index.html");
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
