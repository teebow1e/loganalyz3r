package utility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminCredentialsDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton submitButton;
    private boolean success;

    public AdminCredentialsDialog(Frame parent) {
        super(parent, "Initialize Admin Credentials", true);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(20);
        add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        add(passwordField, gbc);

        // Submit Button
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
        AdminCredentialsDialog dialog = new AdminCredentialsDialog(null);
        dialog.setVisible(true);

        if (dialog.isSuccess()) {
            System.out.println("Username: " + dialog.getUsername());
            System.out.println("Password: " + dialog.getPassword());
        }
    }
}
