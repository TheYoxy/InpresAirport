package NetworkObject.Company;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

public class LowCost extends AbstractCompany {

    public LowCost(){}

    @Override
    public boolean createFlights(String numeroVol, String lieu, Timestamp Hdep, Timestamp Harr, float prix, String Desc, int PlacesDisp, int idAv) {
        System.out.println("coucou");
        return false;
    }
}
