package NetworkObject.Company;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

public class HighClass extends AbstractCompany {


    public boolean modifyCost(String v, String p, Date d, Time t, double pr){
        return false;
    };
    public boolean addSpecialOffer(String v, String p, Date d, Time t, double pr){
        return false;
    };

    @Override
    public boolean createFlights(String numeroVol, String lieu, Timestamp Hdep, Timestamp Harr, float prix, String Desc, int PlacesDisp, int idAv) {
        return false;
    }
}
