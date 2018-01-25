package Filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter(filterName = "ConnexionFilter", value = "/*")
public class ConnexionFilter implements Filter {
    private final static String[] authList = {"/index.jsp", "/NLogin.jsp", "/Login.jsp", "/error.jsp", "/Login", "/NewLog.jsp", "/"};

    public void init(FilterConfig config) {

    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
    throws ServletException, IOException {
        HttpServletRequest  request  = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        HttpSession         session  = request.getSession();
        boolean             find     = false;
        System.out.println("request.getRequestURI() = " + request.getRequestURI());
        for (int i = 0, authListLength = authList.length; i < authListLength && !find; i++) {
            String s = authList[i];
            find = request.getRequestURI().contains(s);
        }

        if (!find)
            if (session.getAttribute("log") == null) {
                response.sendRedirect("/index.jsp");
                return;
            }
        chain.doFilter(req, resp);
    }

    public void destroy() {
    }

}
