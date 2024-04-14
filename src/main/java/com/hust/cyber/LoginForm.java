package com.hust.cyber;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginForm extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginForm.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1200, 800);

        scene.getStylesheets().add(getClass().getResource("/css/Form.css").toExternalForm());

        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();

        LoginFormController controller = loader.getController();
        controller.init(primaryStage);
    }

}
