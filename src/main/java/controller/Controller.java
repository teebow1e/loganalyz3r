package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.control.*;

import ui.LoginForm;
import ui.SignUpForm;
import ui.WebLogManager;

import usermanagement.User;
import usermanagement.UserManagement;

import javax.swing.JOptionPane;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Controller {
    private Stage stage;
    private List<User> userLists;
    private final Logger logger = Logger.getLogger(Controller.class.getName());

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField rePasswordField;

    public void init(Stage stage) {
        String dbFilePath = System.getProperty("user.dir") + "\\credentials\\cred.txt";
        File dbFile = new File(dbFilePath);
        if (dbFile.exists()) {
            userLists = UserManagement.readUserFile(dbFilePath);
        } else {
            logger.log(Level.SEVERE, "DB file not exists: {0}", dbFile.getAbsolutePath());
        }
        this.stage = stage;
    }
    @FXML
    private void onEnter(ActionEvent event) throws Exception {
        handleLoginAccount(null);
    }
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
        boolean authenticated = UserManagement.authenticateUser(userLists, username, password);
        if (authenticated) {
            logger.log(Level.INFO, "Login successful");
            WebLogManager webLogManager = new WebLogManager();
            webLogManager.start(stage);
        } else {
            logger.log(Level.INFO, "Login failed");
            JOptionPane.showMessageDialog(null, "Nhap sai password roi cu!");
        }
    }
}
