package Tools;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;
import java.util.Set;

public abstract class PropertiesReader {
    public static final String filename = "config.properties";
    private static final Properties p = new Properties();

    static {
        try {
            p.load(new FileInputStream(filename));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static String getProperties(String propertiesName) throws IOException {
        return p.getProperty(propertiesName);
    }

    public static Set<String> list() {
        return p.stringPropertyNames();
    }

    public static void Print(PrintStream ps) {
        p.list(ps);
    }

}
