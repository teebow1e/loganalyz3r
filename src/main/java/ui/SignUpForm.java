package ui;

import controller.Controller;
import entrypoint.Config;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SignUpForm {
    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SignUpForm.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        double width = primaryStage.getScene().getWidth();
        double height = primaryStage.getScene().getHeight();
        Scene scene = new Scene(root, width, height);

        primaryStage.setTitle(Config.PROJECT_NAME);
        primaryStage.setScene(scene);
        primaryStage.show();

        root.setOnMouseClicked(event -> scene.getRoot().requestFocus());

        Controller controller = loader.getController();
        controller.init(primaryStage);
    }
}
