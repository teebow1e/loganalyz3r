package ui;

import controller.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginForm extends Application {
    @Override
    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginForm.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        double width;
        double height;
        if (primaryStage.getScene() == null) {
            width = 1300;
            height = 800;
        }
        else {
            width = primaryStage.getScene().getWidth();
            height = primaryStage.getScene().getHeight();
        }
        Scene scene = new Scene(root, width, height);

        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();

        root.setOnMouseClicked(event -> scene.getRoot().requestFocus());

        Controller controller = loader.getController();
        controller.init(primaryStage);
    }
}
