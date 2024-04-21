package ui;

import controller.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class WebLogManager extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        start(primaryStage, 1);
    }

    public void start(Stage primaryStage, int mode) throws Exception {
        FXMLLoader loader = null;
        String fxmlFile = switch (mode) {
            case 1 -> "/fxml/Dashboard.fxml";
            case 2 -> "/fxml/Viewlog.fxml";
            case 3 -> "/fxml/Feedback.fxml";
            case 4 -> "/fxml/Export.fxml";
            case 5 -> "/fxml/Managelog.fxml";
            default -> throw new IllegalArgumentException("Invalid mode specified: " + mode);
        };

        loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = loader.load();
        // System.out.println(fxmlFile + " loaded successfully: " + root);

        Scene scene = new Scene(root, 1000, 600);
        scene.getStylesheets().add(getClass().getResource("/css/WebLog.css").toExternalForm());

        primaryStage.setTitle("Web Log Analyzer");
        primaryStage.setScene(scene);
        primaryStage.show();

        root.setOnMouseClicked(event -> scene.getRoot().requestFocus());

        Controller controller = loader.getController();
        controller.init(primaryStage);

        if (mode == 1) {
            controller.CreatePieChart(root);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
