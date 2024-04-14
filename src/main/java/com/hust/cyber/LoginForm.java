package com.hust.cyber;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class LoginForm extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene loginscene = new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/LoginForm.fxml"))), 1200, 800);

        loginscene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/Form.css")).toExternalForm());

        loginscene.getRoot().setOnMouseClicked(event -> loginscene.getRoot().requestFocus());

        primaryStage.setScene(loginscene);
        primaryStage.setTitle("Log Analyzer");
        primaryStage.show();
    }

}
