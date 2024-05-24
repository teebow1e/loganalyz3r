package controller;

import dataanalyzer.PieChartVisualizer;
import dataanalyzer.TableVisualizer;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;

public class DashboardController {

    @FXML
    private void initialize() {
        try {
            dashBoard();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dashBoard() throws Exception {
        System.out.println("HELLO");
    }
}
