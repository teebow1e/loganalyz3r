package com.hust.cyber;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginFormController {
    private Stage stage;

    // Method to initialize the stage
    public void init(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleCreateAccount(MouseEvent event) throws Exception{
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.start(stage);
    }

    @FXML
    private void handleSubmitAccount(MouseEvent event) throws Exception{
        LoginForm loginForm = new LoginForm();
        loginForm.start(stage);
    }
}
