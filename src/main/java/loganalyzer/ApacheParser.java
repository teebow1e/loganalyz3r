package loganalyzer;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import csvgenerator.CsvGenerator;
import javafx.scene.control.DatePicker;

import static utility.Utility.findFirstMatch;
import static utility.Utility.readFile;

public class ApacheParser {
    private static final Pattern ipAddrPattern = Pattern.compile("((\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})|([0-9a-fA-F]{1,4}(:[0-9a-fA-F]{1,4}){7}))");
    private static final Pattern timestampPattern = Pattern.compile("\\[(\\d{2}/[A-Za-z]{3}/\\d{4}:\\d{2}:\\d{2}:\\d{2} [+\\-]\\d{4})]");
    private static final Pattern userAgentPattern = Pattern.compile("\"([^\"]*)\"[^\"]*$");
    public static String parseIpAddress(String logLine) {
        return findFirstMatch(logLine, ipAddrPattern);
    }
    public static String parseTimestamp(String logLine) {
        String parsedLog = findFirstMatch(logLine, timestampPattern);
        if (parsedLog != null) {
            return parsedLog.substring(1, parsedLog.length() - 1);
        } else {
            return null;
        }
    }
    public static String parseUserAgent(String logLine) {
        String parsedLog = findFirstMatch(logLine, userAgentPattern);
        if (parsedLog != null) {
            return parsedLog.substring(1, parsedLog.length() - 1);
        } else {
            return null;
        }
    }
    public static String[] parseAllInOne(String logLine) {
        return logLine.split(" ");
    }

    public static void parseAndGenerateCSV() {
        Logger logger = Logger.getLogger(ApacheParser.class.getName());
        String logFilePath = System.getProperty("user.dir") + "\\logs\\apache_nginx\\access_log_1000.log";
        Path logPath = Paths.get(logFilePath);
        LinkedList<String> lines = new LinkedList<>();

        if (Files.exists(logPath)) {
            System.out.println("log path exists");
            lines = readFile(logFilePath, logger);
            LinkedList<Apache> logList = new LinkedList<>();
            for (String workingLine : lines) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z");
                logList.add(new Apache(
                        parseIpAddress(workingLine),
                        parseTimestamp(workingLine),
                        parseAllInOne(workingLine)[5].replace("\"", ""),
                        parseAllInOne(workingLine)[7].replace("\"", ""),
                        parseAllInOne(workingLine)[6].replace("\"", ""),
                        Integer.parseInt(parseAllInOne(workingLine)[8]),
                        Integer.parseInt(parseAllInOne(workingLine)[9]),
                        parseUserAgent(workingLine)
                ));
            }
            try {
                System.out.println("generating csv now..");
                CsvGenerator.generateCSVNormalLog(logList);
                System.out.println("done generate");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error generating CSV: {0}", e.getMessage());
            }
        } else {
            logger.log(Level.SEVERE, "Log file not found at location {0}", logFilePath);
        }
    }

    public static List<Apache> parseApacheByDate(DatePicker datePicker) {
        Logger logger = Logger.getLogger(ApacheParser.class.getName());
        String logFilePath = System.getProperty("user.dir") + "\\logs\\apache_nginx\\access_log_50000.log";
        Path logPath = Paths.get(logFilePath);
        LinkedList<String> lines;
        List<Apache> logList = new LinkedList<>();

        if (Files.exists(logPath)) {
            lines = readFile(logFilePath, logger);

            LocalDate selectedDate = datePicker.getValue();

            for (String workingLine : lines) {
                String timestamp = parseTimestamp(workingLine);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
                LocalDate logDate = LocalDate.parse(timestamp, formatter);

                if (logDate.equals(selectedDate)) {
                    logList.add(new Apache(
                            parseIpAddress(workingLine),
                            timestamp,
                            parseAllInOne(workingLine)[5].replace("\"", ""),
                            parseAllInOne(workingLine)[7].replace("\"", ""),
                            parseAllInOne(workingLine)[6].replace("\"", ""),
                            Integer.parseInt(parseAllInOne(workingLine)[8]),
                            Integer.parseInt(parseAllInOne(workingLine)[9]),
                            parseUserAgent(workingLine)
                    ));
                }
            }
        } else {
            logger.log(Level.SEVERE, "Log file not found at location {0}", logFilePath);
        }
        return logList;
    }


    public static void main(String[] args) {
        parseAndGenerateCSV();
    }
}
