package ui;

import controller.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class WebLogManager extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the Dashboard.fxml file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1000, 600);
        scene.getStylesheets().add(getClass().getResource("/css/WebLog.css").toExternalForm());

        primaryStage.setTitle("Web Log Analyzer");
        primaryStage.setScene(scene);
        primaryStage.show();

        root.setOnMouseClicked(event -> scene.getRoot().requestFocus());

        // Pass the primary stage to the controller
        Controller controller = loader.getController();
        controller.init(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
