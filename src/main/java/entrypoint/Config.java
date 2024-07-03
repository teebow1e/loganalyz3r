package entrypoint;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import utility.Utility;
import utility.GetAdminCredFirstRun;
import utility.GetLogPathFirstRun;
import user.UserManagement;
import static utility.Utility.showAlert;

public class Config {
    private static final Logger logger = Logger.getLogger(Config.class.getName());
    private Config() {
        throw new IllegalStateException("This function can not be instantiated.");
    }
    private static JsonNode pathConfig;
    private static String currentlyLoggedOnUser;
    public final static String NOT_AVAILABLE_TEXT = "N/A";
    private static final String CONFIG_DIR_PATH =
            System.getProperty("user.dir") + File.separator + ".config";
    private static final String ACCOUNTS_FILE_PATH =
            CONFIG_DIR_PATH + File.separator + "accounts.json";
    private static final String CONFIG_FILE_PATH =
            CONFIG_DIR_PATH + File.separator + "config.json";
    private static final String IP_DB_FILENAME = "GeoLite2-Country.mmdb";
    public static final String PROJECT_NAME = "loganalyz3r";
    public static String getConfigFilePath() {
        return CONFIG_FILE_PATH;
    }
    public static String getAccountsFilePath() {
        return ACCOUNTS_FILE_PATH;
    }
    public static String getIpDbFilename() {
        return IP_DB_FILENAME;
    }
    public static boolean checkConfigDir() {
        File dir = new File(CONFIG_DIR_PATH);
        return dir.exists() && dir.isDirectory() && dir.canRead();
    }
    public static boolean checkAccountsFile() {
        File file = new File(ACCOUNTS_FILE_PATH);
        return file.exists() && file.isFile() && file.canRead();
    }
    public static boolean checkConfigFile() {
        File file = new File(CONFIG_FILE_PATH);
        return file.exists() && file.isFile() && file.canRead();
    }
    public static void loadConfig() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        pathConfig = mapper.readTree(new File(CONFIG_FILE_PATH));
    }

    public static void firstRunAction() {
        File configFolder = new File(CONFIG_DIR_PATH);
        if (!configFolder.exists()) {
            boolean folderCreated = configFolder.mkdir();
            if (folderCreated) {
                logger.log(Level.INFO,"[FirstRun] .config folder has been created.");
                GetAdminCredFirstRun adminCredFR = new GetAdminCredFirstRun(null);
                GetLogPathFirstRun logPathFR = new GetLogPathFirstRun(null);

                adminCredFR.setVisible(true);
                if (adminCredFR.isSuccess()) {
                    String adminUsername = adminCredFR.getUsername();
                    String adminPassword = adminCredFR.getPassword();
                    UserManagement.addUser(ACCOUNTS_FILE_PATH, adminUsername, adminPassword);
                } else {
                    logger.log(Level.SEVERE,"[FirstRun] Failed to init admin cred.");
                }

                logPathFR.setVisible(true);
                if (logPathFR.isSuccess()) {
                    if (logPathFR.getUseSampleApacheLogCheckBox()) {
                        String apacheSamplePath = Utility.extractFileToLocal(
                                "samplelog/apache_access_log.log",
                                "sample/"
                        );
                        Utility.updateConfigValue(CONFIG_FILE_PATH,
                                "DEFAULT_APACHE_LOG_LOCATION",
                                apacheSamplePath
                        );
                    } else {
                        String apachePath = logPathFR.getApacheLogPath();
                        Utility.updateConfigValue(CONFIG_FILE_PATH,
                                "DEFAULT_APACHE_LOG_LOCATION",
                                apachePath
                        );
                    }

                    if (logPathFR.getUseSampleModSecurityLogCheckBox()) {
                        String modsecSamplePath = Utility.extractFileToLocal(
                                "samplelog/modsecurity_audit_log.log",
                                "sample/"
                        );
                        Utility.updateConfigValue(CONFIG_FILE_PATH,
                                "DEFAULT_MODSECURITY_LOG_LOCATION",
                                modsecSamplePath
                        );
                    } else {
                        String modSecurityPath = logPathFR.getModSecurityLogPath();
                        Utility.updateConfigValue(CONFIG_FILE_PATH,
                                "DEFAULT_MODSECURITY_LOG_LOCATION",
                                modSecurityPath
                        );
                    }
                } else {
                    logger.log(Level.SEVERE,"[FirstRun] Unable to create path config file.");
                }
                Utility.extractFileToLocal("db/GeoLite2-Country.mmdb", ".");
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
