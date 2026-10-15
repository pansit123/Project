package busbooking.ui.admin;

import busbooking.model.Admin;

import javax.swing.*;
import java.awt.*;

public class AdminLoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;

    // บัญชี Admin มีบัญชีเดียว จึงกำหนดไว้ในโค้ดเลย
    private final Admin admin = new Admin("A001", "admin", "1234");

    public AdminLoginFrame() {
        setTitle("เข้าสู่ระบบ Admin");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        createUI();
        setVisible(true);
    }

    private void createUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel titleLabel = new JLabel("เข้าสู่ระบบ Admin", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 90, 160));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtUsername = new JTextField();
        txtPassword = new JPasswordField();

        // แถว Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Username"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(txtUsername, gbc);

        // แถว Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Password"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(txtPassword, gbc);

        // ปุ่มกินพื้นที่ทั้ง 2 คอลัมน์
        JButton btnLogin = new JButton("เข้าสู่ระบบ");
        btnLogin.setBackground(new Color(40, 180, 70));
        btnLogin.setForeground(Color.WHITE);

        JButton btnBack = new JButton("กลับ");
        btnBack.setBackground(new Color(100, 100, 100));
        btnBack.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(btnLogin, gbc);
        gbc.gridy = 3;
        formPanel.add(btnBack, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        btnLogin.addActionListener(e -> login());
        txtPassword.addActionListener(e -> login());   // กด Enter ในช่องรหัสผ่านก็ login ได้
        // กลับไปหน้าเลือกประเภทผู้ใช้ (เดิมแค่ปิดหน้านี้ ทำให้โปรแกรมปิดไปเลย)
        btnBack.addActionListener(e -> {
            dispose();
            busbooking.Main.showLauncher();
        });

        setContentPane(mainPanel);
        busbooking.ui.UiTheme.useFontForDefaults(mainPanel, AdminUi.FONT_LABEL);
    }

    private void login() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "กรุณากรอก Username และ Password",
                    "แจ้งเตือน", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (admin.login(username, password)) {
            JOptionPane.showMessageDialog(this, "เข้าสู่ระบบสำเร็จ",
                    "สำเร็จ", JOptionPane.INFORMATION_MESSAGE);
            new AdminFrame();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Username หรือ Password ไม่ถูกต้อง",
                    "เข้าสู่ระบบไม่สำเร็จ", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            txtUsername.requestFocus();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminLoginFrame::new);
    }
}
