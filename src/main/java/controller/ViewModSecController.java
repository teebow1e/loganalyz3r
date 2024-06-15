package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import loganalyzer.ModSecurity;
import ui.ComboBoxItemWrap;

import static loganalyzer.ModSecurityParser.*;

public class ViewModSecController {

    @FXML
    private TableView<ModSecurity> modSecurityLogTable;
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
    @FXML
    private ComboBox<ComboBoxItemWrap<String>> filterComboBox;
    private static String dbRule;
    private static DatePicker dbDate;
    private static String comboBoxElementToBeTicked;
    private static String searchBoxData;
    private List<String> appliedFilter = new LinkedList<>();

    private ObservableList<ComboBoxItemWrap<String>> filterList = FXCollections.observableArrayList(
            new ComboBoxItemWrap<>("Remote Address"),
            new ComboBoxItemWrap<>("Path"),
            new ComboBoxItemWrap<>("Method"),
            new ComboBoxItemWrap<>("User Agent"),
            new ComboBoxItemWrap<>("Attack Name"),
            new ComboBoxItemWrap<>("Attack Data"),
            new ComboBoxItemWrap<>("Severity")
    );

    public static void setdbRule(String rule) {
        dbRule = rule;
    }

    public static void setdbDate(DatePicker date) {
        dbDate = date;
    }

    public static void setSearchField(String data) {
        searchBoxData = data;
    }

    public AtomicInteger getNumberOfSelectedFilter() {
        AtomicInteger counter = new AtomicInteger();
        filterComboBox.getItems().
                filtered(f -> f.getCheck())
                .forEach(item -> counter.getAndIncrement());
        return counter;
    }

    @FXML
    private void initialize() {
        filterComboBox.setCellFactory( c -> {
            ListCell<ComboBoxItemWrap<String>> cell = new ListCell<>(){
                @Override
                protected void updateItem(ComboBoxItemWrap<String> item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        final CheckBox filterComboBox = new CheckBox(item.toString());
                        filterComboBox.selectedProperty().bind(item.checkProperty());
                        setGraphic(filterComboBox);
                    }
                }
            };

            cell.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
                cell.getItem().checkProperty().set(!cell.getItem().checkProperty().get());
                StringBuilder sb = new StringBuilder();
                filterComboBox.getItems()
                        .filtered(Objects::nonNull)
                        .filtered(ComboBoxItemWrap::getCheck)
                        .forEach(p -> sb.append("; ").append(p.getItem()));
                final String string = sb.toString();
                filterComboBox.setPromptText(string.substring(Integer.min(2, string.length())));
            });
            return cell;
        });

        if (searchBoxData != null) {
            searchField.setText(searchBoxData);
        }

        if (comboBoxElementToBeTicked != null &&
                comboBoxElementToBeTicked.equals("Attack Name")) {
            filterComboBox.setPromptText(comboBoxElementToBeTicked);
            filterList.stream()
                    .filter(item -> item.getItem().equals(comboBoxElementToBeTicked))
                    .findFirst()
                    .ifPresent(item -> item.setCheck(true));
        } else {
            filterComboBox.setPromptText("Choose Filter");
        }

        filterComboBox.setItems(filterList);
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
                    if (getNumberOfSelectedFilter().get() > 0) {
                        appliedFilter.clear();
                        filterComboBox.getItems()
                                .filtered(ComboBoxItemWrap::getCheck)
                                .forEach(item -> appliedFilter.add(item.getItem()));
                        viewLog(appliedFilter);
                    } else {
                        viewLog();
                    }
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

    public static void setComboBoxElementTick(String data) {
        comboBoxElementToBeTicked = data;
    }

    private void viewLog() {
        showLogTable(modSecurityLogTable, searchField.getText(), datePicker, new ArrayList<>());
    }
    private void viewLog(List<String> appliedFilter) {
        showLogTable(modSecurityLogTable, searchField.getText(), datePicker, appliedFilter);
    }

    public void viewLog(String rule, DatePicker datePicker) {
        showLogTable(modSecurityLogTable, rule, datePicker, new ArrayList<>());
    }

    public void initializeLogTable(TableView<ModSecurity> tableView,
                                   String textField, DatePicker datePicker,
                                   List<String> appliedFilter) {
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
            if (containsTextField(rowData, textField, appliedFilter)) {
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
                }
            });
            return row;
        });
    }

    public void showLogTable(TableView<ModSecurity> tableView,
                             String textField, DatePicker datePicker,
                             List<String> appliedFilter) {
        initializeLogTable(tableView, textField, datePicker, appliedFilter);
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


    public static boolean containsTextField(ModSecurity modsec, String textField, List<String> appliedFilter) {
        boolean found = false;
        String remoteAddr = modsec.getRemoteAddress();
        String requestPath = modsec.getRequestPath();
        String method = modsec.getMethod();
        String userAgent = modsec.getUserAgent();
        String attackName = modsec.getAttackName();
        String attackData = modsec.getAttackData();
        String severity = modsec.getSeverity();
        if (appliedFilter.isEmpty()) {
            return remoteAddr.contains(textField) ||
                    requestPath.contains(textField) ||
                    method.contains(textField) ||
                    userAgent.contains(textField) ||
                    attackName.contains(textField) ||
                    attackData.contains(textField) ||
                    severity.contains(textField);
        }
        for (String filter : appliedFilter) {
            found = switch (filter) {
                case "Remote Address" -> textField != null && remoteAddr.contains(textField);
                case "Path" -> textField != null && requestPath.contains(textField);
                case "Method" -> textField != null && method.contains(textField);
                case "User Agent" -> textField != null && userAgent.contains(textField);
                case "Attack Name" -> textField != null && attackName.contains(textField);
                case "Attack Data" -> textField != null && attackData.contains(textField);
                case "Severity" -> textField != null && severity.contains(textField);
                default -> found;
            };
            if (!found) break;
        }
        return found;
    }
}