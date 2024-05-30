package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static dataanalyzer.IpLookUp.IpCheck;

public class DashboardController {

    @FXML
    private VBox mainVBox;

    @FXML
    private BarChart<String, Number> logBarChart;

    @FXML
    private ComboBox<String> timeIntervalComboBox;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<String> startTimeComboBox;

    @FXML
    private TableView<String[]> statusCodeRankingTable;

    @FXML
    private TableColumn<String[], String> statusCodeColumn;

    @FXML
    private TableColumn<String[], Integer> statusCodeCountColumn;

    @FXML
    private TableView<String[]> timestampRankingTable;

    @FXML
    private TableColumn<String[], String> timestampColumn;

    @FXML
    private TableColumn<String[], Integer> timestampCountColumn;

    @FXML
    private TableView<String[]> ipRankingTable;

    @FXML
    private TableColumn<String[], String> ipColumn;

    @FXML
    private TableColumn<String[], Integer> ipCountColumn;

    @FXML
    private TableColumn<String[], String> ipCountryColumn;

    @FXML
    private TableColumn<String[], String> modsecRuleColumn;

    @FXML
    private TableColumn<String[], Integer> modsecRuleCountColumn;

    @FXML
    private TableView<String[]> ruleCountTable;


    private List<LogEntry> logEntries;
    private static final int MAX_DISPLAYED_TIMESTAMPS = 10;

