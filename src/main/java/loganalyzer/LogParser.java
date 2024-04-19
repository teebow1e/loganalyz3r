package loganalyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import csvgenerator.CsvGenerator;

public class LogParser {
    private static final Pattern ipAddrPattern = Pattern.compile("((\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})|([0-9a-fA-F]{1,4}(:[0-9a-fA-F]{1,4}){7}))");
    private static final Pattern timestampPattern = Pattern.compile("\\[(\\d{2}/[A-Za-z]{3}/\\d{4}:\\d{2}:\\d{2}:\\d{2} [+\\-]\\d{4})]");
    private static final Pattern userAgentPattern = Pattern.compile("\"([^\"]*)\"[^\"]*$");
    public static void main(String[] args) {
        Logger logger = Logger.getLogger(LogParser.class.getName());
        String logFilePath = System.getProperty("user.dir") + "/logs/apache_nginx/access_log_50000.log";
        Path logPath = Paths.get(logFilePath);

        try {
            if (Files.exists(logPath)) {
                List<String> lines = Files.readAllLines(logPath);
                LinkedList<Log> logList = new LinkedList<>();
                for (String workingLine : lines) {
                    logList.add(new Log(
                            parseIpAddress(workingLine),
                            parseTimestamp(workingLine),
                            parseAllInOne(workingLine)[0],
                            parseAllInOne(workingLine)[2],
                            parseAllInOne(workingLine)[1],
                            Integer.parseInt(parseAllInOne(workingLine)[3]),
                            Integer.parseInt(parseAllInOne(workingLine)[4]),
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
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading log file: {0}", e.getMessage());
        }
    }

    public static String findFirstMatch(String line, String pattern) {
        Pattern patternToMatch = Pattern.compile(pattern);
        Matcher matcher = patternToMatch.matcher(line);
        if (matcher.find()) {
            return line.substring(matcher.start(), matcher.end());
        }
        return null;
    }

    public static String parseIpAddress(String logLine) {
        return findFirstMatch(logLine, ipAddrPattern.pattern());
    }
    public static String parseTimestamp(String logLine) {
        String parsedLog = findFirstMatch(logLine, timestampPattern.pattern());
        if (parsedLog != null) {
            return parsedLog.substring(1, parsedLog.length() - 1);
        } else {
            return null;
        }
    }
    public static String parseUserAgent(String logLine) {
        String parsedLog = findFirstMatch(logLine, userAgentPattern.pattern());
        if (parsedLog != null) {
            return parsedLog.substring(1, parsedLog.length() - 1);
        } else {
            return null;
        }
    }
    public static String[] parseAllInOne(String logLine) {
        String[] aioParts = logLine.split(" ");
        String[] finalAio = Arrays.copyOfRange(aioParts, 5, 10);
        for (int i = 0; i < finalAio.length; i++) {
            finalAio[i] = finalAio[i].replace("\"", "");
        }
        return finalAio;
    }
}
