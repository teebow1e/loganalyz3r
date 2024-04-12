// UI.java
package com.hust.cyber;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginForm extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create a button
        Button btn = new Button();
        btn.setText("Say 'Hello'");

        // Add action to the button
        btn.setOnAction(event -> {
            // Create a label with greeting message
            Label label = new Label("Hello!");

            // Create a VBox to hold the label
            VBox vbox = new VBox(label);
            vbox.setSpacing(10);

            // Create a new Scene with VBox as the root node
            Scene scene = new Scene(vbox, 200, 100);

            // Create a new Stage for the popup
            Stage popupStage = new Stage();
            popupStage.setScene(scene);
            popupStage.setTitle("Greeting");
            popupStage.show();
        });

        // Create a StackPane layout and add the Button to it
        StackPane root = new StackPane();
        root.getChildren().add(btn);

        // Create a Scene with the StackPane as the root node
        Scene scene = new Scene(root, 300, 250);

        // Set the Scene to the Stage and show the Stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Hello World Example");
        primaryStage.show();
    }
}
