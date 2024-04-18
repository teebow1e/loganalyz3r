package loganalyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class LogParser {
    public static void main(String[] args) {
        String logFilePath = "logs/apache/access_log_100.log";
        Path logPath = Paths.get(logFilePath);

        try {
            if (Files.exists(logPath)) {
                List<String> lines = Files.readAllLines(logPath);
                System.out.println("First 5 lines of " + logPath.getFileName() + ":");
                int lineCount = 0;
                for (String line : lines) {
                    System.out.println(line);
                    lineCount++;
                    if (lineCount >= 5) {
                        break;
                    }
                }
            } else {
                System.out.println("Log file does not exist: " + logPath);
            }
        } catch (IOException e) {
            System.out.println("Error reading log file: " + e.getMessage());
        }
    }
}
