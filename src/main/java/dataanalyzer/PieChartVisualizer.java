package dataanalyzer;

import javafx.scene.Parent;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class PieChartVisualizer  {
    public static void CreatePieChart(Parent root) throws IOException {
        PieChart pieChart = new PieChart();
        pieChart.getData().add(new PieChart.Data("Category A", 40));
        pieChart.getData().add(new PieChart.Data("Category B", 40));
        pieChart.getData().add(new PieChart.Data("Category C", 40));

        pieChart.getStyleClass().add("pie-chart");

        // System.out.println("PieChart created: " + pieChart);

        VBox mainVBox = (VBox) root.lookup("#mainVBox");
        mainVBox.getChildren().add(pieChart);
    }
}
