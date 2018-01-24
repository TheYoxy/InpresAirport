package NetworkObject.Company;

import java.sql.Time;
import java.util.Date;

public interface ICompany {
    boolean createDestination(String v, String p);
    boolean createFlights(String v, String p, Date d, Time t, double pr);
    boolean cancelDestination(String v);
    boolean cancelFlights(String v, String p, Date d, Time t);
}
