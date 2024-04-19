package usermanagement;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserManagement {
    private static final Logger logger = Logger.getLogger(UserManagement.class.getName());
    private UserManagement() {
        throw new IllegalStateException("Utility class");
    }
    public static List<User> readUserFile(String fileName) {
        List<User> userList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String username = parts[0];
                    String password = parts[1];
                    String role = parts[2];
                    userList.add(new User(username, password, role));
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "An error occurred while reading the user file", e);
        }
        return userList;
    }

    public static boolean authenticateUser(List<User> userList, String username, String password) {
        for (User user : userList) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }
}
