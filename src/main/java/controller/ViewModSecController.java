package controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.SimpleStringProperty;
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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import loganalyzer.Apache;
import loganalyzer.ModSecurity;
import loganalyzer.ModSecurityParser;

import static loganalyzer.ApacheParser.parseApacheByDate;
import static loganalyzer.ModSecurityParser.*;
import static utility.Utility.readFile;

public class ViewModSecController {

    @FXML
    private TableView<ModSecurity> Table;
    @FXML
    private TableColumn<ModSecurity, String> timestampColumn;
    @FXML
    private TableColumn<ModSecurity, String> ipColumn;
    @FXML
    private TableColumn<ModSecurity, String> pathColumn;
    @FXML
    private TableColumn<ModSecurity, String> methodColumn;
    @FXML
    private TableColumn<ModSecurity, String> userAgentColumn;
    @FXML
    private TableColumn<ModSecurity, String> attackNameColumn;
    @FXML
    private TableColumn<ModSecurity, String> attackDataColumn;
    @FXML
    private TableColumn<ModSecurity, String> severityColumn;
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
        ShowLogTable(Table, searchField.getText(), datePicker);
    }

    public void viewLog(String rule, DatePicker datePicker) throws Exception{
        ShowLogTable(Table, rule, datePicker);
    }

    public void LogTable(TableView<ModSecurity> tableView, String textField, DatePicker datePicker) {
        tableView.getItems().clear();
        tableView.getColumns().clear();

        timestampColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTimestamp()));
        ipColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRemoteAddress()));
        pathColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRequestPath()));
        methodColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMethod()));
        userAgentColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUserAgent()));
        attackNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAttackName()));
        attackDataColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAttackData()));
        severityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSeverity()));

        tableView.getColumns().addAll(timestampColumn, ipColumn, pathColumn, methodColumn, userAgentColumn, attackNameColumn, attackDataColumn, severityColumn);

        ObservableList<ModSecurity> rows = FXCollections.observableArrayList();

        List<ModSecurity> modSecEntries = parseModSecByDate(datePicker);

        for (ModSecurity rowData : modSecEntries) {
            String dateStr = rowData.getTimestamp();
            LocalDate rowDate = parseDate(dateStr);

            if (containsTextField(rowData, textField)) {
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

    public void ShowLogTable(TableView<ModSecurity> tableView, String textField, DatePicker datePicker)
            throws JsonProcessingException {
        LogTable(tableView, textField, datePicker);
    }

    private static void showRowContent(ModSecurity rowData) {
        VBox contentBox = new VBox();
        contentBox.setSpacing(5);
        Text versionText = new Text("ModSecurity Version: " + rowData.getVersion());
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

        contentBox.getChildren().addAll(versionText, timestampText, transactionIdText,
                ipText, pathText, methodText, protocolText, statusCodeText, userAgentText,
                attackNameText, attackMsgText, attackDataText, severityText);

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