package controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import ui.LoginForm;
import ui.WebLogManager;

public class SidebarController {
    @FXML
    private void handleDashboard(MouseEvent event) throws Exception {
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        WebLogManager webLogManager = new WebLogManager();
        webLogManager.start(primaryStage, 1);
    }

    @FXML
    private void handleStream(MouseEvent event) throws Exception {
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        WebLogManager webLogManager = new WebLogManager();
        webLogManager.start(primaryStage, 2);
    }

    @FXML
    private void handleViewlog(MouseEvent event) throws Exception {
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        WebLogManager webLogManager = new WebLogManager();
        webLogManager.start(primaryStage, 3);
    }

    @FXML
    private void handleViewModSec(MouseEvent event) throws Exception {
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        WebLogManager webLogManager = new WebLogManager();
        webLogManager.start(primaryStage, 4);
    }

    @FXML
    private void handleFeedback(MouseEvent event) throws Exception {
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        WebLogManager webLogManager = new WebLogManager();
        webLogManager.start(primaryStage, 5);
    }

    @FXML
    private void handleAboutUs(MouseEvent event) throws Exception {
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        WebLogManager webLogManager = new WebLogManager();
        webLogManager.start(primaryStage, 6);
    }

    @FXML
    private void handleOption(MouseEvent event) throws Exception {
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        WebLogManager webLogManager = new WebLogManager();
        webLogManager.start(primaryStage, 7);
    }

    @FXML
    private void handleLogout(MouseEvent event) throws Exception {
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        LoginForm loginForm = new LoginForm();
        loginForm.start(primaryStage);
    }
}