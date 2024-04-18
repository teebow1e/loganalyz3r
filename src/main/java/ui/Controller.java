package ui;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.control.*;

public class Controller {
    private Stage stage;

    // Method to initialize the stage
    public void init(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField rePasswordField;

    @FXML
    private void handleCreateAccount(MouseEvent event) throws Exception{
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.start(stage);
    }

    @FXML
    private void returnToLogin(MouseEvent event) throws Exception{
        LoginForm loginForm = new LoginForm();
        loginForm.start(stage);
    }
    @FXML
    private void handleSignupAccount(MouseEvent event) throws Exception{
        String username = usernameField.getText();
        String password = passwordField.getText();
        String rePassword = rePasswordField.getText();

        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        System.out.println("Re-entered Password: " + rePassword);

        LoginForm loginForm = new LoginForm();
        loginForm.start(stage);
    }

    @FXML
    private void handleLoginAccount(MouseEvent event) throws Exception {
        String username = usernameField.getText();
        String password = passwordField.getText();

        System.out.println("Username: " + username);
        System.out.println("Password: " + password);

        WebLogManager webLogManager = new WebLogManager();
        webLogManager.start(stage);
    }

    @FXML
    private void handleLogout(MouseEvent event) throws Exception {
        LoginForm loginForm = new LoginForm();
        stage.close();
        loginForm.start(new Stage());
    }
}
