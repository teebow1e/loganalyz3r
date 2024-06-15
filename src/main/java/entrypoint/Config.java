package entrypoint;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

public class Config {
    private Config() {
        throw new IllegalStateException("This function can not be instantiated.");
    }
    private static JsonNode pathConfig;
    private static final String configDirPath =
            System.getProperty("user.dir") + File.separator + ".config";
    private static final String accountsFilePath =
            configDirPath + File.separator + "accounts.json";
    private static final String configFilePath =
            configDirPath + File.separator + "config.json";
    public static boolean checkConfigDir() {
        File dir = new File(configDirPath);
        return dir.exists() && dir.isDirectory() && dir.canRead();
    }
    public static boolean checkAccountsFile() {
        File file = new File(accountsFilePath);
        return file.exists() && file.isFile() && file.canRead();
    }
    public static boolean checkConfigFile() {
        File file = new File(configFilePath);
        return file.exists() && file.isFile() && file.canRead();
    }
    public static void loadConfig() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        pathConfig = mapper.readTree(new File(configFilePath));
    }
    public static void firstRunAction() {

    }
    public static String getApacheLogLocation() {
        return pathConfig.get("DEFAULT_APACHE_LOG_LOCATION").asText();
    }

    public static String getModSecurityLogLocation() {
        return pathConfig.get("DEFAULT_MODSECURITY_LOG_LOCATION").asText();
    }

    public static String getGeoLiteDbLocation() {
        return pathConfig.get("GEOLITE_DB_LOCATION").asText();
    }
    public static final String ERROR_LABEL = "ERROR";
}
