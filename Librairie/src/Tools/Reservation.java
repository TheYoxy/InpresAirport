package Tools;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import Tools.Bd.Bd;

public class Reservation {
    public static List addReservation(Bd bd, String username, String numVol, int nbPlaces)
    throws SQLException, ReservationException {
        if (bd.suppPlacesLibre(numVol, nbPlaces) == 0)
            throw new ReservationException();
        ResultSet rs = bd.selectReservation(username, numVol);
        //Si il existe
        if (rs.next()) {
            if (bd.updateReservation(username, numVol, nbPlaces) == 0)
                throw new ReservationException();
        }
        else {
            if (bd.insertReservation(username, numVol, nbPlaces) == 0)
                throw new ReservationException();
        }
        rs = bd.selectReservation(username, numVol);
        if (!rs.next()) throw new ReservationException();
        return Bd.toList(rs);
    }

    public static boolean delReservation(Bd bd, String username, String numVol)
    throws SQLException {
        ResultSet rs = bd.selectReservation(username, numVol);
        //Si on a bien un vol
        if (rs.next()) {
            int nbPlaces = rs.getInt("nbPlaces");
            bd.deleteReservation(username, numVol);
            bd.ajoutPlacesLibres(numVol, 0);
        }
        return true;
    }

    public static class ReservationException extends Throwable {

    }
}
