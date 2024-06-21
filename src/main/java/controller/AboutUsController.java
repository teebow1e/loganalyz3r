package controller;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class AboutUsController {

    @FXML
    void openGitHubLink(MouseEvent event) {
        try {
            System.out.println("HELLO"); // Verify if this message prints in the console
            Desktop.getDesktop().browse(new URI("https://github.com/teebow1e/loganalyz3r/"));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
