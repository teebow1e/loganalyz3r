package controller;

import dataanalyzer.PieChartVisualizer;
import dataanalyzer.TableVisualizer;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;

public class ViewLogController {

    @FXML
    private TableView<?> Table; // This should match the fx:id defined in your FXML

    @FXML
    private void initialize() {
        try {
            viewLog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void viewLog() throws Exception {
        TableVisualizer.ShowLogTable((TableView<String[]>) Table);
    }
}
