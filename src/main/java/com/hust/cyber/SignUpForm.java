package com.hust.cyber;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SignUpForm {
    public void start(Stage primaryStage) throws IOException {
        Scene signUpScene = new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/SignUpForm.fxml"))), 1200, 800);

        signUpScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/Form.css")).toExternalForm());

        signUpScene.getRoot().setOnMouseClicked(event -> signUpScene.getRoot().requestFocus());

        primaryStage.setScene(signUpScene);
        primaryStage.setTitle("Sign Up");
        primaryStage.show();
    }
}
