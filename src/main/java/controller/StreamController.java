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
import utility.LogFileVerifier;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static loganalyzer.ApacheParser.*;
import static utility.Utility.*;

public class StreamController {
    @FXML
    private TableView<Apache> mainStreamTable;
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
    private final Logger logger = Logger.getLogger(StreamController.class.getName());

    FileChooser.ExtensionFilter extFilterLogs = new FileChooser.ExtensionFilter(
            "Log files (*.log, *.txt, *.json)",
            "*.log", "*.txt", "*.json"
    );
    @FXML
    private void initialize() {
        streamTable(mainStreamTable);
        startStopButton.setText("Start Streaming");
    }

    @FXML
    void chooseFile(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(extFilterLogs);
        fc.setTitle("Choose a file for streaming");
        File file = fc.showOpenDialog(null);
        if (file != null) {
            fileToStreamPath = file.getAbsolutePath();
            // this only support apache for now.
            chosenFileText.setText("You selected: " + file.getAbsolutePath());
            logger.log(Level.INFO,"File name: {0}", file.getName());
            logger.log(Level.INFO,"File size: {0} bytes", file.length());
        }
    }

    @FXML
    private void handleStartStopButtonAction() {
        if (isTailerRunning) {
            logger.log(Level.INFO, "Tailer ordered to stop.");
            stopTailer();
            startStopButton.setText("Start Streaming");
        } else {
            logger.log(Level.INFO, "Tailer ordered to start.");
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
            logger.log(Level.INFO, "User selected an unreadable file for streaming.");
            showAlert("ERROR",
                    "This file is unreadable or you does not have the required permission to read. The program can not continue.");
            return;
        }

        TailerListenerAdapter listener = new TailerListenerAdapter() {
            @Override
            public void handle(String line) {
                logger.log(Level.FINE, "[{0}] new line added: {1}", new Object[]{fileToStreamPath, line});
                rows.add(parseLogLine(line));
                mainStreamTable.setItems(rows);
            }
        };
        tailer = new Tailer(file, listener, 200, true);
        tailerThread = new Thread(tailer);
        logger.log(Level.INFO, "Tailer ready to start.");
        tailerThread.start();
    }

    private void stopTailer() {
        if (tailer != null) {
            tailer.stop();
            try {
                tailerThread.join();
            } catch (InterruptedException e) {
                logger.log(Level.INFO,"The tailer was interrupted.", e);
                Thread.currentThread().interrupt();
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
