package ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SignUpForm {
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SignUpForm.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1000, 600);

        scene.getStylesheets().add(getClass().getResource("/css/Form.css").toExternalForm());

        primaryStage.setTitle("Sign Up");
        primaryStage.setScene(scene);
        primaryStage.show();

        root.setOnMouseClicked(event -> scene.getRoot().requestFocus());

        Controller controller = loader.getController();
        controller.init(primaryStage);
    }
}
