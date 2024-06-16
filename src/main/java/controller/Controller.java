package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.control.*;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.logging.Level;

import ui.LoginForm;
import ui.SignUpForm;
import ui.WebLogManager;
import user.User;
import user.UserManagement;
import entrypoint.Config;

import static utility.Utility.showAlert;

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

    // CONSTANT VALUE HERE
    private String dbFilePath = System.getProperty("user.dir")
            + File.separator
            + ".config"
            + File.separator
            + "accounts.json";

    public void init(Stage stage) {
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
    private void handleSignupAccount(MouseEvent event) throws Exception {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String rePassword = rePasswordField.getText();

        if (username.isEmpty()) {
            showAlert(Config.ERROR_LABEL, "Username can not be empty");
            returnToLogin(event);
            return;
        }

        if (password.length() < 8) {
            showAlert(Config.ERROR_LABEL, "Password must be longer than 8 characters.");
            returnToLogin(event);
            return;
        }

        if (!Objects.equals(password, rePassword)) {
            showAlert(Config.ERROR_LABEL,
                    "Password and Re-enter password field must be the same."
            );
            returnToLogin(event);
            return;
        }
        UserManagement.addUser(dbFilePath, username, password);
        returnToLogin(event);
    }

    @FXML
    private void handleLoginAccount(MouseEvent event) throws Exception {
        String username = usernameField.getText();
        String password = passwordField.getText();

        boolean authenticated = UserManagement.authenticateUser(userLists, username, password);
        if (authenticated) {
            logger.log(Level.INFO, "[{0}] User authenticated successfully.", username);
            Config.setCurrentlyLoggedOnUser(username);
            WebLogManager webLogManager = new WebLogManager();
            webLogManager.start(stage);
        } else {
            logger.log(Level.INFO, "User failed to authenticate with username {0}.", username);
            showAlert("Wrong Password", "Nhap sai password roi cu!");
        }
    }
}
