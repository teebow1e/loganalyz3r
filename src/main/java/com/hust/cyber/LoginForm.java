package com.hust.cyber;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.Priority;

public class LoginForm extends Application {

    @Override
    public void start(Stage primaryStage) {
        Label label = new Label("Welcome to the Login Form");

        VBox vbox = new VBox(10);
        vbox.getChildren().add(label);
        VBox.setVgrow(vbox, Priority.ALWAYS);

        Scene scene = new Scene(vbox, 1200, 800);

        primaryStage.setScene(scene); // Set the scene to the stage
        primaryStage.setTitle("Login Form"); // Set the stage title
        primaryStage.show(); // Show the stage
    }

}
