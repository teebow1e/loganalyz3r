package ui;

import controller.Controller;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SignUpForm {
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SignUpForm.fxml"));
        Parent root = loader.load();

        double width = primaryStage.getScene().getWidth();
        double height = primaryStage.getScene().getHeight();
        if (width <= 0) {
            width = 1200;
            height = 800;
        }

        Scene scene = new Scene(root, width, height);

        primaryStage.setTitle("Sign Up");
        primaryStage.setScene(scene);
        primaryStage.show();

        root.setOnMouseClicked(event -> scene.getRoot().requestFocus());

        Controller controller = loader.getController();
        controller.init(primaryStage);
    }
}
