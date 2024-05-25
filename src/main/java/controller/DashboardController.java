package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Tooltip;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.*;

public class DashboardController {

    @FXML
    private VBox mainVBox;

    @FXML
    private BarChart<String, Number> logBarChart;

    @FXML
    private ComboBox<String> timeIntervalComboBox;

    private List<LogEntry> logEntries;
    private static final int MAX_DISPLAYED_TIMESTAMPS = 7;

    @FXML
    private void initialize() {
        try {
            logEntries = parseLogFile("logs/apache_nginx/access_log_10000.log");
            setupComboBox();
            displayLogsByInterval("15 Minutes");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupComboBox() {
        timeIntervalComboBox.setItems(FXCollections.observableArrayList("15 Minutes", "30 Minutes", "1 Hour", "2 Hours", "12 Hours", "1 Day"));
        timeIntervalComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                displayLogsByInterval(newValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        timeIntervalComboBox.getSelectionModel().selectFirst();
    }

    private List<LogEntry> parseLogFile(String filePath) throws Exception {
        List<LogEntry> entries = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                String dateStr = parts[3].substring(1) + " " + parts[4].substring(0, parts[4].length() - 1);
                Date date = dateFormat.parse(dateStr);
                int statusCode = Integer.parseInt(parts[8]);
                entries.add(new LogEntry(date, statusCode));
            }
        }
        return entries;
    }

    private void displayLogsByInterval(String interval) {
        Map<String, Map<String, Integer>> groupedLogs = groupLogsByInterval(interval);

        // Clear previous data
        logBarChart.getData().clear();

        // Create series color
        XYChart.Series<String, Number> series200 = new XYChart.Series<>();
        series200.setName("200-299");
        XYChart.Series<String, Number> series300 = new XYChart.Series<>();
        series300.setName("300-399");
        XYChart.Series<String, Number> series400 = new XYChart.Series<>();
        series400.setName("400-499");
        XYChart.Series<String, Number> series500 = new XYChart.Series<>();
        series500.setName("500-599");

        List<Map.Entry<String, Map<String, Integer>>> entries = new ArrayList<>(groupedLogs.entrySet());

        // Sort entries by date
        entries.sort(Comparator.comparing(Map.Entry::getKey));

        // Limit to max 6 entries
        List<Map.Entry<String, Map<String, Integer>>> displayedEntries = entries.size() > MAX_DISPLAYED_TIMESTAMPS ?
                entries.subList(0, MAX_DISPLAYED_TIMESTAMPS) : entries;

        for (Map.Entry<String, Map<String, Integer>> entry : displayedEntries) {
            String timeSlot = entry.getKey();
            Map<String, Integer> statusCounts = entry.getValue();

            XYChart.Data<String, Number> data200 = new XYChart.Data<>(timeSlot, statusCounts.getOrDefault("200-299", 0));
            XYChart.Data<String, Number> data300 = new XYChart.Data<>(timeSlot, statusCounts.getOrDefault("300-399", 0));
            XYChart.Data<String, Number> data400 = new XYChart.Data<>(timeSlot, statusCounts.getOrDefault("400-499", 0));
            XYChart.Data<String, Number> data500 = new XYChart.Data<>(timeSlot, statusCounts.getOrDefault("500-599", 0));

            series200.getData().add(data200);
            series300.getData().add(data300);
            series400.getData().add(data400);
            series500.getData().add(data500);
        }

        List<XYChart.Series<String, Number>> seriesList = new ArrayList<>();
        seriesList.add(series200);
        seriesList.add(series300);
        seriesList.add(series400);
        seriesList.add(series500);

        logBarChart.getData().addAll(seriesList);
    }

    private Map<String, Map<String, Integer>> groupLogsByInterval(String interval) {
        Map<String, Map<String, Integer>> groupedLogs = new TreeMap<>();
        SimpleDateFormat dateFormat;
        Calendar calendar = Calendar.getInstance();

        int field = Calendar.MINUTE;
        int amount = 15;

        switch (interval) {
            case "30 Minutes":
                amount = 30;
                break;
            case "1 Hour":
                field = Calendar.HOUR_OF_DAY;
                amount = 1;
                break;
            case "2 Hours":
                field = Calendar.HOUR_OF_DAY;
                amount = 2;
                break;
            case "12 Hours":
                field = Calendar.HOUR_OF_DAY;
                amount = 12;
                break;
            case "1 Day":
                field = Calendar.DAY_OF_MONTH;
                amount = 1;
                break;
        }

        dateFormat = getDateFormat(interval);

        for (LogEntry entry : logEntries) {
            calendar.setTime(entry.date);
            adjustCalendar(calendar, field, amount);
            String timeSlot = dateFormat.format(calendar.getTime());
            String statusRange = getStatusRange(entry.statusCode);

            groupedLogs.computeIfAbsent(timeSlot, k -> new HashMap<>()).merge(statusRange, 1, Integer::sum);
        }

        return groupedLogs;
    }

    private SimpleDateFormat getDateFormat(String interval) {
        switch (interval) {
            case "15 Minutes":
            case "30 Minutes":
            case "1 Hour":
            case "2 Hours":
            case "12 Hours":
                return new SimpleDateFormat("yyyy-MM-dd HH:mm");
            case "1 Day":
                return new SimpleDateFormat("yyyy-MM-dd");
            default:
                throw new IllegalArgumentException("Unexpected interval: " + interval);
        }
    }

    private void adjustCalendar(Calendar calendar, int field, int amount) {
        if (field == Calendar.MINUTE) {
            calendar.set(field, (calendar.get(field) / amount) * amount);
        } else {
            calendar.set(field, (calendar.get(field) / amount) * amount);
            calendar.set(Calendar.MINUTE, 0);
        }
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private String getStatusRange(int statusCode) {
        if (statusCode >= 200 && statusCode < 300) {
            return "200-299";
        } else if (statusCode >= 300 && statusCode < 400) {
            return "300-399";
        } else if (statusCode >= 400 && statusCode < 500) {
            return "400-499";
        } else if (statusCode >= 500 && statusCode < 600) {
            return "500-599";
        } else {
            return "Other";
        }
    }

    private static class LogEntry {
        Date date;
        int statusCode;

        LogEntry(Date date, int statusCode) {
            this.date = date;
            this.statusCode = statusCode;
        }
    }
}
