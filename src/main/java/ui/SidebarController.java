package ui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class SidebarController {
    private Stage stage;

    public void init(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleLogout(MouseEvent event) throws Exception {
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        LoginForm loginForm = new LoginForm();
        loginForm.start(primaryStage);
    }
}