    @FXML
    private void initialize() {
        try {
            logEntries = parseLogFile("logs/apache_nginx/access_log_50000.log");
            setupComboBox();
            setupDatePicker();
            setupStartTimeComboBox();
            setupTableViews();
            setupModsecTable();
            displayLogsByInterval("15 Minutes", LocalDate.now());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupComboBox() {
        timeIntervalComboBox.setItems(FXCollections.observableArrayList("15 Minutes", "30 Minutes", "1 Hour", "2 Hours", "12 Hours", "1 Day"));
        timeIntervalComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                displayLogsByInterval(newValue, datePicker.getValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        timeIntervalComboBox.getSelectionModel().selectFirst();
    }

    private void setupDatePicker() {
        datePicker.setValue(LocalDate.now());
        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            try {
                displayLogsByInterval(timeIntervalComboBox.getSelectionModel().getSelectedItem(), newValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void setupStartTimeComboBox() {
        ObservableList<String> times = FXCollections.observableArrayList();
        for (int hour = 0; hour < 24; hour++) {
            times.add(String.format("%02d:00", hour));
        }
        startTimeComboBox.setItems(times);
        startTimeComboBox.getSelectionModel().select("00:00"); // Default selection
        startTimeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                displayLogsByInterval(timeIntervalComboBox.getSelectionModel().getSelectedItem(), datePicker.getValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    private void setupModsecTable() {
        modsecRuleColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()[0]));
        modsecRuleCountColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(Integer.parseInt(cellData.getValue()[1])).asObject());
        updateModsecRuleTable();
    }

    private void updateModsecRuleTable() {
        String filePath = "logs/parsed/modsecurity.csv";
        Map<String, Integer> ruleCounts = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("remoteAddress")) {
                    continue;
                }
                String[] parts = line.split(",");
                String ruleName = parts[7];
                if (!ruleName.startsWith("REQUEST")) {
                    continue;
                }
                ruleCounts.merge(ruleName, 1, Integer::sum);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ObservableList<String[]> items = ruleCountTable.getItems();
        items.clear();

        ruleCounts.forEach((rule, count) -> items.add(new String[]{rule, count.toString()}));
    }


    private void setupTableViews() {
        // Setup the status code ranking table
        statusCodeColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()[0]));
        statusCodeCountColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(Integer.parseInt(cellData.getValue()[1])).asObject());

        // Setup the timestamp ranking table
        timestampColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()[0]));
        timestampCountColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(Integer.parseInt(cellData.getValue()[1])).asObject());

        // Setup the IP ranking table
        ipColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()[0]));
        ipCountColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(Integer.parseInt(cellData.getValue()[1])).asObject());
        ipCountryColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue()[2]));
    }

    private List<LogEntry> parseLogFile(String filePath) throws Exception {
        List<LogEntry> entries = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                String ipAddress = parts[0];
                String dateStr = parts[3].substring(1) + " " + parts[4].substring(0, parts[4].length() - 1);
                Date date = dateFormat.parse(dateStr);
                int statusCode = Integer.parseInt(parts[8]);
                entries.add(new LogEntry(ipAddress, date, statusCode));
            }
        }
        return entries;
    }

    private void displayLogsByInterval(String interval, LocalDate selectedDate) throws IOException {
        // Ensure selectedTime is not null
        String selectedTime = startTimeComboBox.getSelectionModel().getSelectedItem();
        if (selectedTime == null) {
            selectedTime = "00:00";
        }

        Map<String, Map<String, Integer>> groupedLogs = groupLogsByInterval(interval, selectedDate);

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

        // Limit to max entries
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

        // Update rankings
        updateStatusCodeRanking(groupedLogs);
        updateTimestampRanking(groupedLogs);
        updateIpRanking(groupedLogs, selectedDate);
    }

    private Map<String, Map<String, Integer>> groupLogsByInterval(String interval, LocalDate selectedDate) {
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

        String selectedTime = startTimeComboBox.getSelectionModel().getSelectedItem();
        if (selectedTime == null) {
            selectedTime = "00:00";
        }
        int startHour = Integer.parseInt(selectedTime.split(":")[0]);

        for (LogEntry entry : logEntries) {
            calendar.setTime(entry.date);
            LocalDate entryDate = entry.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (entryDate.equals(selectedDate) && calendar.get(Calendar.HOUR_OF_DAY) >= startHour) {
                adjustCalendar(calendar, field, amount);
                String timeSlot = dateFormat.format(calendar.getTime());
                String statusRange = getStatusRange(entry.statusCode);

                groupedLogs.computeIfAbsent(timeSlot, k -> new HashMap<>()).merge(statusRange, 1, Integer::sum);
            }
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

    private void updateStatusCodeRanking(Map<String, Map<String, Integer>> groupedLogs) {
        Map<String, Integer> statusCodeCounts = new HashMap<>();

        // Iterate over the grouped logs for the selected time interval
        for (Map<String, Integer> statusCounts : groupedLogs.values()) {
            for (Map.Entry<String, Integer> entry : statusCounts.entrySet()) {
                statusCodeCounts.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }
        }

        // Clear previous data in the table
        ObservableList<String[]> items = statusCodeRankingTable.getItems();
        items.clear();

        // Sort the status codes based on their counts
        List<Map.Entry<String, Integer>> sortedStatusCodes = new ArrayList<>(statusCodeCounts.entrySet());
        sortedStatusCodes.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        // Add the sorted status codes to the table
        for (Map.Entry<String, Integer> entry : sortedStatusCodes) {
            items.add(new String[]{entry.getKey(), entry.getValue().toString()});
        }
    }

    private void updateTimestampRanking(Map<String, Map<String, Integer>> groupedLogs) {
        List<Map.Entry<String, Map<String, Integer>>> sortedTimestamps = new ArrayList<>(groupedLogs.entrySet());
        sortedTimestamps.sort((e1, e2) -> {
            int sum1 = e1.getValue().values().stream().mapToInt(Integer::intValue).sum();
            int sum2 = e2.getValue().values().stream().mapToInt(Integer::intValue).sum();
            return Integer.compare(sum2, sum1);
        });

        ObservableList<String[]> items = timestampRankingTable.getItems();
        items.clear();

        // Ensure we do not access beyond the size of the list
        int limit = Math.min(sortedTimestamps.size(), 7);
        for (int i = 0; i < limit; i++) {
            Map.Entry<String, Map<String, Integer>> entry = sortedTimestamps.get(i);
            int sum = entry.getValue().values().stream().mapToInt(Integer::intValue).sum();
            items.add(new String[]{entry.getKey(), String.valueOf(sum)});
        }
    }

    private void updateIpRanking(Map<String, Map<String, Integer>> groupedLogs, LocalDate selectedDate) throws IOException {
        Map<String, Integer> ipCounts = new HashMap<>();

        // Iterate over log entries to count requests by IP
        for (LogEntry entry : logEntries) {
            LocalDate entryDate = entry.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (entryDate.equals(selectedDate)) {
                ipCounts.merge(entry.ipAddress, 1, Integer::sum);
            }
        }

        ObservableList<String[]> items = ipRankingTable.getItems();
        items.clear();

        // Sort the IPs based on their counts
        List<Map.Entry<String, Integer>> sortedIps = new ArrayList<>(ipCounts.entrySet());
        sortedIps.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        // Add the sorted IPs to the table
        for (Map.Entry<String, Integer> entry : sortedIps) {
            items.add(new String[]{entry.getKey(), entry.getValue().toString(), IpCheck(entry.getKey())});
        }
    }

    private static class LogEntry {
        String ipAddress;
        Date date;
        int statusCode;

        LogEntry(String ipAddress, Date date, int statusCode) {
            this.ipAddress = ipAddress;
            this.date = date;
            this.statusCode = statusCode;
        }
    }
}
