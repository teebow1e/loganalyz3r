package utility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class LogPathSelectionDialog extends JDialog {
    private JTextField apacheLogPathField;
    private JTextField modSecurityLogPathField;
    private JButton browseApacheLogButton;
    private JButton browseModSecurityLogButton;
    private JButton submitButton;
    private boolean success;

    public LogPathSelectionDialog(Frame parent) {
        super(parent, "Select Log Paths", true);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Apache Log Path
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        add(new JLabel("Apache Log Path:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        apacheLogPathField = new JTextField(20);
        add(apacheLogPathField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        browseApacheLogButton = new JButton("Browse");
        add(browseApacheLogButton, gbc);

        // ModSecurity Log Path
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        add(new JLabel("ModSecurity Log Path:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        modSecurityLogPathField = new JTextField(20);
        add(modSecurityLogPathField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        browseModSecurityLogButton = new JButton("Browse");
        add(browseModSecurityLogButton, gbc);

        // Submit Button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        submitButton = new JButton("Submit");
        add(submitButton, gbc);

        browseApacheLogButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int option = fileChooser.showOpenDialog(LogPathSelectionDialog.this);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    apacheLogPathField.setText(file.getAbsolutePath());
                }
            }
        });

        browseModSecurityLogButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int option = fileChooser.showOpenDialog(LogPathSelectionDialog.this);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    modSecurityLogPathField.setText(file.getAbsolutePath());
                }
            }
        });

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

    public String getApacheLogPath() {
        return apacheLogPathField.getText();
    }

    public String getModSecurityLogPath() {
        return modSecurityLogPathField.getText();
    }

    public boolean isSuccess() {
        return success;
    }

    public static void main(String[] args) {
        LogPathSelectionDialog dialog = new LogPathSelectionDialog(null);
        dialog.setVisible(true);

        if (dialog.isSuccess()) {
            System.out.println("Apache Log Path: " + dialog.getApacheLogPath());
            System.out.println("ModSecurity Log Path: " + dialog.getModSecurityLogPath());
        }
    }
}
