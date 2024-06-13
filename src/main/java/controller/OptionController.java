package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class OptionController {
    @FXML
    private TextField apacheLogLocationTF;

    @FXML
    private Button chooseApacheLocationBtn;

    @FXML
    private Button chooseModsecLocationBtn;

    @FXML
    private Button confirmApacheLocationBtn;

    @FXML
    private Button confirmModsecLocationBtn;

    @FXML
    private Button fetchUpdateDBBtn;

    @FXML
    private Text ipDBLocationText;

    @FXML
    private Text ipDBNewVerStatusText;

    @FXML
    private TextField modsecLogLocationTF;

    @FXML
    private Button updateDBBtn;

    @FXML
    void fetchUpdateDB(ActionEvent event) {
        System.out.println("you want to fetch update");
    }
}
