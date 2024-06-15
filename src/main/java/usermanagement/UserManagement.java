package usermanagement;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mindrot.jbcrypt.BCrypt;

import java.io.*;
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
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(new File(fileName));
            JsonNode usersNode = rootNode.path("users");
            if (usersNode.isArray()) {
                for (JsonNode userNode : usersNode) {
                    String username = userNode.path("username").asText();
                    String passwordHash = userNode.path("password_hash").asText();
                    userList.add(new User(username, passwordHash));
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "An error occurred while reading the user file", e);
        }
        return userList;
    }

    public static boolean authenticateUser(List<User> userList, String username, String password) {
        for (User user : userList) {
            if (user.getUsername().equals(username) && BCrypt.checkpw(password, user.getPasswordHash())) {
                return true;
            }
        }
        return false;
    }

    public static void addUser(String fileName, User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            String hashedPassword = BCrypt.hashpw(user.getPasswordHash(), BCrypt.gensalt());
            writer.write(user.getUsername() + "," + hashedPassword + "\n");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "An error occurred while writing to the user file", e);
        }
    }

    public static void main(String[] args) {
        System.out.println(BCrypt.hashpw("a", BCrypt.gensalt()));
        System.out.println(BCrypt.checkpw("a", "$2a$10$t.g7znEK3FTRtqNZSHb4c.D.IdnUm8SBobTV/xjq6uvs7iB7qng/m"));
    }
}
