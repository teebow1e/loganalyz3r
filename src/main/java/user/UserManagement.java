package user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

    public static void addUser(String fileName, String username, String plaintextPassword) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(new File(fileName));
            ArrayNode usersNode = (ArrayNode) rootNode.path("users");
            String hashedPassword = BCrypt.hashpw(plaintextPassword, BCrypt.gensalt());
            ObjectNode newUserNode = objectMapper.createObjectNode();
            newUserNode.put("username", username);
            newUserNode.put("password_hash", hashedPassword);
            usersNode.add(newUserNode);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(fileName), rootNode);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "An error occurred while writing to the user file", e);
        }
    }
}
