package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class WebLogManager extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1000, 600);
        scene.getStylesheets().add(getClass().getResource("/css/WebLog.css").toExternalForm());

        primaryStage.setTitle("Web Log Analyzer");
        primaryStage.setScene(scene);
        primaryStage.show();

        root.setOnMouseClicked(event -> scene.getRoot().requestFocus());

        Controller controller = loader.getController();
        controller.init(primaryStage);
    }
}
