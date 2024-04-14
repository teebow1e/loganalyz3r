package com.hust.cyber;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginFormController {
    @FXML
    private void handleCreateAccount(MouseEvent event) {
        try {
            SignUpForm signUpForm = new SignUpForm();
            Stage stage = new Stage(); // Create a new stage for the sign-up form
            signUpForm.start(stage); // Open the sign-up form
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
