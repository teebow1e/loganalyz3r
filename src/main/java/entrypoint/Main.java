package entrypoint;

import javafx.application.Application;
import ui.LoginForm;
import javax.swing.JOptionPane;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (!Config.checkConfigDir()) {
            // perform first run action
            // WorkInProgress, not finished yet
            new utility.AdminCredentialsDialog(null);
            new utility.LogPathSelectionDialog(null);
        }
        boolean runtimeCheck = Config.checkAccountsFile() && Config.checkConfigFile();
        if (!runtimeCheck) {
            JOptionPane.showMessageDialog(null,
                    "Account files and Config file does not exist.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Config.loadConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        System.out.println(Config.getApacheLogLocation());
//        System.out.println(Config.getModSecurityLogLocation());
        Application.launch(LoginForm.class, args);
    }
}
