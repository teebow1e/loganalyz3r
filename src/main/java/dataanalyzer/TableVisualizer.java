package dataanalyzer;

import javafx.scene.Parent;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class TableVisualizer {
    public static void CreateTable(Parent root) throws IOException {
        PieChart pieChart = new PieChart();
        pieChart.getData().add(new PieChart.Data("Category A", 20));
        pieChart.getData().add(new PieChart.Data("Category B", 30));
        pieChart.getData().add(new PieChart.Data("Category C", 40));

        pieChart.getStyleClass().add("pie-chart");

        // System.out.println("PieChart created: " + pieChart);

        VBox mainVBox = (VBox) root.lookup("#mainVBox");
        mainVBox.getChildren().add(pieChart);
    }
}
