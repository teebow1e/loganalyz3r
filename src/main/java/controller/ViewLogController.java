package controller;

import javafx.beans.property.SimpleIntegerProperty;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import ui.ComboBoxItemWrap;
import loganalyzer.Apache;
import static loganalyzer.ApacheParser.parseApacheByDate;

public class ViewLogController {
    @FXML
    private TableView<Apache> Table;
    @FXML
    private TableColumn<Apache, String> ipColumn;
    @FXML
    private TableColumn<Apache, String> timestampColumn;
    @FXML
    private TableColumn<Apache, String> methodColumn;
    @FXML
    private TableColumn<Apache, String> protocolColumn;
    @FXML
    private TableColumn<Apache, String> requestPathColumn;
    @FXML
    private TableColumn<Apache, Integer> statusCodeColumn;
    @FXML
    private TableColumn<Apache, Integer> contentLengthColumn;
    @FXML
    private TableColumn<Apache, String> userAgentColumn;
    @FXML
    public TextField searchField;
    @FXML
    public DatePicker datePicker;
    @FXML
    private ComboBox<ComboBoxItemWrap<String>> filterComboBox;

    private static String dbSearch;
    private static DatePicker dbDate;
    private static String comboBoxElementToBeTicked;
    private static String searchBoxData;
    private final Logger logger = Logger.getLogger(ViewLogController.class.getName());

    private List<String> appliedFilter = new LinkedList<>();

    private final ObservableList<ComboBoxItemWrap<String>> filterList = FXCollections.observableArrayList(
            new ComboBoxItemWrap<>("IP Address"),
            new ComboBoxItemWrap<>("Time Stamp"),
            new ComboBoxItemWrap<>("Method"),
            new ComboBoxItemWrap<>("Protocol"),
            new ComboBoxItemWrap<>("Request Path"),
            new ComboBoxItemWrap<>("Status Code"),
            new ComboBoxItemWrap<>("User-Agent")
    );

    public static void setIpSearch(String search) {
        dbSearch = search;
    }

    public static void setdbDate(DatePicker date) {
        dbDate = date;
    }

    public static void setComboBoxElementTick(String data) {
        comboBoxElementToBeTicked = data;
    }
    public static void setSearchBoxData(String data) {
        searchBoxData = data;
    }

