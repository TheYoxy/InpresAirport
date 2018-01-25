package NetworkObject.Company;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

public interface ICompany {
    boolean createFlights(String numeroVol, String lieu, Timestamp Hdep, Timestamp Harr, float prix , String Desc, int PlacesDisp, int idAv);
}
