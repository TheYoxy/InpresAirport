package NetworkObject.Company;

import java.sql.Time;
import java.util.Date;

public class Ryanair implements ICompany {
    @Override
    public boolean createDestination(String v, String p) {
        return false;
    }

    @Override
    public boolean createFlights(String v, String p, Date d, Time t, double pr) {
        return false;
    }

    @Override
    public boolean cancelDestination(String v) {
        return false;
    }

    @Override
    public boolean cancelFlights(String v, String p, Date d, Time t) {
        return false;
    }
}
