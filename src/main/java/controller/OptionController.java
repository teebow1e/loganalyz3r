package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import java.io.*;
import java.net.URLConnection;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ProgressMonitorInputStream;

import utility.Utility;
import entrypoint.Config;
import utility.LogFileVerifier;

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
    private static final Logger logger = Logger.getLogger(OptionController.class.getName());

    FileChooser.ExtensionFilter extFilterLogs = new FileChooser.ExtensionFilter(
            "Log files (*.log, *.txt, *.json)",
            "*.log", "*.txt", "*.json"
    );
    @FXML
    private void initialize() {
        welcomeText.setText(String.format("Welcome, %s!", Config.getCurrentlyLoggedOnUser()));
        apacheLogLocationTF.setText(Config.getApacheLogLocation());
        modsecLogLocationTF.setText(Config.getModSecurityLogLocation());

        File ipDBFile = new File(Config.getIpDbFilename());
        if (ipDBFile.exists() && ipDBFile.canRead()) {
            String ipDBLocationMsg = "IP Database Location: Available at ";
            ipDBLocationText.setText(ipDBLocationMsg + ipDBFile.getAbsolutePath());
        } else {
            logger.log(Level.WARNING, "GeoLiteIPDB not found.");
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
                logger.log(Level.INFO,"File name: {0}", file.getName());
                logger.log(Level.INFO,"File size: {0} bytes", file.length());
                apacheLogLocationTF.setText(tmpLocation);
            } else {
                logger.log(Level.INFO, "[Apache] User selected an invalid file.");
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
                logger.log(Level.INFO,"File name: {0}", file.getName());
                logger.log(Level.INFO,"File size: {0} bytes", file.length());
                modsecLogLocationTF.setText(tmpLocation);
            } else {
                logger.log(Level.INFO, "[ModSecurity] User selected an invalid file.");
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
        logger.log(Level.INFO, "HTTP request received status code {0}", responseCode);
        if (responseCode != 200) {
            logger.log(Level.INFO, "Received response: {0}", con.getResponseMessage());
            Utility.showAlert(
                    "ERROR",
                    String.format("Got status code %d, you should try again.", responseCode)
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
            Utility.showAlert("ERROR", String.format("%s\n%s", "An error has occurred during fetching update.", e));
            logger.log(Level.INFO, "[fetchUpdate] Failed to fetch update from remote API.", e);
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
            } catch (IOException ioe) {
                logger.log(Level.WARNING, "[fetchUpdate] Failed to read GeoLiteDB file.", ioe);
            } catch (ParseException pex) {
                logger.log(Level.WARNING, "[fetchUpdate] Failed to parse remote data to time data.", pex);
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
                logger.log(Level.INFO,"No releases found when connecting to API.");
            }
        } catch (Exception e) {
            Utility.showAlert("ERROR", String.format("%s\n%s", "An error has occurred during downloading updates.", e));
            logger.log(Level.INFO, "[updateIPDB] Failed to fetch update from remote API.");
        }
    }

    private void downloadFile(String fileURL, String savePath) {
        try {
            URL url = new URL(fileURL);
            URLConnection connection = url.openConnection();
            int fileSize = connection.getContentLength();

            try (InputStream in = new ProgressMonitorInputStream(null, "Downloading file", connection.getInputStream());
                 FileOutputStream outputStream = new FileOutputStream(savePath)) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                int totalBytesRead = 0;

                while ((bytesRead = in.read(buffer, 0, 1024)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                    int progress = (int) ((totalBytesRead / (double) fileSize) * 100);
                    // A download progress bar would be awesome asf
                    logger.log(Level.INFO, "Download progress: {0}%", progress);
                }
                Utility.showInfo("File Downloaded", "New version of IPDB has been downloaded successfully.");
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to perform required IO activities.", e);
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
