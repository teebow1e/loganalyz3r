package ui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.control.*;

public class SidebarController {

    private Stage stage;

    public void init(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleLogout(MouseEvent event) throws Exception {
        // Get the primary stage of the application
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Close the primary stage
        primaryStage.close();

        // Open the login form in a new stage
        LoginForm loginForm = new LoginForm();
        loginForm.start(new Stage());
    }
}
