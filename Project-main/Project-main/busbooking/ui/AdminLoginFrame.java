package busbooking.ui;

import busbooking.model.Admin;

import javax.swing.*;
import java.awt.*;

public class AdminLoginFrame extends JFrame {

    // Components

    private JTextField txtUsername;
    private JPasswordField txtPassword;

    private JButton btnLogin;
    private JButton btnBack;

    // Admin Account

    private final Admin admin = new Admin(
            "A001",
            "admin",
            "1234"
    );

    // Constructor

    public AdminLoginFrame() {

        setTitle("เข้าสู่ระบบ Admin");

        setSize(500, 400);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        setResizable(false);

        setupFont();

        createUI();

        setVisible(true);
    }

    // Font

    private void setupFont() {

        UIManager.put(
                "Label.font",
                new Font(
                        "Tahoma",
                        Font.PLAIN,
                        16
                )
        );

        UIManager.put(
                "Button.font",
                new Font(
                        "Tahoma",
                        Font.PLAIN,
                        16
                )
        );

        UIManager.put(
                "TextField.font",
                new Font(
                        "Tahoma",
                        Font.PLAIN,
                        16
                )
        );

        UIManager.put(
                "PasswordField.font",
                new Font(
                        "Tahoma",
                        Font.PLAIN,
                        16
                )
        );

        UIManager.put(
                "OptionPane.messageFont",
                new Font(
                        "Tahoma",
                        Font.PLAIN,
                        16
                )
        );
    }

    // Create UI

    private void createUI() {

        JPanel mainPanel =
                new JPanel(
                        new BorderLayout()
                );

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        30,
                        40,
                        30,
                        40
                )
        );

        // Title
      
        JLabel titleLabel =
                new JLabel(
                        "เข้าสู่ระบบ Admin",
                        SwingConstants.CENTER
                );

        titleLabel.setFont(
                new Font(
                        "Tahoma",
                        Font.BOLD,
                        28
                )
        );

        titleLabel.setForeground(
                new Color(
                        0,
                        90,
                        160
                )
        );

        mainPanel.add(
                titleLabel,
                BorderLayout.NORTH
        );

        // Form

        JPanel formPanel =
                new JPanel(
                        new GridBagLayout()
                );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(
                        10,
                        10,
                        10,
                        10
                );

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;

        formPanel.add(
                new JLabel("Username"),
                gbc
        );

        txtUsername =
                new JTextField();

        gbc.gridx = 1;
        gbc.weightx = 1;

        formPanel.add(
                txtUsername,
                gbc
        );

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;

        formPanel.add(
                new JLabel("Password"),
                gbc
        );

        txtPassword =
                new JPasswordField();

        gbc.gridx = 1;
        gbc.weightx = 1;

        formPanel.add(
                txtPassword,
                gbc
        );

        // Buttons

        btnLogin =
                new JButton("เข้าสู่ระบบ");

        btnBack =
                new JButton("กลับ");

        btnLogin.setBackground(
                new Color(
                        40,
                        180,
                        70
                )
        );

        btnLogin.setForeground(
                Color.WHITE
        );

        btnBack.setBackground(
                new Color(
                        100,
                        100,
                        100
                )
        );

        btnBack.setForeground(
                Color.WHITE
        );

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;

        formPanel.add(
                btnLogin,
                gbc
        );

        gbc.gridy = 3;

        formPanel.add(
                btnBack,
                gbc
        );

        mainPanel.add(
                formPanel,
                BorderLayout.CENTER
        );

        // Events
        
        btnLogin.addActionListener(
                e -> login()
        );

        btnBack.addActionListener(
                e -> dispose()
        );

        txtPassword.addActionListener(
                e -> login()
        );

        setContentPane(mainPanel);
    }

    // Login

    private void login() {

        String username =
                txtUsername
                        .getText()
                        .trim();

        String password =
                new String(
                        txtPassword
                                .getPassword()
                );

        if (username.isEmpty()
                || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "กรุณากรอก Username และ Password",
                    "แจ้งเตือน",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // ตรวจสอบ Login

        if (admin.login(
                username,
                password
        )) {

            JOptionPane.showMessageDialog(
                    this,
                    "เข้าสู่ระบบสำเร็จ",
                    "สำเร็จ",
                    JOptionPane.INFORMATION_MESSAGE
            );
          
            // เปิดหน้า Admin

            new AdminFrame();
           
            // ปิดหน้า Login

            dispose();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Username หรือ Password ไม่ถูกต้อง",
                    "เข้าสู่ระบบไม่สำเร็จ",
                    JOptionPane.ERROR_MESSAGE
            );

            txtPassword.setText("");

            txtUsername.requestFocus();
        }
    }
   
    // Main
  
    public static void main(String[] args) {

        SwingUtilities.invokeLater(
                () -> new AdminLoginFrame()
        );
    }
}