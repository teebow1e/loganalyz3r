package controller;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;

public class PieChartController {

    @FXML
    private PieChart pieChart;

    public void initialize() {
        // Initialize or update the PieChart as needed
        pieChart.getData().clear();
        pieChart.getData().add(new PieChart.Data("Category A", 40));
        pieChart.getData().add(new PieChart.Data("Category B", 40));
        pieChart.getData().add(new PieChart.Data("Category C", 40));
    }
}
