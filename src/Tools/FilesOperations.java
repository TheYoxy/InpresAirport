package Tools;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

public class FilesOperations {

    /**
     *
     */
    public final static String PROPERTIES = "config.properties";

    private static Properties properties;

    private String _dbtype;

    public FilesOperations(String dbtype){
        if(dbtype.equalsIgnoreCase("mysql") || dbtype.equalsIgnoreCase("oracle"))
            _dbtype = dbtype;
    }
    public void load_Properties(){
        properties = new Properties();

        try {
            properties.load(new FileInputStream(PROPERTIES));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public String getUsername(){
        if(_dbtype.equalsIgnoreCase("mysql"))
            return properties.getProperty("usernameSQL");
        else
            return properties.getProperty("usernameOracle");
    }

    public String getPassword(){
        if(_dbtype.equalsIgnoreCase("oracle"))
            return properties.getProperty("passwordSQL");
        else
            return properties.getProperty("passwordOracle");
    }
}
