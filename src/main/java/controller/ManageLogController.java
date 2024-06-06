package controller;

import dataanalyzer.TableVisualizer;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class ManageLogController {
    @FXML
    private TableView<String[]> Table;
    @FXML
    private void initialize() {
        try {
            manageLog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void manageLog() throws Exception {
        TableVisualizer.ManageLogTable(Table);
    }
}