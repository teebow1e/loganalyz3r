package ui;

import controller.*;
import entrypoint.Config;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class WebLogManager extends Application {
    @Override
    public void start(Stage primaryStage) {
        start(primaryStage, 1);
    }

    public void start(Stage primaryStage, int mode) {
        FXMLLoader loader;
        String fxmlFile = switch (mode) {
            case 1 -> "/fxml/main/Dashboard.fxml";
            case 2 -> "/fxml/main/Stream.fxml";
            case 3 -> "/fxml/main/Viewlog.fxml";
            case 4 -> "/fxml/main/Viewmodsec.fxml";
            case 5 -> "/fxml/main/Feedback.fxml";
            case 6 -> "/fxml/main/AboutUs.fxml";
            case 7 -> "/fxml/main/Option.fxml";
            default -> throw new IllegalArgumentException("Invalid mode specified: " + mode);
        };

        double width = primaryStage.getScene().getWidth();
        double height = primaryStage.getScene().getHeight();
        loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Scene scene = new Scene(root, width, height);

        primaryStage.setTitle(Config.PROJECT_NAME);
        primaryStage.setScene(scene);
        primaryStage.show();

        Controller controller = loader.getController();
        controller.init(primaryStage);
    }
    public static void main(String[] args) {
        launch(args);
    }
}
