package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import static utility.Utility.showAlert;

public class FeedbackController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextArea feedbackArea;
    @FXML
    private void handleSubmit() {
        String name = nameField.getText();
        String email = emailField.getText();
        String feedback = feedbackArea.getText();

        if (name.isEmpty() || email.isEmpty() || feedback.isEmpty()) {
            showAlert("Error", "All fields are required.");
        } else {
            showAlert("Success", "Thank you for your feedback!");
        }
    }
}
