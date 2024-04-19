//package dataanalyzer;
//
//import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.chart.PieChart;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//
//public class PieChartVisualizer extends Application {
//
//    @Override
//    public void start(Stage primaryStage) throws IOException {
//        // Load the FXML layout file
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PieChart.fxml"));
//        Parent root = loader.load();
//
//        // Get the PieChart from the FXML
//        PieChart pieChart = (PieChart) loader.getNamespace().get("pieChart");
//
//        // Add sample data to the PieChart
//        pieChart.getData().add(new PieChart.Data("Category A", 30));
//        pieChart.getData().add(new PieChart.Data("Category B", 20));
//        pieChart.getData().add(new PieChart.Data("Category C", 50));
//
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}
