package utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.scene.control.Alert;
import user.UserManagement;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;

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

    public static void updateConfigValue(String fileName, String key, String newValue) {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(fileName);

        try {
            JsonNode rootNode;

            if (file.exists() && file.length() != 0) {
                rootNode = objectMapper.readTree(file);
            } else {
                rootNode = objectMapper.createObjectNode();
            }

            if (rootNode instanceof ObjectNode objectNode) {
                objectNode.put(key, newValue);

                objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, rootNode);
                System.out.println("Updated key \"" + key + "\" with value \"" + newValue + "\".");
            } else {
                System.out.println("Root node is not an object node.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while updating the configuration file");
        }
    }

    public static void showFirstRunMessage() {
        JOptionPane.showMessageDialog(
                null,
                "It looks like this is the first time you are opening this app. You have to perform first run actions. Click OK to continue.",
                "First Run",
                JOptionPane.INFORMATION_MESSAGE
        );
    }


    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
