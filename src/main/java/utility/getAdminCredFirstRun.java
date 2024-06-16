package utility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class getAdminCredFirstRun extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton submitButton;
    private boolean success;

    public getAdminCredFirstRun(Frame parent) {
        super(parent, "Initialize Admin Credentials", true);
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
        getAdminCredFirstRun dialog = new getAdminCredFirstRun(null);
        dialog.setVisible(true);

        if (dialog.isSuccess()) {
            System.out.println("[DEBUG] Username: " + dialog.getUsername());
            System.out.println("[DEBUG] Password: " + dialog.getPassword());
        }
    }
}