    public AtomicInteger getNumberOfSelectedFilter() {
        AtomicInteger counter = new AtomicInteger();
        filterComboBox.getItems().
                filtered(ComboBoxItemWrap::getCheck)
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
                        .forEach(p -> sb.append("; ").append(p.getItem())
                );
                final String string = sb.toString();
                filterComboBox.setPromptText(string.substring(Integer.min(2, string.length())));
            });

            return cell;
        });

        if (searchBoxData != null) {
            searchField.setText(searchBoxData);
        }

        if (comboBoxElementToBeTicked != null) {
            switch (comboBoxElementToBeTicked) {
                case "Time Stamp":
                case "Status Code":
                case "IP Address":
                    filterComboBox.setPromptText(comboBoxElementToBeTicked);
                    filterList.stream()
                            .filter(item -> item.getItem().equals(comboBoxElementToBeTicked))
                            .findFirst()
                            .ifPresent(item -> item.setCheck(true));
                    break;
                default:
                    filterComboBox.setPromptText("Choose Filter");
                    break;
            }
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

        datePicker.valueProperty().addListener((observable, oldValue, newValue) ->
            viewLog(searchField.getText())
        );

        searchField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    if (getNumberOfSelectedFilter().get() > 0) {
                        appliedFilter.clear();
                        filterComboBox.getItems()
                                .filtered(ComboBoxItemWrap::getCheck)
                                .forEach(item -> appliedFilter.add(item.getItem())
                        );
                        viewLog(appliedFilter);
                    } else {
                        viewLog();
                    }
                } catch (Exception e) {
                    // which has exception here ? @fieryphoenix
                    logger.log(Level.INFO, "An exception occurred", e);
                }
            }
        });

        try {
            if (dbSearch == null) {
                viewLog();
            }
            else {
                viewLog(dbSearch);
            }
        } catch (Exception e) {
            logger.log(Level.INFO, "An exception occurred", e);
        }
    }

    public void viewLog() {
        LogTable(Table, searchField.getText(), new ArrayList<>());
    }

    public void viewLog(String search) {
        filterComboBox.getItems()
                .filtered(ComboBoxItemWrap::getCheck)
                .forEach(item -> appliedFilter.add(item.getItem())
        );
        LogTable(Table, search, appliedFilter);
    }

    public void viewLog(List<String> appliedFilter) {
        LogTable(Table, searchField.getText(), appliedFilter);
    }

    public void LogTable(TableView<Apache> tableView, String textField, List<String> appliedFilter) {
        tableView.getItems().clear();
        tableView.getColumns().clear();

        ipColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRemoteAddress()));
        timestampColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTimestamp()));
        methodColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMethod()));
        protocolColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProtocol()));
        requestPathColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRequestPath()));
        statusCodeColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getStatusCode()).asObject());
        contentLengthColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getContentLength()).asObject());
        userAgentColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUserAgent()));

        tableView.getColumns().addAll(ipColumn, timestampColumn, methodColumn, protocolColumn, requestPathColumn, statusCodeColumn, contentLengthColumn, userAgentColumn);

        ObservableList<Apache> rows = FXCollections.observableArrayList();

        List<Apache> logEntries = parseApacheByDate(datePicker);

        for (Apache row: logEntries) {
            if (containsTextField(row, textField, appliedFilter)) {
                rows.add(row);
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
                }
            });
            return row;
        });
    }

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

    public static boolean containsTextField(Apache apache, String textField, List<String> fields) {
        boolean found = false;
        String ip = apache.getRemoteAddress();
        String timestamp = apache.getTimestamp();
        String method = apache.getMethod();
        String protocol = apache.getProtocol();
        String requestPath = apache.getRequestPath();
        int statusCode = apache.getStatusCode();
        String userAgent = apache.getUserAgent();

        if (fields.isEmpty()) {
            return ip.contains(textField) ||
                    timestamp.contains(textField) ||
                    method.contains(textField) ||
                    protocol.contains(textField) ||
                    requestPath.contains(textField) ||
                    userAgent.contains(textField);
        }
        for (String field : fields) {
            found = switch (field) {
                case "IP Address" -> textField != null && ip.contains(textField);
                case "Time Stamp" -> {
                    DateTimeFormatter APACHE_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z");
                    DateTimeFormatter TEXTFIELD_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    if (textField != null) {
                        try {
                            LocalDateTime apacheDateTime = LocalDateTime.parse(timestamp, APACHE_TIMESTAMP_FORMATTER);
                            LocalDateTime textFieldDateTime = LocalDateTime.parse(textField, TEXTFIELD_TIMESTAMP_FORMATTER);
                            yield apacheDateTime.isAfter(textFieldDateTime);
                        } catch (Exception e) {
                            yield false;
                        }
                    } else {
                        yield false;
                    }
                }
                case "Method" -> textField != null && method.contains(textField);
                case "Protocol" -> textField != null && protocol.contains(textField);
                case "Request Path" -> textField != null && requestPath.contains(textField);
                case "Status Code" -> {
                    if (textField != null) {
                        if (textField.contains("-")) {
                            String[] range = textField.split("-");
                            if (range.length == 2) {
                                try {
                                    int lowerBound = Integer.parseInt(range[0]);
                                    int upperBound = Integer.parseInt(range[1]);
                                    yield statusCode >= lowerBound && statusCode <= upperBound;
                                } catch (NumberFormatException e) {
                                    yield false;
                                }
                            } else {
                                yield false;
                            }
                        } else {
                            yield Integer.toString(statusCode).contains(textField);
                        }
                    } else {
                        yield false;
                    }
                }
                case "User-Agent" -> textField != null && userAgent.contains(textField);
                default -> found;
            };
            if (!found) break;
        }
        return found;
    }
}