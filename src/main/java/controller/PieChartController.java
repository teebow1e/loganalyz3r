package controller;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class PieChartController {

    @FXML
    private PieChart pieChart;

    @FXML
    public void initialize() {
        pieChart.getData().clear();
        pieChart.getData().add(new PieChart.Data("Category A", 30));
        pieChart.getData().add(new PieChart.Data("Category B", 20));
        pieChart.getData().add(new PieChart.Data("Category C", 50));
    }
}