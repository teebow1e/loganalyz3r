package utility;

import javafx.scene.control.Alert;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.LinkedList;

public class Utility {
    private Utility() {
        throw new IllegalStateException("Utility class");
    }
    public static String findFirstMatch(String line, Pattern pattern) {
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public static String getElementSafely(String[] array, int index) {
        if (index >= 0 && index < array.length) {
            return array[index];
        } else {
            return null;
        }
    }

    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
