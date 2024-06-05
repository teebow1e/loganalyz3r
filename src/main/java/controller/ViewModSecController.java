package controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import loganalyzer.Apache;
import loganalyzer.ModSecurity;
import loganalyzer.ModSecurityParser;

import static loganalyzer.ModSecurityParser.*;
import static utility.Utility.readFile;

public class ViewModSecController {

    @FXML
    private TableView<ModSecurity> Table;
    @FXML
    private TextField searchField;
    @FXML
    private DatePicker datePicker;

    private static String dbRule;
    private static DatePicker dbDate;

    public static void setdbRule(String rule) {
        dbRule = rule;
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

        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (dbRule == null) {
                    viewLog();
                }
                else {
                    viewLog(dbRule, datePicker);
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
            if (dbRule == null) {
                viewLog();
            }
            else {
                viewLog(dbRule, dbDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void viewLog() throws Exception {
        ShowLogTable(Table, searchField.getText(), datePicker.getValue());
    }

    public void viewLog(String rule, DatePicker dbDate) throws Exception{
        ShowLogTable(Table, rule, dbDate.getValue());
    }

    public static void LogTable(TableView<ModSecurity> tableView, String textField, LocalDate selectedDate) throws JsonProcessingException {
        List<ModSecurity> parsedData = parseLogs();

        tableView.getItems().clear();
        tableView.getColumns().clear();

        if (parsedData.isEmpty()) {
            return;
        }

        TableColumn<ModSecurity, String> versionColumn = new TableColumn<>("Version");
        versionColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getVersion()));

        TableColumn<ModSecurity, String> timestampColumn = new TableColumn<>("Timestamp");
        timestampColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTimestamp()));

        TableColumn<ModSecurity, String> transactionIdColumn = new TableColumn<>("Transaction ID");
        transactionIdColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTransactionId()));

        TableColumn<ModSecurity, String> ipColumn = new TableColumn<>("IP Address");
        ipColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRemoteAddress()));

        TableColumn<ModSecurity, String> pathColumn = new TableColumn<>("Request Path");
        pathColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRequestPath()));

        TableColumn<ModSecurity, String> methodColumn = new TableColumn<>("Method");
        methodColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMethod()));

        TableColumn<ModSecurity, String> protocolColumn = new TableColumn<>("Protocol");
        protocolColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getProtocol()));

        TableColumn<ModSecurity, Integer> statusCodeColumn = new TableColumn<>("Status Code");
        statusCodeColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getStatusCode()).asObject());

        TableColumn<ModSecurity, String> userAgentColumn = new TableColumn<>("User Agent");
        userAgentColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUserAgent()));

        TableColumn<ModSecurity, String> attackNameColumn = new TableColumn<>("Attack Name");
        attackNameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAttackName()));

        TableColumn<ModSecurity, String> attackMsgColumn = new TableColumn<>("Attack Message");
        attackMsgColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAttackMsg()));

        TableColumn<ModSecurity, String> attackDataColumn = new TableColumn<>("Attack Data");
        attackDataColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAttackData()));

        TableColumn<ModSecurity, String> severityColumn = new TableColumn<>("Severity");
        severityColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSeverity()));

        tableView.getColumns().addAll(versionColumn, timestampColumn, transactionIdColumn, ipColumn, pathColumn, methodColumn, protocolColumn, statusCodeColumn, userAgentColumn, attackNameColumn, attackMsgColumn, attackDataColumn, severityColumn);

        ObservableList<ModSecurity> rows = FXCollections.observableArrayList();

        for (ModSecurity rowData : parsedData) {
            String dateStr = rowData.getTimestamp();
            LocalDate rowDate = parseDate(dateStr);

            if (rowDate.equals(selectedDate) && containsTextField(rowData, textField)) {
                rows.add(rowData);
            }
        }

        tableView.setItems(rows);
        tableView.setEditable(true);

        tableView.setRowFactory(tv -> {
            TableRow<ModSecurity> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    ModSecurity rowData = row.getItem();
                    showRowContent(rowData);
                    System.out.println("Double clicked row: " + rowData);
                }
            });
            return row;
        });
    }

    public static void ShowLogTable(TableView<ModSecurity> tableView, String textField, LocalDate selectedDate) throws JsonProcessingException {
        LogTable(tableView, textField, selectedDate);
    }

    private static void showRowContent(ModSecurity rowData) {
        VBox contentBox = new VBox();
        contentBox.setSpacing(5);

        Text versionText = new Text("Version: " + rowData.getVersion());
        Text timestampText = new Text("Timestamp: " + rowData.getTimestamp());
        Text transactionIdText = new Text("Transaction ID: " + rowData.getTransactionId());
        Text ipText = new Text("IP Address: " + rowData.getRemoteAddress());
        Text pathText = new Text("Request Path: " + rowData.getRequestPath());
        Text methodText = new Text("Method: " + rowData.getMethod());
        Text protocolText = new Text("Protocol: " + rowData.getProtocol());
        Text statusCodeText = new Text("Status Code: " + rowData.getStatusCode());
        Text userAgentText = new Text("User Agent: " + rowData.getUserAgent());
        Text attackNameText = new Text("Attack Name: " + rowData.getAttackName());
        Text attackMsgText = new Text("Attack Message: " + rowData.getAttackMsg());
        Text attackDataText = new Text("Attack Data: " + rowData.getAttackData());
        Text severityText = new Text("Severity: " + rowData.getSeverity());

        contentBox.getChildren().addAll(versionText, timestampText, transactionIdText, ipText, pathText, methodText, protocolText, statusCodeText, userAgentText, attackNameText, attackMsgText, attackDataText, severityText);

        Dialog<Void> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(contentBox);
        dialog.setTitle("Row Details");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    public static LocalDate parseDate(String inputDate) {
        DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss.SSSSSS Z");
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(inputDate, INPUT_FORMATTER);
        return zonedDateTime.toLocalDate();
    }

    private static List<ModSecurity> parseLogs() throws JsonProcessingException {
        Logger logger = Logger.getLogger(ModSecurityParser.class.getName());
        String logFilePath = System.getProperty("user.dir") + "/logs/modsecurity/modsec_audit_new.log";
        List<ModSecurity> parsedData = new ArrayList<>();
        LinkedList<String> logLines = readFile(logFilePath, logger);
        ObjectMapper objectMapper = new ObjectMapper();
        for (String line : logLines) {
            JsonNode jsonNode = objectMapper.readTree(line);
            String auditData = jsonNode.path("audit_data").path("messages").get(0).asText();
            String requestLine = jsonNode.path("request").path("request_line").asText();
            String[] requestParts = requestLine.split(" ");
            JsonNode versionSection = jsonNode.path("audit_data").path("producer");
            String potentialVersion;
            if (versionSection.isMissingNode() || versionSection.isNull()) {
                potentialVersion = "";
            } else {
                potentialVersion = parseVersion(versionSection.get(0).asText());
            }


            ModSecurity parsedLine = new ModSecurity(
                    potentialVersion,
                    jsonNode.path("transaction").path("time").asText(),
                    jsonNode.path("transaction").path("transaction_id").asText(),
                    jsonNode.path("transaction").path("remote_address").asText(),
                    requestParts[1],
                    requestParts[0],
                    requestParts[2],
                    jsonNode.path("response").path("status").asInt(),
                    jsonNode.path("request").path("headers").path("User-Agent").asText(),
                    parseAttackType(auditData),
                    parseAttackMsg(auditData),
                    parseAttackData(auditData),
                    parseSeverity(auditData)
            );
            parsedData.add(parsedLine);
        }
        return parsedData;
    }

    public static boolean containsTextField(ModSecurity modsec, String textField) {
        String version = modsec.getVersion();
        String transactionId = modsec.getTransactionId();
        String attackName = modsec.getAttackName();
        String attackMsg = modsec.getAttackMsg();
        String attackData = modsec.getAttackData();
        String severity = modsec.getSeverity();
        return version.contains(textField) ||
                transactionId.contains(textField) ||
                attackName.contains(textField) ||
                attackMsg.contains(textField) ||
                attackData.contains(textField) ||
                severity.contains(textField);
    }

}
