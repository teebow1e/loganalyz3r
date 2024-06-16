package controller;

import entrypoint.Config;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import utility.Utility;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class OptionController {
    @FXML
    private TextField apacheLogLocationTF;

    @FXML
    private Button chooseApacheLocationBtn;

    @FXML
    private Button chooseModsecLocationBtn;

    @FXML
    private Button confirmApacheLocationBtn;

    @FXML
    private Button confirmModsecLocationBtn;

    @FXML
    private Button fetchUpdateDBBtn;

    @FXML
    private Text ipDBLocationText;

    @FXML
    private Text ipDBNewVerStatusText;

    @FXML
    private TextField modsecLogLocationTF;

    @FXML
    private Button updateDBBtn;
    @FXML
    private Text welcomeText;
    private final String IP_DB_TEXT = "IP Location Database: Available at ";
    private String updateDBLink = "";
    FileChooser.ExtensionFilter extFilterLogs = new FileChooser.ExtensionFilter(
            "Log files (*.log, *.txt, *.json)",
            "*.log", "*.txt", "*.json"
    );

    @FXML
    private void initialize() {
        welcomeText.setText(String.format("Welcome, %s!", Config.getCurrentlyLoggedOnUser()));
        apacheLogLocationTF.setText(Config.getApacheLogLocation());
        modsecLogLocationTF.setText(Config.getModSecurityLogLocation());
    }

    @FXML
    void chooseFileApacheLog(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(extFilterLogs);
        fc.setTitle("Choose a file for streaming");
        File file = fc.showOpenDialog(null);
        if (file != null) {
            String tmpLocation = file.getAbsolutePath();
            // todo: implement check of log file
            System.out.println("[DEBUG] File name: " + file.getName());
            System.out.println("[DEBUG] File size: " + file.length() + " bytes");
            apacheLogLocationTF.setText(tmpLocation);
        }
    }

    @FXML
    void chooseFileModSecurityLog(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(extFilterLogs);
        fc.setTitle("Choose a file for streaming");
        File file = fc.showOpenDialog(null);
        if (file != null) {
            String tmpLocation = file.getAbsolutePath();
            // todo: implement check of log file
            System.out.println("[DEBUG] File name: " + file.getName());
            System.out.println("[DEBUG] File size: " + file.length() + " bytes");
            modsecLogLocationTF.setText(tmpLocation);
        }
    }

    @FXML
    void fetchUpdateDB(ActionEvent event) {
        String resultFetch = Utility.getLatestIPDBRelease();
        if (!Objects.equals(resultFetch, "")) {
            // todo: need perform check with the current DB file
            ipDBNewVerStatusText.setText(String.format("New version available: %s", resultFetch));
        } else {
            ipDBNewVerStatusText.setText("You are using the latest IPDB version!");
        }
    }

    @FXML
    void updateIPDB(ActionEvent event) {
        System.out.println("let's update");
    }
    @FXML
    void setApacheLocation(ActionEvent event) throws IOException {
        Utility.updateConfigValue(Config.getConfigFilePath(),
                "DEFAULT_APACHE_LOG_LOCATION",
                apacheLogLocationTF.getText()
        );
        Config.loadConfig();
    }

    @FXML
    void setModSecurityLocation(ActionEvent event) throws IOException {
        Utility.updateConfigValue(Config.getConfigFilePath(),
                "DEFAULT_MODSECURITY_LOG_LOCATION",
                modsecLogLocationTF.getText()
        );
        Config.loadConfig();
    }
}
