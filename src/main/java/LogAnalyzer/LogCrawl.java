package LogAnalyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class LogCrawl {
    public static void main(String[] args) {
        // Define the path to the log file
        String logFilePath = "logs/apache/access_log_100.log";

        // Create a Path object for the log file
        Path logPath = Paths.get(logFilePath);

        try {
            // Check if the log file exists
            if (Files.exists(logPath)) {
                // Read the contents of the log file
                List<String> lines = Files.readAllLines(logPath);

                // Print the first 5 lines of the log file
                System.out.println("First 5 lines of " + logPath.getFileName() + ":");
                int lineCount = 0;
                for (String line : lines) {
                    System.out.println(line);
                    lineCount++;
                    if (lineCount >= 5) {
                        break; // Exit the loop after reading 5 lines
                    }
                }
            } else {
                System.out.println("Log file does not exist: " + logPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
