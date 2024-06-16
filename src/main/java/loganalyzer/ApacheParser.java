package loganalyzer;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import entrypoint.Config;
import javafx.scene.control.DatePicker;

import static utility.Utility.findFirstMatch;
import static utility.Utility.getElementSafely;

public class ApacheParser {
    private ApacheParser() {
        throw new IllegalStateException("Utility class");
    }
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

    public static String parseMethod(String[] aioArr) {
        String tempMethodValue = getElementSafely(aioArr, 5);
        if (tempMethodValue != null) {
            return tempMethodValue.replace("\"", "");
        }
        return null;
    }

    public static String parseProtocol(String[] aioArr) {
        String tempMethodValue = getElementSafely(aioArr, 7);
        if (tempMethodValue != null) {
            return tempMethodValue.replace("\"", "");
        }
        return null;
    }

    public static String parseRequestPath(String[] aioArr) {
        String tempMethodValue = getElementSafely(aioArr, 6);
        if (tempMethodValue != null) {
            return tempMethodValue.replace("\"", "");
        }
        return null;
    }

    public static int parseStatusCode(String[] aioArr) {
        String tempMethodValue = getElementSafely(aioArr, 8);
        if (tempMethodValue != null) {
            return Integer.parseInt(tempMethodValue.replace("\"", ""));
        }
        return 0;
    }

    public static int parseContentLength(String[] aioArr) {
        String tempMethodValue = getElementSafely(aioArr, 9);
        if (tempMethodValue != null) {
            return Integer.parseInt(tempMethodValue.replace("\"", ""));
        }
        return 0;
    }

    public static Apache parseLogLine(String line) {
        String[] aioArr = parseAllInOne(line);
        return new Apache(
                parseIpAddress(line),
                parseTimestamp(line),
                parseMethod(aioArr),
                parseProtocol(aioArr),
                parseRequestPath(aioArr),
                parseStatusCode(aioArr),
                parseContentLength(aioArr),
                parseUserAgent(line)
        );
    }

    public static List<Apache> parseApacheByDate(DatePicker datePicker) {
        Logger logger = Logger.getLogger(ApacheParser.class.getName());
        // CONSTANT VALUE HERE
        String logFilePath = Config.getApacheLogLocation();
        Path logPath = Paths.get(logFilePath);
        List<Apache> logList = new LinkedList<>();
        LocalDate selectedDate = datePicker.getValue();

        if (Files.exists(logPath)) {
            try (BufferedReader br = new BufferedReader(new FileReader(logFilePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String timestamp = parseTimestamp(line);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
                    LocalDate logDate = LocalDate.parse(timestamp, formatter);

                    if (logDate.equals(selectedDate)) {
                        // at this point, every line put to this code can be parsed
                        // however, some of them might be invalid, but still be able to be parsed
                        // we need to make sure that only valid log will be pushed into this application
                        // also pushed a notification if there's un-parse-able log in the process
                        logList.add(parseLogLine(line));
                    }
                }
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
