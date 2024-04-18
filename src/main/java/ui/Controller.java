package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.control.*;
import usermanagement.User;
import usermanagement.UserManagement;

import javax.swing.JOptionPane;

import java.io.File;
import java.util.List;

public class Controller {
    private Stage stage;
    public List<User> userLists;

    public void init(Stage stage) {
        String dbFilePath = System.getProperty("user.dir") + "\\credentials\\cred.txt";
        File dbFile = new File(dbFilePath);
        if (dbFile.exists()) {
            System.out.println("exists: " + dbFile.getAbsolutePath());
            userLists = UserManagement.readUserFile(dbFilePath);
        } else {
            System.out.println("not exists: " + dbFile.getAbsolutePath());
        }
        this.stage = stage;
    }

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField rePasswordField;

    @FXML
    private void onEnter(ActionEvent ae) throws Exception {
        System.out.println("Key Enter clicked");
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
            System.out.println("Login successful");
            WebLogManager webLogManager = new WebLogManager();
            webLogManager.start(stage);
        } else {
            System.out.println("Login failed");
            JOptionPane.showMessageDialog(null, "Nhap sai password roi cu!");
        }
    }

    @FXML
    private void handleLogout(MouseEvent event) throws Exception {
        LoginForm loginForm = new LoginForm();
        stage.close();
        loginForm.start(new Stage());
    }
}
