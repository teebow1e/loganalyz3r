package com.hust.cyber;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class LoginForm extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file
        System.out.println("test");
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/LoginForm.fxml")));

        // Create a Scene with the loaded root
        Scene scene = new Scene(root, 300, 200);

        // Set the Scene to the Stage and show the Stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login Form");
        primaryStage.show();
    }

}
