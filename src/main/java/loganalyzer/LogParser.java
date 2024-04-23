package loganalyzer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.regex.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import csvgenerator.CsvGenerator;
import static utility.Utility.findFirstMatch;
import static utility.Utility.readFile;

public class LogParser {
    private static final Pattern ipAddrPattern = Pattern.compile("((\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})|([0-9a-fA-F]{1,4}(:[0-9a-fA-F]{1,4}){7}))");
    private static final Pattern timestampPattern = Pattern.compile("\\[(\\d{2}/[A-Za-z]{3}/\\d{4}:\\d{2}:\\d{2}:\\d{2} [+\\-]\\d{4})]");
    private static final Pattern userAgentPattern = Pattern.compile("\"([^\"]*)\"[^\"]*$");
    public static void main(String[] args) {
        Logger logger = Logger.getLogger(LogParser.class.getName());
        String logFilePath = System.getProperty("user.dir") + "\\logs\\apache_nginx\\access_log_100.log";
        System.out.println(System.getProperty("user.dir"));
        Path logPath = Paths.get(logFilePath);
        LinkedList<String> lines = new LinkedList<>();

        if (Files.exists(logPath)) {
            lines = readFile(logFilePath, logger);

            LinkedList<Log> logList = new LinkedList<>();
            for (String workingLine : lines) {
                logList.add(new Log(
                        parseIpAddress(workingLine),
                        parseTimestamp(workingLine),
                        parseAllInOne(workingLine)[5],
                        parseAllInOne(workingLine)[7].replace("\"", ""),
                        parseAllInOne(workingLine)[6].replace("\"", ""),
                        Integer.parseInt(parseAllInOne(workingLine)[8]),
                        Integer.parseInt(parseAllInOne(workingLine)[9]),
                        parseUserAgent(workingLine)
                ));
            }
            try {
                CsvGenerator.generateCSV(logList);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error generating CSV: {0}", e.getMessage());
            }
        } else {
            logger.log(Level.SEVERE, "Log file not found at location {0}", logFilePath);
        }
    }
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
}
