package busbooking.ui;

import busbooking.model.Admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AdminLoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    private Admin admin;

    public AdminLoginFrame() {

        // ข้อมูล Admin สำหรับทดสอบ
        admin = new Admin(
                "A001",
                "admin",
                "1234"
        );

        setTitle("Admin Login");
        setSize(450, 330);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        createUI();

        setVisible(true);
    }

    private void createUI() {

        // Main Panel

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(
                new EmptyBorder(25, 35, 25, 35)
        );

        // Title
     
        JLabel title = new JLabel(
                "เข้าสู่ระบบ Admin",
                SwingConstants.CENTER
        );

        title.setFont(
                new Font("Tahoma", Font.BOLD, 24)
        );

        mainPanel.add(
                title,
                BorderLayout.NORTH
        );

        // Form Panel

        JPanel formPanel =
                new JPanel(new GridBagLayout());

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(8, 5, 8, 5);

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        // Username Label

        JLabel usernameLabel =
                new JLabel("Username");

        usernameLabel.setFont(
                new Font("Tahoma", Font.PLAIN, 16)
        );

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;

        formPanel.add(
                usernameLabel,
                gbc
        );

        // Username TextField

        usernameField =
                new JTextField();

        usernameField.setFont(
                new Font("Tahoma", Font.PLAIN, 16)
        );

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;

        formPanel.add(
                usernameField,
                gbc
        );

        // Password Label

        JLabel passwordLabel =
                new JLabel("Password");

        passwordLabel.setFont(
                new Font("Tahoma", Font.PLAIN, 16)
        );

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;

        formPanel.add(
                passwordLabel,
                gbc
        );

        // Password Field

        passwordField =
                new JPasswordField();

        passwordField.setFont(
                new Font("Tahoma", Font.PLAIN, 16)
        );

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;

        formPanel.add(
                passwordField,
                gbc
        );

        mainPanel.add(
                formPanel,
                BorderLayout.CENTER
        );

        // Login Button

        JButton loginButton =
                new JButton("เข้าสู่ระบบ");

        loginButton.setFont(
                new Font("Tahoma", Font.BOLD, 16)
        );

        loginButton.setPreferredSize(
                new Dimension(150, 45)
        );

        mainPanel.add(
                loginButton,
                BorderLayout.SOUTH
        );

        // Button Action

        loginButton.addActionListener(
                e -> login()
        );

        // กด Enter เพื่อ Login
        passwordField.addActionListener(
                e -> login()
        );

        add(mainPanel);
    }

    private void login() {

        String username =
                usernameField.getText().trim();

        String password =
                new String(
                        passwordField.getPassword()
                );

        // ตรวจสอบช่องว่าง
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
        if (admin.login(username, password)) {

            JOptionPane.showMessageDialog(
                    this,
                  "เข้าสู่ระบบสำเร็จ",
                  "สำเร็จ",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();

            new AdminFrame(admin);

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Username หรือ Password ไม่ถูกต้อง",
                    "เข้าสู่ระบบไม่สำเร็จ",
                    JOptionPane.ERROR_MESSAGE
            );

            passwordField.setText("");
        }
    }
}