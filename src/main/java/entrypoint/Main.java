package entrypoint;

import javafx.application.Application;
import ui.LoginForm;
import utility.Utility;

import javax.swing.JOptionPane;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (!Config.checkConfigDir()) {
            Utility.showFirstRunMessage();
            Config.firstRunAction();
        }
        boolean runtimeCheck = Config.checkAccountsFile() && Config.checkConfigFile();
        if (!runtimeCheck) {
            JOptionPane.showMessageDialog(null,
                    "Account files and Config file does not exist. You should try remove the .config folder and try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Config.loadConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Application.launch(LoginForm.class, args);
    }
}