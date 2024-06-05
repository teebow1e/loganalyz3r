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
        start(primaryStage, 1);
    }

    public void start(Stage primaryStage, int mode) throws Exception {
        FXMLLoader loader = null;
        String fxmlFile = switch (mode) {
            case 1 -> "/fxml/main/Dashboard.fxml";
            case 2 -> "/fxml/main/Stream.fxml";
            case 3 -> "/fxml/main/Viewlog.fxml";
            case 4 -> "/fxml/main/Viewmodsec.fxml";
            case 5 -> "/fxml/main/Feedback.fxml";
            case 6 -> "/fxml/main/Export.fxml";
//            case 7 -> "/fxml/main/Managelog.fxml";
            default -> throw new IllegalArgumentException("Invalid mode specified: " + mode);
        };

        // System.out.println("FXML File Path: " + getClass().getResource(fxmlFile));
        loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/css/Sidebar.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/Table.css").toExternalForm());

        primaryStage.setTitle("Web Log Analyzer");
        primaryStage.setScene(scene);
        primaryStage.show();

        // root.setOnMouseClicked(event -> scene.getRoot().requestFocus());

        Controller controller = loader.getController();
        controller.init(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
