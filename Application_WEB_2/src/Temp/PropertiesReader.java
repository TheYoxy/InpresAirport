package Temp;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;
import java.util.Set;

public abstract class PropertiesReader {
    private static final Properties p = new Properties();

    static {
        try {
            p.load(PropertiesReader.class.getResourceAsStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void Print(PrintStream ps) {
        p.list(ps);
    }

    public static String getProperties(String propertiesName) {
        return p.getProperty(propertiesName);
    }

    public static Set<String> list() {
        return p.stringPropertyNames();
    }

}
