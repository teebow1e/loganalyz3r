package controller;

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
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
    private Button searchBtn;
    @FXML
    private ComboBox<ComboBoxItemWrap<String>> filterComboBox;

    private static String dbSearch;
    private static DatePicker dbDate;

    private List<String> appliedFilter = new LinkedList<>();

    private ObservableList<ComboBoxItemWrap<String>> filterList = FXCollections.observableArrayList(
            new ComboBoxItemWrap<>("IP Address"),
            new ComboBoxItemWrap<>("Method"),
            new ComboBoxItemWrap<>("Protocol"),
            new ComboBoxItemWrap<>("Request Path"),
            new ComboBoxItemWrap<>("Status Code"),
            new ComboBoxItemWrap<>("User-Agent")
    );

    public static void setIpSearch(String address) {
        dbSearch = address;
    }

    public static void setdbDate(DatePicker date) {
        dbDate = date;
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
                filterComboBox.getItems().filtered( f-> f!=null).filtered( f-> f.getCheck()).forEach( p -> {
                    sb.append("; "+p.getItem());
                });
                final String string = sb.toString();
                filterComboBox.setPromptText(string.substring(Integer.min(2, string.length())));
            });

            return cell;
        });

        filterComboBox.setItems(filterList);

        searchBtn.setOnAction(event -> {
            System.out.println("this button does nothing hehe");
        });

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
                if (dbSearch == null) {
                    viewLog();
                }
                else {
                    viewLog(dbSearch);
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
                        filterComboBox.getItems().filtered(
                                f -> f.getCheck()).forEach(item -> appliedFilter.add(item.getItem())
                        );
                        System.out.println("called view log with filter");
                        viewLog(appliedFilter);
                    } else {
                        System.out.println("called view log with no filter");
                        viewLog();
                    }
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
                viewLog(dbSearch);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void viewLog() {
        LogTable(Table, searchField.getText(), new ArrayList<>());
    }

    public void viewLog(String ipAddress) {
        LogTable(Table, ipAddress, new ArrayList<>());
    }

    public void viewLog(List<String> appliedFilter) {
        LogTable(Table, searchField.getText(), appliedFilter);
    }

    public void LogTable(TableView<Apache> tableView, String textField, List<String> appliedFilter) {
        tableView.getItems().clear();
        tableView.getColumns().clear();

        ipColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRemoteAddress()));
        timestampColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTimestamp()));
        methodColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMethod()));
        protocolColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getProtocol()));
        requestPathColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRequestPath()));
        statusCodeColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getStatusCode()).asObject());
        contentLengthColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getContentLength()).asObject());
        userAgentColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUserAgent()));

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
                    System.out.println("Double clicked row: " + rowData);
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
            // we should add more field here, so that user don't have to uncheck everything
            // just to search for one value
            found = switch (field) {
                case "IP Address" -> textField != null && ip.contains(textField);
                case "Method" -> textField != null && method.contains(textField);
                case "Protocol" -> textField != null && protocol.contains(textField);
                case "Request Path" -> textField != null && requestPath.contains(textField);
                case "Status Code" -> textField != null && Integer.toString(apache.getStatusCode()).contains(textField);
                case "User-Agent" -> textField != null && userAgent.contains(textField);
                default -> found;
            };
            if (!found) break;
        }
        return found;
    }

}