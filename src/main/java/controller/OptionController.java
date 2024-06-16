package controller;

import entrypoint.Config;
import utility.LogFileVerifier;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import utility.Utility;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.HttpURLConnection;
import java.net.URL;

public class OptionController {
    @FXML
    private TextField apacheLogLocationTF;
    @FXML
    private Text ipDBLocationText;
    @FXML
    private Text ipDBNewVerStatusText;
    @FXML
    private TextField modsecLogLocationTF;
    @FXML
    private Text welcomeText;
    private final String IP_DB_TEXT = "IP Database Location: Available at ";
    FileChooser.ExtensionFilter extFilterLogs = new FileChooser.ExtensionFilter(
            "Log files (*.log, *.txt, *.json)",
            "*.log", "*.txt", "*.json"
    );
    @FXML
    private void initialize() {
        welcomeText.setText(String.format("Welcome, %s!", Config.getCurrentlyLoggedOnUser()));
        apacheLogLocationTF.setText(Config.getApacheLogLocation());
        modsecLogLocationTF.setText(Config.getModSecurityLogLocation());

        File ipDBFile = new File("GeoLite2-Country.mmdb");
        if (ipDBFile.exists() && ipDBFile.canRead()) {
            ipDBLocationText.setText(IP_DB_TEXT + ipDBFile.getAbsolutePath());
        } else {
            ipDBLocationText.setText("IP Database Location: NOT FOUND");
        }
    }
    @FXML
    void chooseFileApacheLog(ActionEvent event) throws IOException {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(extFilterLogs);
        fc.setTitle("Choose Apache log location");
        File file = fc.showOpenDialog(null);
        if (file != null) {
            String tmpLocation = file.getAbsolutePath();
            if (LogFileVerifier.isApacheLogFile(tmpLocation)) {
                System.out.println("[DEBUG] File name: " + file.getName());
                System.out.println("[DEBUG] File size: " + file.length() + " bytes");
                apacheLogLocationTF.setText(tmpLocation);
            } else {
                Utility.showAlert("ERROR", "The selected file is not a valid log file.");
            }
        }
    }

    @FXML
    void chooseFileModSecurityLog(ActionEvent event) throws IOException {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(extFilterLogs);
        fc.setTitle("Choose ModSecurity log location");
        File file = fc.showOpenDialog(null);
        if (file != null) {
            String tmpLocation = file.getAbsolutePath();
            if (LogFileVerifier.isModSecLogFile(tmpLocation)) {
                System.out.println("[DEBUG] File name: " + file.getName());
                System.out.println("[DEBUG] File size: " + file.length() + " bytes");
                modsecLogLocationTF.setText(tmpLocation);
            } else {
                Utility.showAlert("ERROR", "The selected file is not a valid log file.");
            }
        }
    }

    private static JsonNode fetchReleases() throws Exception {
        URL obj = new URL(Config.API_URL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", Config.ACCEPT_HEADER);
        con.setRequestProperty("X-GitHub-Api-Version", Config.API_VERSION_HEADER);

        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            Utility.showAlert(
                    "ERROR",
                    String.format("Got status code %d with response %s.", responseCode, con.getResponseMessage())
            );
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(response.toString());
    }

    @FXML
    public void fetchUpdateDB(ActionEvent event) {
        String resultFetch = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        try {
            JsonNode rootNode = fetchReleases();
            if (rootNode.isArray() && !rootNode.isEmpty()) {
                JsonNode firstObject = rootNode.get(0);
                resultFetch = firstObject.get("body").asText();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!Objects.equals(resultFetch, "")) {
            Path path = Paths.get("GeoLite2-Country.mmdb");
            BasicFileAttributes attrs;
            try {
                attrs = Files.readAttributes(path, BasicFileAttributes.class);
                String currentFileCreatedDate = sdf.format(new Date(attrs.lastModifiedTime().toMillis()));

                Date fetchedDate = sdf.parse(resultFetch);
                Date currentFileCreationDate = sdf.parse(currentFileCreatedDate);

                if (fetchedDate.after(currentFileCreationDate)) {
                    ipDBNewVerStatusText.setText(String.format("New version available: %s", resultFetch));
                } else {
                    ipDBNewVerStatusText.setText("You are using the latest IPDB version!");
                }
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        } else {
            ipDBNewVerStatusText.setText("You are using the latest IPDB version!");
        }
    }

    @FXML
    void updateIPDB(ActionEvent event) {
        try {
            JsonNode rootNode = fetchReleases();
            if (rootNode.isArray() && !rootNode.isEmpty()) {
                JsonNode firstObject = rootNode.get(0);
                JsonNode assetsArray = firstObject.get("assets");
                if (assetsArray.isArray() && !assetsArray.isEmpty()) {
                    for (JsonNode asset : assetsArray) {
                        String browserDownloadUrl = asset.get("browser_download_url").asText();
                        if (browserDownloadUrl.contains("Country")) {
                            downloadFile(browserDownloadUrl, "GeoLite2-Country.mmdb");
                        }
                    }
                }
            } else {
                System.out.println("No releases found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downloadFile(String fileURL, String savePath) {
        try (InputStream in = new BufferedInputStream(new URL(fileURL).openStream());
             OutputStream out = new FileOutputStream(savePath)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer, 0, 1024)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            Utility.showInfo("File Downloaded",
                    "New version of IPDB has been downloaded successfully."
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
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
