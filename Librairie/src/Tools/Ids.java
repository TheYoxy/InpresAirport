package Tools;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javafx.util.Pair;

public class Ids {
    public static Pair<List<String>, List<Integer>> genIdBillets(ResultSet ids, String lieu, String numVol, int nb) throws SQLException {
        // NumVol-Lieu-Num
        List<String> strings = new LinkedList<>();
        List<Integer> integers = new LinkedList<>();
        int val = !ids.next()
                  ? 1
                  : ids.getInt(1);

        System.out.println(Thread.currentThread().getName() + "> Dernier numéro dans la base: " + val);

        for (int i = 0; i < nb; i++) {
            integers.add(++val);
            strings.add(String.format("%s-%s-%d", numVol, lieu, val));
        }

        strings.forEach(s -> System.out.println(Thread.currentThread().getName() + "> Id généré: " + s));
        return new Pair<>(strings, integers);
    }
}
