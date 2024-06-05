package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;

import loganalyzer.Apache;
import loganalyzer.ApacheParser;

import static utility.Utility.readFile;

public class ViewLogController {

    @FXML
    private TableView<Apache> Table;
    @FXML
    public TextField searchField;
    @FXML
    public DatePicker datePicker;

    private static String dbSearch;
    private static DatePicker dbDate;

    public static void setIpSearch(String address) {
        dbSearch = address;
    }

    public static void setdbDate(DatePicker date) {
        dbDate = date;
    }

    @FXML
    private void initialize() {
        if(datePicker.getValue() == null) {
            if (dbDate == null) {
                datePicker.setValue(LocalDate.now());
            }
            else {
                datePicker.setValue(dbDate.getValue());
            }
        }

        if (searchField.getText() == null) {
            if (dbSearch != null) {
                searchField.setText(dbSearch);
            }
        }

        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (dbSearch == null) {
                    viewLog();
                }
                else {
                    viewLog(dbSearch, datePicker);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        searchField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    viewLog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            if (dbSearch == null) {
                viewLog();
            }
            else {
                viewLog(dbSearch, dbDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void viewLog() {
        ShowLogTable(Table, searchField.getText(), datePicker.getValue());
    }

    public void viewLog(String ipAddress, DatePicker dbDate) {
        ShowLogTable(Table, ipAddress, dbDate.getValue());
    }

    public static void LogTable(TableView<Apache> tableView, String textField, LocalDate selectedDate) {
        List<Apache> parsedData = parseLogs();

        tableView.getItems().clear();
        tableView.getColumns().clear();

        if (parsedData.isEmpty()) {
            return;
        }

        TableColumn<Apache, String> ipColumn = new TableColumn<>("IP Address");
        ipColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRemoteAddress()));

        TableColumn<Apache, String> timestampColumn = new TableColumn<>("Timestamp");
        timestampColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTimestamp()));

        TableColumn<Apache, String> methodColumn = new TableColumn<>("Method");
        methodColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMethod()));

        TableColumn<Apache, String> protocolColumn = new TableColumn<>("Protocol");
        protocolColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getProtocol()));

        TableColumn<Apache, String> requestPathColumn = new TableColumn<>("Request Path");
        requestPathColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRequestPath()));

        TableColumn<Apache, Integer> statusCodeColumn = new TableColumn<>("Status Code");
        statusCodeColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getStatusCode()).asObject());

        TableColumn<Apache, Integer> contentLengthColumn = new TableColumn<>("Content Length");
        contentLengthColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getContentLength()).asObject());

        TableColumn<Apache, String> userAgentColumn = new TableColumn<>("User Agent");
        userAgentColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUserAgent()));

        tableView.getColumns().addAll(ipColumn, timestampColumn, methodColumn, protocolColumn, requestPathColumn, statusCodeColumn, contentLengthColumn, userAgentColumn);

        ObservableList<Apache> rows = FXCollections.observableArrayList();

        for (Apache rowData : parsedData) {
            String dateStr = rowData.getTimestamp();
            LocalDate rowDate = parseDate(dateStr);

            if (rowDate.equals(selectedDate) && containsTextField(rowData, textField)) {
                rows.add(rowData);
            }
        }

        tableView.setItems(rows);
        tableView.setEditable(true);

        tableView.setRowFactory(tv -> {
            TableRow<Apache> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    Apache rowData = row.getItem();
                    showRowContent(rowData);
                    System.out.println("Double clicked row: " + rowData);
                }
            });
            return row;
        });
    }

    public static void ShowLogTable(TableView<Apache> tableView, String textField, LocalDate selectedDate) {
        LogTable(tableView, textField, selectedDate);
//        updateTableView(tableView, textField, selectedDate);
    }

//    private static void updateTableView(TableView<Apache> tableView, TextField textField, LocalDate selectedDate) {
//        try {
//            LogTable(tableView, textField, selectedDate);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    private static void showRowContent(Apache rowData) {
        VBox contentBox = new VBox();
        contentBox.setSpacing(5);

        Text ipText = new Text("IP Address: " + rowData.getRemoteAddress());
        Text timestampText = new Text("Timestamp: " + rowData.getTimestamp());
        Text methodText = new Text("Method: " + rowData.getMethod());
        Text protocolText = new Text("Protocol: " + rowData.getProtocol());
        Text requestPathText = new Text("Request Path: " + rowData.getRequestPath());
        Text statusCodeText = new Text("Status Code: " + rowData.getStatusCode());
        Text contentLengthText = new Text("Content Length: " + rowData.getContentLength());
        Text userAgentText = new Text("User Agent: " + rowData.getUserAgent());

        contentBox.getChildren().addAll(ipText, timestampText, methodText, protocolText, requestPathText, statusCodeText, contentLengthText, userAgentText);

        Dialog<Void> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(contentBox);
        dialog.setTitle("Row Details");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    public static LocalDate parseDate(String inputDate) {
        DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z");
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(inputDate, INPUT_FORMATTER);
        return zonedDateTime.toLocalDate();
    }

    private static List<Apache> parseLogs() {
        Logger logger = Logger.getLogger(ApacheParser.class.getName());
        String logFilePath = System.getProperty("user.dir") + "\\logs\\apache_nginx\\access_log_50000.log";
        List<Apache> parsedData = new ArrayList<>();
        LinkedList<String> logLines = readFile(logFilePath, logger);
        for (String logLine : logLines) {
            Apache parsedLine = new Apache(
                    ApacheParser.parseIpAddress(logLine),
                    ApacheParser.parseTimestamp(logLine),
                    ApacheParser.parseAllInOne(logLine)[5].replace("\"", ""),
                    ApacheParser.parseAllInOne(logLine)[7].replace("\"", ""),
                    ApacheParser.parseAllInOne(logLine)[6].replace("\"", ""),
                    Integer.parseInt(ApacheParser.parseAllInOne(logLine)[8]),
                    Integer.parseInt(ApacheParser.parseAllInOne(logLine)[9]),
                    ApacheParser.parseUserAgent(logLine)
            );
            parsedData.add(parsedLine);
        }
        return parsedData;
    }

    public static boolean containsTextField(Apache apache, String textField) {
        String ip = apache.getRemoteAddress();
        String timestamp = apache.getTimestamp();
        String method = apache.getMethod();
        String protocol = apache.getProtocol();
        String requestPath = apache.getRequestPath();
        String userAgent = apache.getUserAgent();

        return ip.contains(textField) ||
                timestamp.contains(textField) ||
                method.contains(textField) ||
                protocol.contains(textField) ||
                requestPath.contains(textField) ||
                userAgent.contains(textField);
    }

}
