package controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.chart.LineChart;
import javafx.stage.Stage;
import loganalyzer.Apache;
import loganalyzer.ModSecurity;
import ui.WebLogManager;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static loganalyzer.ApacheParser.*;
import static loganalyzer.ModSecurityParser.*;
import static utility.IpLookUp.checkIP;

public class DashboardController {
    @FXML
    private VBox mainVBox;
    @FXML
    private LineChart<String, Number> logLineChart;
    @FXML
    private ComboBox<String> timeIntervalComboBox;
    @FXML
    private DatePicker datePicker;
//    @FXML
//    private ComboBox<String> startTimeComboBox;
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
    private TableView<String[]> ruleCountTable;
    @FXML
    private TableColumn<String[], String> modsecRuleColumn;
    @FXML
    private TableColumn<String[], Integer> modsecRuleCountColumn;
    @FXML
    private PieChart pieChartData;

    private List<Apache> logEntries;
    private List<ModSecurity> modsecEntries;

    @FXML
    private void initialize() {
        try {
            LocalDate initialDate = LocalDate.now();
            datePicker.setValue(initialDate);
            setupDatePicker();
            logEntries = parseApacheByDate(datePicker);
            modsecEntries = parseModSecByDate(datePicker);
            setupComboBox();
            setupStartTimeComboBox();
            setupTableViews();
            displayLogsByInterval("15 Minutes", LocalDate.now());
            addClickListenerToMainVBox();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addClickListenerToMainVBox() {
        mainVBox.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            Node clickedNode = event.getPickResult().getIntersectedNode();
            if (!(clickedNode instanceof TableView) && !(clickedNode.getParent() instanceof TableView)
                    && !(clickedNode instanceof ComboBox) && !(clickedNode.getParent() instanceof ComboBox)) {
                mainVBox.requestFocus();
            }
        });
    }

    private void setupComboBox() {
        timeIntervalComboBox.setItems(FXCollections.observableArrayList(
                "15 Minutes",
                "30 Minutes",
                "1 Hour",
                "2 Hours",
                "12 Hours",
                "1 Day"
        ));
        timeIntervalComboBox
                .getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (observable, oldValue, newValue) -> {
                            try {
                                displayLogsByInterval(newValue, datePicker.getValue());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
        );
        timeIntervalComboBox.getSelectionModel().selectFirst();
    }

    private void setupDatePicker() {
        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            try {
                logEntries = parseApacheByDate(datePicker);
                modsecEntries = parseModSecByDate(datePicker);
                displayLogsByInterval(
                        timeIntervalComboBox.getSelectionModel().getSelectedItem(),
                        newValue
                );
                setupTableViews();
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
//        startTimeComboBox.setItems(times);
//        startTimeComboBox.getSelectionModel().select("00:00"); // Default selection
//        startTimeComboBox
//                .getSelectionModel()
//                .selectedItemProperty()
//                .addListener((observable, oldValue, newValue) -> {
//            try {
//                displayLogsByInterval(timeIntervalComboBox.getSelectionModel().getSelectedItem(), datePicker.getValue());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
    }

    private void setupTableViews() {
        statusCodeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[0]));
        statusCodeCountColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(Integer.parseInt(cellData.getValue()[1])).asObject());

        timestampColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[0]));
        timestampCountColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(Integer.parseInt(cellData.getValue()[1])).asObject());

        ipColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[0]));
        ipCountColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(Integer.parseInt(cellData.getValue()[1])).asObject());
        ipCountryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[2]));

        modsecRuleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[0]));
        modsecRuleCountColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(Integer.parseInt(cellData.getValue()[1])).asObject());

        statusCodeRankingTable.setRowFactory(tv -> {
            TableRow<String[]> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    String[] rowData = row.getItem();
                    handleStatusCodeDoubleClick(rowData[0]);
                }
            });
            return row;
        });

        timestampRankingTable.setRowFactory(tv -> {
            TableRow<String[]> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    String[] rowData = row.getItem();
                    handleTimeStampDoubleClick(rowData[0]);
                }
            });
            return row;
        });

        ipRankingTable.setRowFactory(tv -> {
            TableRow<String[]> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    String[] rowData = row.getItem();
                    handleIpDoubleClick(rowData[0]);
                }
            });
            return row;
        });

        ruleCountTable.setRowFactory(tv -> {
            TableRow<String[]> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    String[] rowData = row.getItem();
                    handleModSecDoubleClick(rowData[0]);
                }
            });
            return row;
        });
    }

    private void displayLogsByInterval(String interval, LocalDate selectedDate) {
        XYChart.Series<String, Number> logSeries = new XYChart.Series<>();
        logSeries.setName("Log Count");

        Map<String, Map<String, Integer>> groupedLogs = groupLogsByInterval(interval, selectedDate);
        logLineChart.getData().clear();
        List<Map.Entry<String, Map<String, Integer>>> entries = new ArrayList<>(groupedLogs.entrySet());
        entries.sort(Map.Entry.comparingByKey());
        List<Map.Entry<String, Map<String, Integer>>> displayedEntries = entries;

        for (Map.Entry<String, Map<String, Integer>> entry : displayedEntries) {
            String timeSlot = entry.getKey();
            String timeOnly;
            if (timeSlot.length() < 11) {
                timeOnly = timeSlot;
            }
            else {
                timeOnly = timeSlot.substring(11, 16);
            }
            Map<String, Integer> statusCounts = entry.getValue();
            int totalLogs = statusCounts.values().stream().mapToInt(Integer::intValue).sum();

            XYChart.Data<String, Number> data = new XYChart.Data<>(timeOnly, totalLogs);

            data.nodeProperty().addListener((observable, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                        try {
                            Stage primaryStage = (Stage) mainVBox.getScene().getWindow();
                            ViewLogController.setComboBoxElementTick("Time Stamp");
                            ViewLogController.setSearchBoxData(timeSlot);
                            ViewLogController.setIpSearch(timeSlot);
                            ViewLogController.setdbDate(datePicker);
                            WebLogManager webLogManager = new WebLogManager();
                            webLogManager.start(primaryStage, 3);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            });

            logSeries.getData().add(data);
        }

        logLineChart.getData().add(logSeries);
        logLineChart.setLegendVisible(false);

        updateStatusCodeRanking(groupedLogs);
        updateTimestampRanking(groupedLogs);
        updateIpRanking(selectedDate);
        updateModsecRuleTable(selectedDate);
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
            default:
                //todo: add something here?
        }

        dateFormat = getDateFormat(interval);

//        String selectedTime = startTimeComboBox.getSelectionModel().getSelectedItem();
        String selectedTime = "00:00";
//        if (selectedTime == null) {
//            selectedTime = "00:00";
//        }
        int startHour = Integer.parseInt(selectedTime.split(":")[0]);

        for (Apache entry : logEntries) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
            try {
                calendar.setTime(sdf.parse(entry.getTimestamp()));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            LocalDate entryDate = calendar.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (entryDate.equals(selectedDate) && calendar.get(Calendar.HOUR_OF_DAY) >= startHour) {
                adjustCalendar(calendar, field, amount);
                String timeSlot = dateFormat.format(calendar.getTime());
                String statusRange = getStatusRange(entry.getStatusCode());

                groupedLogs.computeIfAbsent(timeSlot, k -> new HashMap<>()).merge(statusRange, 1, Integer::sum);
            }
        }

        return groupedLogs;
    }

    private SimpleDateFormat getDateFormat(String interval) {
        return switch (interval) {
            case "15 Minutes", "30 Minutes", "1 Hour", "2 Hours", "12 Hours" ->
                    new SimpleDateFormat("yyyy-MM-dd HH:mm");
            case "1 Day" -> new SimpleDateFormat("yyyy-MM-dd");
            default -> throw new IllegalArgumentException("Unexpected interval: " + interval);
        };
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

        for (Map<String, Integer> statusCounts : groupedLogs.values()) {
            for (Map.Entry<String, Integer> entry : statusCounts.entrySet()) {
                statusCodeCounts.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }
        }

        ObservableList<String[]> items = statusCodeRankingTable.getItems();
        items.clear();

        List<Map.Entry<String, Integer>> sortedStatusCodes = new ArrayList<>(statusCodeCounts.entrySet());
        sortedStatusCodes.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

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

        int limit = Math.min(sortedTimestamps.size(), 7);
        for (int i = 0; i < limit; i++) {
            Map.Entry<String, Map<String, Integer>> entry = sortedTimestamps.get(i);
            int sum = entry.getValue().values().stream().mapToInt(Integer::intValue).sum();
            items.add(new String[]{entry.getKey(), String.valueOf(sum)});
        }
    }

    private void updateIpRanking(LocalDate selectedDate) {
        Map<String, Integer> ipCounts = new HashMap<>();
        Map<String, Integer> countryCounts = new HashMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);

        for (Apache entry : logEntries) {
            LocalDate logDate = LocalDate.parse(entry.getTimestamp(), formatter);
            if (logDate.equals(selectedDate)) {
                ipCounts.merge(entry.getRemoteAddress(), 1, Integer::sum);
                countryCounts.merge(checkIP(entry.getRemoteAddress()), 1, Integer::sum);
            }
        }

        ObservableList<String[]> items = ipRankingTable.getItems();
        items.clear();

        List<Map.Entry<String, Integer>> sortedIps = new ArrayList<>(ipCounts.entrySet());
        sortedIps.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        List<Map.Entry<String, Integer>> sortedCountries = new ArrayList<>(countryCounts.entrySet());
        sortedCountries.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        ObservableList<PieChart.Data> piechartActualData = FXCollections.observableArrayList();

        for (Map.Entry<String, Integer> entry : sortedIps) {
            String ipAddress = entry.getKey();
            int count = entry.getValue();
            String country = checkIP(ipAddress);
            items.add(new String[]{ipAddress, String.valueOf(count), country});
        }

        int count = 0;
        for (Map.Entry<String, Integer> entry : sortedCountries) {
            if (count >= 6) {
                break;
            }
            String country = entry.getKey();
            int countryCount = entry.getValue();
            if (!Objects.equals(country, "N/A")) {
                piechartActualData.add(new PieChart.Data(country, countryCount));
                count++;
            }
        }

        pieChartData.setData(piechartActualData);
        pieChartData.setLegendVisible(false);
    }

    private void updateModsecRuleTable(LocalDate selectedDate) {
        Map<String, Integer> ruleCounts = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss.SSSSSS Z", Locale.ENGLISH);

        for (ModSecurity entry : modsecEntries) {
            LocalDate logDate = LocalDate.parse(entry.getTimestamp(), formatter);
            if (logDate.equals(selectedDate)) {
                ruleCounts.merge(entry.getAttackName(), 1, Integer::sum);
            }
        }

        ObservableList<String[]> items = ruleCountTable.getItems();
        items.clear();

        List<Map.Entry<String, Integer>> sortedRules = new ArrayList<>(ruleCounts.entrySet());
        sortedRules.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        for (Map.Entry<String, Integer> entry : sortedRules) {
            String rule = entry.getKey();
            int count = entry.getValue();
            items.add(new String[]{rule, String.valueOf(count)});
        }
    }

    private void handleStatusCodeDoubleClick (String statusCode) {
        try {
            Stage primaryStage = (Stage) mainVBox.getScene().getWindow();
            ViewLogController.setSearchBoxData(statusCode);
            ViewLogController.setComboBoxElementTick("Status Code");
            ViewLogController.setIpSearch(statusCode);
            ViewLogController.setdbDate(datePicker);
            WebLogManager webLogManager = new WebLogManager();
            webLogManager.start(primaryStage, 3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleTimeStampDoubleClick (String timeStamp) {
        try {
            Stage primaryStage = (Stage) mainVBox.getScene().getWindow();
            ViewLogController.setSearchBoxData(timeStamp);
            ViewLogController.setComboBoxElementTick("Time Stamp");
            ViewLogController.setIpSearch(timeStamp);
            ViewLogController.setdbDate(datePicker);
            WebLogManager webLogManager = new WebLogManager();
            webLogManager.start(primaryStage, 3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleIpDoubleClick(String ipAddress) {
        try {
            Stage primaryStage = (Stage) mainVBox.getScene().getWindow();
            ViewLogController.setSearchBoxData(ipAddress);
            ViewLogController.setComboBoxElementTick("IP Address");
            ViewLogController.setIpSearch(ipAddress);
            ViewLogController.setdbDate(datePicker);
            WebLogManager webLogManager = new WebLogManager();
            webLogManager.start(primaryStage, 3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleModSecDoubleClick(String rule) {
        try {
            Stage primaryStage = (Stage) mainVBox.getScene().getWindow();
            ViewModSecController.setSearchField(rule);
            ViewModSecController.setComboBoxElementTick("Attack Name");
            ViewModSecController.setdbRule(rule);
            ViewModSecController.setdbDate(datePicker);
            WebLogManager webLogManager = new WebLogManager();
            webLogManager.start(primaryStage, 4);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
