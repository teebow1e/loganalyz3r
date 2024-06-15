package entrypoint;

import javafx.application.Application;
import ui.LoginForm;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    private static final String configDirPath =
            System.getProperty("user.dir") + File.separator + ".config";
    private static final String accountsFilePath =
            configDirPath + File.separator + "accounts.json";
    private static final String configFilePath =
            configDirPath + File.separator + "config.json";
    public static void main(String[] args) {
        File configDir = new File(configDirPath);
        File accountsFile = new File(accountsFilePath);
        File configFile = new File(configFilePath);
        if (!configDir.exists()) {
            configDir.mkdir();
        }
        Application.launch(LoginForm.class, args);
    }

//    private static void createFileIfNotExists(String filePath) {
//        File file = new File(filePath);
//        if (!file.exists()) {
//            try {
//                Files.createFile(Paths.get(filePath));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
