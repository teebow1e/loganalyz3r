package controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import loganalyzer.Apache;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static loganalyzer.ApacheParser.*;
import static utility.Utility.*;
import static utility.LogFileVerifier.*;

public class StreamController {
    // this class right now only support Apache log
    // todo: add a dropdown, can we modify the table according to the log we choose?
    // @fieryphoenix
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
    private Button startStopButton;
    @FXML
    private Text chosenFileText;
    private Tailer tailer;
    private Thread tailerThread;
    private boolean isTailerRunning = false;
    private String fileToStreamPath = "";

    FileChooser.ExtensionFilter extFilterLogs = new FileChooser.ExtensionFilter(
            "Log files (*.log, *.txt, *.json)",
            "*.log", "*.txt", "*.json"
    );


    @FXML
    private void initialize() {
        streamTable(Table);
        startStopButton.setText("Start Streaming");
    }

    @FXML
    void chooseFile(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(extFilterLogs);
        fc.setTitle("Choose a file for streaming");
        File file = fc.showOpenDialog(null);
        if (file != null) {
            chosenFileText.setText("You selected: " + file.getAbsolutePath());
            fileToStreamPath = file.getAbsolutePath();
            System.out.println("[DEBUG] File name: " + file.getName());
            System.out.println("[DEBUG] File size: " + file.length() + " bytes");
        }
    }

    @FXML
    private void handleStartStopButtonAction() {
        if (isTailerRunning) {
            stopTailer();
            startStopButton.setText("Start Streaming");
        } else {
            startTailer();
            startStopButton.setText("Stop Streaming");
        }
        isTailerRunning = !isTailerRunning;
    }

    private void streamTable(TableView<Apache> tableView) {
        tableView.getItems().clear();

        ipColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRemoteAddress()));
        timestampColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTimestamp()));
        methodColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMethod()));
        protocolColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProtocol()));
        requestPathColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRequestPath()));
        statusCodeColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getStatusCode()).asObject());
        contentLengthColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getContentLength()).asObject());
        userAgentColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUserAgent()));

        tableView.setRowFactory(tv -> {
            TableRow<Apache> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    Apache rowData = row.getItem();
                    showRowContent(rowData);
                    System.out.println("[DEBUG] Double clicked row: " + rowData);
                }
            });
            return row;
        });
    }

    private void startTailer() {
        ObservableList<Apache> rows = FXCollections.observableArrayList();
        if (Objects.equals(fileToStreamPath, "")) {
            showAlert("ERROR", "You have not selected any files!");
            return;
        }
        File file = new File(fileToStreamPath);

        if (!file.canRead()) {
            showAlert("ERROR",
                    "This file is unreadable or you does not have the required permission to read. The program can not continue.");
            return;
        }

        TailerListenerAdapter listener = new TailerListenerAdapter() {
            @Override
            public void handle(String line) {
                System.out.printf("[DEBUG] new line added: %s\n", line);
                rows.add(parseLogLine(line));
                System.out.println(rows.size());
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
