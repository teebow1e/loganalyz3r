package loganalyzer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.control.DatePicker;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModSecurityParser {
    private ModSecurityParser() {
        throw new IllegalStateException("Utility class");
    }

    public static String parseAttackType(String data) {
        String regex = "\\[file\\s+\"([^\"]*)\"\\]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(data);

        if (matcher.find()) {
            String filePath = matcher.group(1);
            String filename = filePath.substring(filePath.lastIndexOf('/') + 1);
            return filename.replace(".conf", "");
        } else {
            return null;
        }
    }

    public static String parseAttackMsg(String data) {
        String regex = "\\[msg\\s+\"([^\"]*)\"\\]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(data);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    public static String parseAttackData(String logEntry) {
        String regex = "\\[data\\s+\"([^\"]*)\"\\]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(logEntry);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    public static String parseSeverity(String data) {
        String regex = "\\[severity\\s+\"([^\"]*)\"\\]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(data);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    public static String parseVersion(String data) {
        String regex = "(?<=\\/)(\\d+\\.\\d+\\.\\d+)(?= \\()";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(data);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    public static List<ModSecurity> parseModSecByDate(DatePicker datePicker) {
        Logger logger = Logger.getLogger(ApacheParser.class.getName());
        // CONSTANT VALUE HERE
        String logFilePath = System.getProperty("user.dir")
                + File.separator
                + "logs"
                + File.separator
                + "modsecurity"
                + File.separator
                + "modsec_audit_new.log";
        Path logPath = Paths.get(logFilePath);
        ObjectMapper objectMapper = new ObjectMapper();
        List<ModSecurity> logList = new LinkedList<>();
        LocalDate selectedDate = datePicker.getValue();

        if (Files.exists(logPath)) {
            try (BufferedReader br = new BufferedReader(new FileReader(logFilePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    JsonNode jsonNode = objectMapper.readTree(line);
                    String auditData = jsonNode.path("audit_data").path("messages").get(0).asText();
                    String requestLine = jsonNode.path("request").path("request_line").asText();
                    String[] requestParts = requestLine.split(" ");
                    JsonNode versionSection = jsonNode.path("audit_data").path("producer");
                    String potentialVersion;
                    if (versionSection.isMissingNode() || versionSection.isNull()) {
                        potentialVersion = "";
                    } else {
                        potentialVersion = parseVersion(versionSection.get(0).asText());
                    }
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss.SSSSSS Z", Locale.ENGLISH);

                    String timestamp = jsonNode.path("transaction").path("time").asText();
                    LocalDate logDate = LocalDate.parse(timestamp, formatter);

                    if (logDate.equals(selectedDate)) {
                        logList.add(new ModSecurity(
                                potentialVersion,
                                jsonNode.path("transaction").path("time").asText(),
                                jsonNode.path("transaction").path("transaction_id").asText(),
                                jsonNode.path("transaction").path("remote_address").asText(),
                                requestParts[1],
                                requestParts[0],
                                requestParts[2],
                                jsonNode.path("response").path("status").asInt(),
                                jsonNode.path("request").path("headers").path("User-Agent").asText(),
                                parseAttackType(auditData),
                                parseAttackMsg(auditData),
                                parseAttackData(auditData),
                                parseSeverity(auditData)
                        ));
                    }
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            logger.log(Level.SEVERE, "Log file not found at location {0}", logFilePath);
        }
        return logList;
    }
}