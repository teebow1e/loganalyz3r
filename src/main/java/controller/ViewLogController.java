package controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
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

import loganalyzer.Apache;

import static loganalyzer.ApacheParser.parseApacheByDate;

public class ViewLogController {

    @FXML
    private TableView<Apache> Table;
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

    private ObservableList<ComboBoxItemWrap<String>> filterList = FXCollections.observableArrayList(
            new ComboBoxItemWrap<>("hihi"),
            new ComboBoxItemWrap<>("quan ngooo"),
            new ComboBoxItemWrap<>("lmao")
    );

    public static void setIpSearch(String address) {
        dbSearch = address;
    }

    public static void setdbDate(DatePicker date) {
        dbDate = date;
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
            filterComboBox.getItems().filtered(
                    f -> f.getCheck()).forEach(item -> System.out.printf("%s ", item.getItem())
            );
            System.out.println();
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

    public void LogTable(TableView<Apache> tableView, String textField, LocalDate selectedDate) {
        tableView.getItems().clear();
        tableView.getColumns().clear();

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

        List<Apache> logEntries = parseApacheByDate(datePicker);

        for (Apache row: logEntries) {
            if (containsTextField(row, textField)) {
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

    public void ShowLogTable(TableView<Apache> tableView, String textField, LocalDate selectedDate) {
        LogTable(tableView, textField, selectedDate);
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
    public class ComboBoxItemWrap<T> {

        private BooleanProperty check = new SimpleBooleanProperty(false);
        private ObjectProperty<T> item = new SimpleObjectProperty<>();

        ComboBoxItemWrap() {
        }

        ComboBoxItemWrap(T item) {
            this.item.set(item);
        }

        ComboBoxItemWrap(T item, Boolean check) {
            this.item.set(item);
            this.check.set(check);
        }

        public BooleanProperty checkProperty() {
            return check;
        }

        public Boolean getCheck() {
            return check.getValue();
        }

        public void setCheck(Boolean value) {
            check.set(value);
        }

        public ObjectProperty<T> itemProperty() {
            return item;
        }

        public T getItem() {
            return item.getValue();
        }

        public void setItem(T value) {
            item.setValue(value);
        }

        @Override
        public String toString() {
            return item.getValue().toString();
        }
    }

}
