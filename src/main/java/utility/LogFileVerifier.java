package utility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

public class LogFileVerifier {
    private static final Pattern MODSEC_JSON_PATTERN = Pattern.compile(".*\"transaction\".*|.*\"messages\".*|.*\"details\".*");
    private static final Pattern MODSEC_PLAIN_PATTERN = Pattern.compile(".*\\[client\\].*|.*\\[msg\\].*|.*\\[severity\\].*|.*\\[hostname\\].*");
    private static final Pattern APACHE_LOG_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3} - .* \\[.*\\] \".*\" \\d{3} .*");

    public static boolean isModSecLogFile(String filePath) throws IOException {
        return checkFilePattern(filePath, MODSEC_JSON_PATTERN, MODSEC_PLAIN_PATTERN);
    }

    public static boolean isApacheLogFile(String filePath) throws IOException {
        return checkFilePattern(filePath, APACHE_LOG_PATTERN);
    }

    private static boolean checkFilePattern(String filePath, Pattern... patterns) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null && count < 5) {
                for (Pattern pattern : patterns) {
                    if (pattern.matcher(line).matches()) {
                        return true;
                    }
                }
                count++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public static void main(String[] args) {
        try {
            String filePath = "logs/modsecurity/modsec_audit.json";
            if (isModSecLogFile(filePath)) {
                System.out.println("[DEBUG] This is a ModSecurity log file.");
            } else if (isApacheLogFile(filePath)) {
                System.out.println("[DEBUG] This is an Apache log file.");
            } else {
                System.out.println("[DEBUG] Unknown log file type.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
