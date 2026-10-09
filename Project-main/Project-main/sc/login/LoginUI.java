package login;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class LoginUI extends JFrame {

    private JTextField userField;
    private JPasswordField passField;
    private JButton loginButton;
    private JLabel registerLink;

    // ข้อมูลจำลอง User
    private User user = new User(
            "U001",
            "สมชาย ใจดี",
            "somchai@email.com",
            "password123",
            "0812345678"
    );

    public LoginUI() {
        setTitle("เข้าสู่ระบบ - ระบบจองตั๋วรถบัส");
        setSize(850, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 850, 520, 20, 20));

        Font thaiFont = new Font("Tahoma", Font.PLAIN, 13);
        setUIFont(new javax.swing.plaf.FontUIResource(thaiFont));

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.setBackground(Color.WHITE);

        // =====================================================
        // ฝั่งซ้าย : LOGIN (จัดกึ่งกลางทั้งหมด)
        // =====================================================
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(new EmptyBorder(30, 45, 30, 45));

        // ปุ่มปิด
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        topBar.setOpaque(false);
        topBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        JButton closeBtn = new JButton("✕");
        closeBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
        closeBtn.setForeground(new Color(100, 100, 100));
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> System.exit(0));
        topBar.add(closeBtn);
        leftPanel.add(topBar);

        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // หัวข้อ "เข้าสู่ระบบ"
        JLabel titleLabel = new JLabel("เข้าสู่ระบบ");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 24));
        titleLabel.setForeground(new Color(30, 30, 30));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(titleLabel);

        leftPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        Dimension fieldSize = new Dimension(280, 38);

        // --- EMAIL GROUP ---
        JPanel emailGroup = new JPanel();
        emailGroup.setLayout(new BoxLayout(emailGroup, BoxLayout.Y_AXIS));
        emailGroup.setOpaque(false);
        emailGroup.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailGroup.setMaximumSize(new Dimension(280, 65));

        JPanel emailHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        emailHeader.setOpaque(false);
        emailHeader.setMaximumSize(new Dimension(280, 18));
        
        JLabel userLabel = new JLabel("Email address");
        userLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        userLabel.setForeground(new Color(80, 80, 80));
        emailHeader.add(userLabel);
        
        emailGroup.add(emailHeader);
        emailGroup.add(Box.createRigidArea(new Dimension(0, 5)));

        userField = new JTextField();
        styleTextField(userField, fieldSize);
        emailGroup.add(userField);

        leftPanel.add(emailGroup);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // --- PASSWORD GROUP ---
        JPanel passGroup = new JPanel();
        passGroup.setLayout(new BoxLayout(passGroup, BoxLayout.Y_AXIS));
        passGroup.setOpaque(false);
        passGroup.setAlignmentX(Component.CENTER_ALIGNMENT);
        passGroup.setMaximumSize(new Dimension(280, 65));

        JPanel passHeader = new JPanel(new BorderLayout());
        passHeader.setOpaque(false);
        passHeader.setMaximumSize(new Dimension(280, 18));

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        passLabel.setForeground(new Color(80, 80, 80));

        JLabel forgotPass = new JLabel(
                "<html><a href='' style='color:#666666;text-decoration:none;font-size:11px;'>forgot password</a></html>"
        );
        forgotPass.setCursor(new Cursor(Cursor.HAND_CURSOR));

        passHeader.add(passLabel, BorderLayout.WEST);
        passHeader.add(forgotPass, BorderLayout.EAST);

        passGroup.add(passHeader);
        passGroup.add(Box.createRigidArea(new Dimension(0, 5)));

        passField = new JPasswordField();
        styleTextField(passField, fieldSize);
        passGroup.add(passField);

        leftPanel.add(passGroup);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // --- REMEMBER ME ---
        JPanel rememberWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rememberWrapper.setOpaque(false);
        rememberWrapper.setMaximumSize(new Dimension(280, 25));
        rememberWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);

        JCheckBox rememberCheck = new JCheckBox(" Remember for 30 days");
        rememberCheck.setBackground(Color.WHITE);
        rememberCheck.setFont(new Font("Tahoma", Font.PLAIN, 12));
        rememberCheck.setForeground(new Color(100, 100, 100));
        rememberWrapper.add(rememberCheck);
        leftPanel.add(rememberWrapper);

        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // --- LOGIN BUTTON ---
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Tahoma", Font.BOLD, 14));
        loginButton.setForeground(Color.BLACK);
        loginButton.setBackground(new Color(255, 193, 7));
        loginButton.setFocusPainted(false);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(280, 40));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        leftPanel.add(loginButton);

        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // --- SIGN UP ---
        JPanel signUpWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        signUpWrapper.setOpaque(false);
        signUpWrapper.setMaximumSize(new Dimension(280, 25));
        signUpWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);

        registerLink = new JLabel(
                "<html>Don't have an account? <a href='' style='color:#000000;font-weight:bold;'>Sign Up</a></html>"
        );
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signUpWrapper.add(registerLink);
        leftPanel.add(signUpWrapper);

        // =====================================================
        // ฝั่งขวา : รูปภาพ
        // =====================================================
        JPanel rightPanel = new JPanel() {
            private BufferedImage image;
            {
                image = loadImage();
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int panelWidth = getWidth();
                int panelHeight = getHeight();

                if (image == null) {
                    g2d.setColor(new Color(235, 235, 235));
                    g2d.fillRect(0, 0, panelWidth, panelHeight);
                    g2d.setColor(Color.RED);
                    g2d.setFont(new Font("Tahoma", Font.BOLD, 18));
                    String error = "ไม่พบรูป comsci.jpg";
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(error);
                    g2d.drawString(error, (panelWidth - textWidth) / 2, panelHeight / 2);
                    g2d.dispose();
                    return;
                }

                int imageWidth = image.getWidth();
                int imageHeight = image.getHeight();

                double scaleX = (double) panelWidth / imageWidth;
                double scaleY = (double) panelHeight / imageHeight;
                double scale = Math.max(scaleX, scaleY);

                int newWidth = (int) (imageWidth * scale);
                int newHeight = (int) (imageHeight * scale);

                int x = (panelWidth - newWidth) / 2;
                int y = (panelHeight - newHeight) / 2;

                g2d.drawImage(image, x, y, newWidth, newHeight, null);

                g2d.setColor(new Color(0, 0, 0, 80));
                g2d.fillRect(0, 0, panelWidth, panelHeight);

                String text = "COMSCI TOUR";
                g2d.setFont(new Font("Tahoma", Font.BOLD, 30));
                g2d.setColor(Color.WHITE);
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textX = (panelWidth - textWidth) / 2;
                int textY = (panelHeight + fm.getAscent()) / 2;

                g2d.drawString(text, textX, textY);
                g2d.dispose();
            }

            private BufferedImage loadImage() {
                try {
                    java.net.URL url = getClass().getResource("/sc/login/images/comsci.jpg");
                    if (url != null) return ImageIO.read(url);
                } catch (Exception ignored) {}

                String[] paths = {
                        "src/sc/login/images/comsci.jpg",
                        "sc/login/images/comsci.jpg",
                        "src/login/images/comsci.jpg",
                        "login/images/comsci.jpg",
                        "./images/comsci.jpg",
                        "images/comsci.jpg"
                };

                for (String path : paths) {
                    try {
                        File file = new File(path);
                        if (file.exists()) {
                            return ImageIO.read(file);
                        }
                    } catch (IOException ignored) {}
                }
                return null;
            }
        };

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        setContentPane(mainPanel);

        // =====================================================
        // LOGIN EVENT (ล็อกอินสำเร็จ เปิดหน้า SelectSeatGUI ทันที)
        // =====================================================
        loginButton.addActionListener(e -> {
            String email = userField.getText().trim();
            String pass = new String(passField.getPassword()).trim();

            if (user.login(email, pass)) {
                JOptionPane.showMessageDialog(null, "Login สำเร็จ!", "สำเร็จ", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // ปิดหน้า Login

                // เปิดหน้า SelectSeatGUI ทันที
                SwingUtilities.invokeLater(() -> {
                    new SelectSeatGUI().setVisible(true);
                });
            } else {
                JOptionPane.showMessageDialog(null, "อีเมลหรือรหัสผ่านไม่ถูกต้อง", "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
            }
        });

        // SIGN UP EVENT (เปิดหน้าสมัครสมาชิกในแพ็กเกจ sc.login)
        registerLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
                SwingUtilities.invokeLater(() -> {
                    new RegisterFormUI().setVisible(true);
                });
            }
        });
    }

    private void styleTextField(JComponent c, Dimension d) {
        c.setMaximumSize(d);
        c.setPreferredSize(d);
        c.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(210, 215, 220), 1, true),
                        BorderFactory.createEmptyBorder(4, 10, 4, 10)
                )
        );
    }

    private static void setUIFont(javax.swing.plaf.FontUIResource f) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, f);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginUI().setVisible(true);
        });
    }
}