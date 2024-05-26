package controller;

import dataanalyzer.TableVisualizer;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class ManageLogController {

    @FXML
    private TableView<?> Table; // This should match the fx:id defined in your FXML

    @FXML
    private void initialize() {
        try {
            manageLog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void manageLog() throws Exception {
        TableVisualizer.ManageLogTable((TableView<String[]>) Table);
    }
}