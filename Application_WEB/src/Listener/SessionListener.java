package Listener;

import Beans.ReservationB;
import Tools.Bd;
import Tools.BdType;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.sql.SQLException;
import java.util.List;

@WebListener("/")
public class SessionListener implements HttpSessionListener {
    private Bd Sgbd = null;

    // Public constructor is required by servlet spec
    public SessionListener() {
        try {
            Sgbd = new Bd(BdType.MySql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------
    // HttpSessionListener implementation
    // -------------------------------------------------------
    @Override
    public void sessionCreated(HttpSessionEvent se) {
      /* Session is created. */
        se.getSession().setMaxInactiveInterval(30 * 60);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
      /* Session is destroyed. */
        System.out.println("SessionListener.sessionDestroyed");
        HttpSession s = se.getSession();
        List<ReservationB> list = (List<ReservationB>) s.getAttribute("reservation");
        if (list != null)
            for (ReservationB rb : list)
                try {
                    Sgbd.AjoutPlacesLibres((String) rb.getInfosVol().get(0), rb.getNbrPlaces());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
    }
}
