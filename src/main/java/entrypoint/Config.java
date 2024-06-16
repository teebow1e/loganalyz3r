package entrypoint;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import user.UserManagement;
import utility.Utility;
import utility.getAdminCredFirstRun;
import utility.getLogPathFirstRun;

import java.io.File;
import java.io.IOException;

import static utility.Utility.showAlert;

public class Config {
    private Config() {
        throw new IllegalStateException("This function can not be instantiated.");
    }
    private static JsonNode pathConfig;
    private static String currentlyLoggedOnUser;
    private static final String configDirPath =
            System.getProperty("user.dir") + File.separator + ".config";
    private static final String accountsFilePath =
            configDirPath + File.separator + "accounts.json";
    private static final String configFilePath =
            configDirPath + File.separator + "config.json";
    public static String getConfigFilePath() {
        return configFilePath;
    }
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
        File configFolder = new File(configDirPath);
        if (!configFolder.exists()) {
            boolean folderCreated = configFolder.mkdir();
            if (folderCreated) {
                System.out.println("DEBUG - Folder has been created .config");
                getAdminCredFirstRun adminCredFR = new getAdminCredFirstRun(null);
                getLogPathFirstRun logPathFR = new getLogPathFirstRun(null);

                adminCredFR.setVisible(true);
                if (adminCredFR.isSuccess()) {
                    String adminUsername = adminCredFR.getUsername();
                    String adminPassword = adminCredFR.getPassword();
                    UserManagement.addUser(accountsFilePath, adminUsername, adminPassword);
                } else {
                    System.out.println("DEBUG - Failed to init admin cred");
                }

                logPathFR.setVisible(true);
                if (logPathFR.isSuccess()) {
                    String apachePath = logPathFR.getApacheLogPath();
                    String modSecurityPath = logPathFR.getModSecurityLogPath();
                    Utility.updateConfigValue(configFilePath,
                            "DEFAULT_APACHE_LOG_LOCATION",
                            apachePath
                    );
                    Utility.updateConfigValue(configFilePath,
                            "DEFAULT_MODSECURITY_LOG_LOCATION",
                            modSecurityPath
                    );
                } else {
                    System.out.println("debug - unable to create modsec apache path");
                }
            } else {
                showAlert(ERROR_LABEL,
                        "Problem occured during first-run action. The program can not continue."
                );
            }
        }
    }

    public static String getApacheLogLocation() {
        return pathConfig.get("DEFAULT_APACHE_LOG_LOCATION").asText();
    }
    public static String getModSecurityLogLocation() {
        return pathConfig.get("DEFAULT_MODSECURITY_LOG_LOCATION").asText();
    }

    public static void setCurrentlyLoggedOnUser(String data) {
        currentlyLoggedOnUser = data;
    }

    public static String getCurrentlyLoggedOnUser() {
        return currentlyLoggedOnUser;
    }

    public static final String ERROR_LABEL = "ERROR";
    public static final String API_URL = "https://api.github.com/repos/P3TERX/GeoLite.mmdb/releases";
    public static final String ACCEPT_HEADER = "application/vnd.github+json";
    public static final String API_VERSION_HEADER = "2022-11-28";
}
