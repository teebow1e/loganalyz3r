package loganalyzer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static utility.Utility.*;

public class ModSecurityParser {
    private ModSecurityParser() {
        throw new IllegalStateException("Utility class");
    }

    private static final Pattern timeStampPattern = Pattern.compile("\\[(.*?)\\]");
    private static final Pattern ipAddrPattern = Pattern.compile("\\[client .*?\\]");
    private static final Pattern aioPattern = Pattern.compile("\\[[^\\[\\]]*?\\]");
    public static void main (String[] args) {
        Logger logger = Logger.getLogger(ModSecurityParser.class.getName());
        String logFilePath = System.getProperty("user.dir") + "/logs/modsecurity/error.log";
        Path logPath = Paths.get(logFilePath);
        LinkedList<String> lines = new LinkedList<>();

        if (Files.exists(logPath)) {
            lines = readFile(logFilePath, logger);

            for (String line: lines) {
                System.out.println(parseLogType(line));
            }
        } else {
            logger.log(Level.SEVERE, "Log file not found at location {0}", logFilePath);
        }
    }
    // todo: datetime object?
    private static String parseTimestamp(String line) {
        String tempResult = findFirstMatch(line, timeStampPattern);
        if (tempResult != null) {
            return tempResult.replaceAll("\\[|\\]", "");
        } else {
            return "";
        }
    }
    private static String parseIpAddr(String line) {
        String tempResult = findFirstMatch(line, ipAddrPattern);
        if (tempResult != null) {
            return tempResult.replaceAll("\\[|\\]|client\s", "");
        } else {
            return "";
        }
    }

    private static String parseLogType (String line) {
        Matcher matcher = aioPattern.matcher(line);
        for (int i = 0; i < 2 && matcher.find(); i++) {
            if (i == 1) {
                return matcher.group().replaceAll("\\[|\\]", "");
            }
        }
        return "N/A";
    }
}
