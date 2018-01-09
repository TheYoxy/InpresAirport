package NetworkObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class Ids {
    public static List<String> genIdBillets(ResultSet ids, String lieu, String numVol, int nb) throws SQLException {
        // NumVol-Lieu-Num
        List<String> list = new LinkedList<>();
        int val;
        if (!ids.last())
            val = 1;
        else {
            String id = ids.getString(1);
            val = Integer.parseInt(id.substring(id.lastIndexOf('-') + 1));
        }

        for (int i = 0; i < nb; i++)
            list.add(String.format("%s-%s-%d", numVol, lieu, val++));

        list.forEach(s -> System.out.println(Thread.currentThread().getName() + "> Id généré: " + s));
        return list;
    }
}
