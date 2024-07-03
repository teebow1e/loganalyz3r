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

import static utility.Utility.*;

public class ApacheParser {
    private ApacheParser() {
        throw new IllegalStateException("Utility class");
    }
    private static final Pattern ipAddrPattern = Pattern.compile("((\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})|([0-9a-fA-F]{1,4}(:[0-9a-fA-F]{1,4}){7}))");
    private static final Pattern ipv6AddrPattern = Pattern.compile("([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\\\.){3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\\\.){3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])");
    private static final Pattern timestampPattern = Pattern.compile("\\[(\\d{2}/[A-Za-z]{3}/\\d{4}:\\d{2}:\\d{2}:\\d{2} [+\\-]\\d{4})]");
    private static final Pattern userAgentPattern = Pattern.compile("\"([^\"]*)\"[^\"]*$");
    private static final Logger logger = Logger.getLogger(ApacheParser.class.getName());
    public static String parseIpAddress(String logLine) {
        if (findFirstMatch(logLine, ipAddrPattern) == null) {
            return findFirstMatch(logLine, ipv6AddrPattern);
        } else {
            return findFirstMatch(logLine, ipAddrPattern);
        }
    }
    public static String parseTimestamp(String logLine) {
        String parsedLog = findFirstMatch(logLine, timestampPattern);
        if (parsedLog != null) {
            try {
                return parsedLog.substring(1, parsedLog.length() - 1);
            } catch (Exception e) {
                return Config.NOT_AVAILABLE_TEXT;
            }
        } else {
            return Config.NOT_AVAILABLE_TEXT;
        }
    }
    public static String parseUserAgent(String logLine) {
        String parsedLog = findFirstMatch(logLine, userAgentPattern);
        if (parsedLog != null) {
            try {
                return parsedLog.substring(1, parsedLog.length() - 1);
            } catch (Exception e) {
                return Config.NOT_AVAILABLE_TEXT;
            }
        } else {
            return Config.NOT_AVAILABLE_TEXT;
        }
    }

    public static String[] parseAllInOne(String logLine) {
        return logLine.split(" ");
    }

    public static String parseMethod(String[] aioArr) {
        String tempMethodValue = getElementSafely(aioArr, 5);
        if (tempMethodValue != null) {
            try {
                return tempMethodValue.replace("\"", "");
            } catch (Exception e) {
                return Config.NOT_AVAILABLE_TEXT;
            }
        }
        return Config.NOT_AVAILABLE_TEXT;
    }

    public static String parseProtocol(String[] aioArr) {
        String tempMethodValue = getElementSafely(aioArr, 7);
        if (tempMethodValue != null) {
            try {
                return tempMethodValue.replace("\"", "");
            } catch (Exception e) {
                return Config.NOT_AVAILABLE_TEXT;
            }
        }
        return Config.NOT_AVAILABLE_TEXT;
    }

    public static String parseRequestPath(String[] aioArr) {
        String tempMethodValue = getElementSafely(aioArr, 6);
        if (tempMethodValue != null) {
            try {
                return tempMethodValue.replace("\"", "");
            } catch (Exception e) {
                return Config.NOT_AVAILABLE_TEXT;
            }
        }
        return Config.NOT_AVAILABLE_TEXT;
    }

    public static int parseStatusCode(String[] aioArr) {
        String tempMethodValue = getElementSafely(aioArr, 8);
        if (tempMethodValue != null) {
            try {
                return Integer.parseInt(tempMethodValue.replace("\"", ""));
            } catch (Exception e) {
                return 0;
            }

        }
        return 0;
    }

    public static int parseContentLength(String[] aioArr) {
        String tempMethodValue = getElementSafely(aioArr, 9);
        if (tempMethodValue != null) {
            try {
                return Integer.parseInt(tempMethodValue.replace("\"", ""));
            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }

    public static Apache parseLogLine(String line) {
        try {
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
        } catch (Exception e) {
            logger.log(Level.WARNING, "An error occurred during parsing phase: {0}", e);
        }
        return null;
    }

    public static List<Apache> parseApacheByDate(DatePicker datePicker) {
        String logFilePath = Config.getApacheLogLocation();
        Path logPath = Paths.get(logFilePath);
        List<Apache> logList = new LinkedList<>();
        LocalDate selectedDate = datePicker.getValue();
        System.out.println(selectedDate);

        if (Files.exists(logPath)) {
            try (BufferedReader br = new BufferedReader(new FileReader(logFilePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String timestamp = parseTimestamp(line);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
                    LocalDate logDate = LocalDate.parse(timestamp, formatter);

                    if (logDate.equals(selectedDate)) {
                        logList.add(parseLogLine(line));
                    }
                }
            } catch (IOException e) {
                logger.log(Level.WARNING, "Failed to perform IO activities on log file.");
                throw new RuntimeException(e);
            }
        }
        else {
            String errMsg = String.format("Apache Log file not found at location %s", logFilePath);
            logger.log(Level.WARNING, errMsg);
            showAlert("ERROR", errMsg);
        }
        return logList;
    }
}
