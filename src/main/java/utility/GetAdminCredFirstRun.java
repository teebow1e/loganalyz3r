package utility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GetAdminCredFirstRun extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton submitButton;
    private boolean success;
    private static final Logger logger = Logger.getLogger(GetAdminCredFirstRun.class.getName());

    public GetAdminCredFirstRun(Frame parent) {
        super(parent, "Create admin account", true);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(20);
        usernameField.setText("admin");
        usernameField.setEditable(false);
        add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        submitButton = new JButton("Submit");
        add(submitButton, gbc);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                success = true;
                dispose();
            }
        });

        pack();
        setLocationRelativeTo(parent);
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public boolean isSuccess() {
        return success;
    }

    public static void main(String[] args) {
        GetAdminCredFirstRun dialog = new GetAdminCredFirstRun(null);
        dialog.setVisible(true);

        if (dialog.isSuccess()) {
            logger.log(Level.INFO, "[FirstRun] Get admin creds from user successfully.");
        } else {
            logger.log(Level.INFO, "[FirstRun] Failed to init admin creds.");
        }
    }
}
