package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import loganalyzer.Apache;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

import java.io.File;

import static loganalyzer.ApacheParser.*;

public class StreamController {
    @FXML
    private TableView<Apache> Table;
    @FXML
    private Button startStopButton;

    private Tailer tailer;
    private Thread tailerThread;
    private boolean isTailerRunning = false;

    @FXML
    private void initialize() {
        streamTable(Table);
        startStopButton.setText("Start");
    }

    @FXML
    private void handleStartStopButtonAction() {
        if (isTailerRunning) {
            stopTailer();
            startStopButton.setText("Start");
        } else {
            startTailer();
            startStopButton.setText("Stop");
        }
        isTailerRunning = !isTailerRunning;
    }

    private void streamTable(TableView<Apache> tableView) {
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

    private void startTailer() {
        ObservableList<Apache> rows = FXCollections.observableArrayList();
        File file = new File("logs/apache_nginx/access_log_100.log");
        TailerListenerAdapter listener = new TailerListenerAdapter() {
            @Override
            public void handle(String line) {
                System.out.printf("new line added: %s\n", line);
                rows.add(new Apache(
                        parseIpAddress(line),
                        parseTimestamp(line),
                        parseAllInOne(line)[5].replace("\"", ""),
                        parseAllInOne(line)[7].replace("\"", ""),
                        parseAllInOne(line)[6].replace("\"", ""),
                        Integer.parseInt(parseAllInOne(line)[8]),
                        Integer.parseInt(parseAllInOne(line)[9]),
                        parseUserAgent(line)
                ));
                Table.setItems(rows);
            }
        };
        tailer = new Tailer(file, listener, 200, true);
        tailerThread = new Thread(tailer);
        tailerThread.start();
    }

    private void stopTailer() {
        if (tailer != null) {
            tailer.stop();
            try {
                tailerThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
}
