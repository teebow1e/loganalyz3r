package loganalyzer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jdk.jshell.execution.Util;
import utility.Utility;
import utility.Utility.*;

public class ModSecurityParser {
    private ModSecurityParser() {
        throw new IllegalStateException("Utility class");
    }

    public static void main(String[] args) {
        Logger logger = Logger.getLogger(ModSecurityParser.class.getName());
        String logFilePath = System.getProperty("user.dir") + "/logs/modsecurity/modsec_audit.json";
        Path logPath = Paths.get(logFilePath);
        ObjectMapper objectMapper = new ObjectMapper();

        if (Files.exists(logPath)) {
            try (BufferedReader reader = Files.newBufferedReader(logPath)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    JsonNode jsonNode = objectMapper.readTree(line);
                    String timestamp = jsonNode.path("transaction").path("time").asText();
                    String transactionId = jsonNode.path("transaction").path("transaction_id").asText();
                    String remoteAddress = jsonNode.path("transaction").path("remote_address").asText();
                    int remotePort = jsonNode.path("transaction").path("remote_port").asInt();
                    String requestLine = jsonNode.path("request").path("request_line").asText();
                    String auditData = jsonNode.path("audit_data").path("messages").get(0).asText();


                    String attackType = parseAttackType(auditData);
                    String attackMsg = parseAttackMsg(auditData);
                    String attackData = parseAttackData(auditData);
                    String severity = parseSeverity(auditData);

                    System.out.println(timestamp);
                    System.out.println(transactionId);
                    System.out.println(remoteAddress);
                    System.out.println(remotePort);
                    System.out.println(requestLine);
                    System.out.println(attackType);
                    System.out.println(attackMsg);
                    System.out.println(attackData);
                    System.out.println(severity);
                    System.out.println("----");
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error reading the log file");
            }
        } else {
            logger.log(Level.SEVERE, "Log file not found at location {0}", logFilePath);
        }
    }

    private static String parseAttackType(String data) {
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

    private static String parseAttackMsg(String data) {
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

    private static String parseSeverity(String data) {
        String regex = "\\[severity\\s+\"([^\"]*)\"\\]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(data);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }
}
