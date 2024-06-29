package loganalyzer;

import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static loganalyzer.ApacheParser.parseTimestampSpecial;

public class SpecialApache {
    public static void main(String[] args) {
        String filePath = "C:\\Users\\teebow1e\\IdeaProjects\\project1-soict\\src\\main\\resources\\samplelog\\apache_access_log.log";
        LocalDate targetDate = LocalDate.of(2024, 4, 17); // The date you're interested in
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy", java.util.Locale.ENGLISH);

        String datetocheck = "2024-06-30";
        List<String> relevantLogs = findLogsForDate(filePath, datetocheck, formatter);
        relevantLogs.forEach(System.out::println);
    }

    private static List<String> findLogsForDate(String filePath, String targetDate, DateTimeFormatter formatter) {
        System.out.println("func called");
        List<String> logs = new ArrayList<>();
        try (RandomAccessFile file = new RandomAccessFile(filePath, "r")) {
            long low = 0;
            long high = file.length();
            System.out.println(high);
            long firstPos = findFirstLinePos(file, low, high, targetDate, formatter);

            if (firstPos == -1) {
                return logs;
            }

            file.seek(firstPos);
            String line;
            while ((line = file.readLine()) != null) {
                System.out.println(line);
                String date = parseTimestampSpecial(line);
                if (!date.equals(targetDate)) {
                    break;
                }
                logs.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return logs;
    }

    private static long findFirstLinePos(RandomAccessFile file, long low, long high, String targetDate1, DateTimeFormatter formatter) throws Exception {
        System.out.println("caled 2");
        long mid = 0;
        LocalDate targetDate = null;
        while (low <= high) {
            mid = low + (high - low) / 2;
            file.seek(mid);
            if (mid != 0) {
                file.readLine();
            }
            String line = file.readLine();
            if (line == null) {
                break;
            }
            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd/MMM/yyyy");
            LocalDate date = LocalDate.parse(parseTimestampSpecial(line), formatter1);
            targetDate = LocalDate.parse(targetDate1);

            System.out.println(date);
            System.out.println(targetDate);
            if (date.isBefore(targetDate)) {
                low = mid + 1;
            } else if (date.isAfter(targetDate)) {
                high = mid - 1;
            } else { // Found the target date, now find the first occurrence
                long pos = file.getFilePointer() - line.length() - 2; // 2 for line break
                if (pos <= 0) {
                    return 0;
                }
                high = pos - 1;
            }
        }

        // After finding the date, move backward to find the first line with the date
        return high < 0 ? -1 : backtrackToFirstLogWithDate(file, high, targetDate, formatter);
    }

    private static long backtrackToFirstLogWithDate(RandomAccessFile file, long pos, LocalDate targetDate, DateTimeFormatter formatter) throws Exception {
        long currentPos = pos;
        file.seek(currentPos);
        while (currentPos > 0) {
            file.seek(currentPos);
            file.readLine();
            currentPos = file.getFilePointer();
            String line = file.readLine();
            if (line == null || (LocalDate.parse(parseTimestampSpecial(line))).equals(targetDate)) {
                break;
            }
            pos = currentPos;
        }
        return pos;
    }
}
