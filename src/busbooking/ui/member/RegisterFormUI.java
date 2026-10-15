package busbooking.ui.member;

import busbooking.model.Gender;
import busbooking.model.User;
import busbooking.ui.ThaiDatePicker;
import busbooking.ui.UiTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;

public class RegisterFormUI extends JFrame {

    private JComboBox<String> genderCombo;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private ThaiDatePicker birthDateField;
    private JTextField mobileField;
    private JPasswordField passField;
    private JPasswordField confirmPassField;

    private JButton submitButton;
    private JButton clearButton;

    public RegisterFormUI() {
        setTitle("สมัครสมาชิก - ระบบจองตั๋วรถบัส");
        setSize(550, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Font thaiFont = new Font("Tahoma", Font.PLAIN, 13);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        add(scrollPane);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel section1 = new JLabel("สมัครสมาชิก: กรุณาระบุข้อมูลส่วนตัวของท่าน");
        section1.setFont(new Font("Tahoma", Font.BOLD, 14));
        section1.setForeground(new Color(60, 60, 60));
        mainPanel.add(section1, gbc);

        gbc.gridy++;
        JSeparator sep1 = new JSeparator();
        sep1.setForeground(new Color(200, 200, 200));
        mainPanel.add(sep1, gbc);

        // ชื่อ + นามสกุล
        gbc.gridy++;
        JPanel row1 = new JPanel(new GridLayout(1, 2, 15, 0));
        row1.setBackground(Color.WHITE);
        firstNameField = createPlaceholderTextField("กรอกชื่อ");
        lastNameField = createPlaceholderTextField("กรอกนามสกุล");
        row1.add(wrapWithLabel("ชื่อ", firstNameField));
        row1.add(wrapWithLabel("นามสกุล", lastNameField));
        mainPanel.add(row1, gbc);

        // วันเกิด + เพศ
        gbc.gridy++;
        JPanel row2 = new JPanel(new GridLayout(1, 2, 15, 0));
        row2.setBackground(Color.WHITE);
        // วันเกิด: เลือกจากปฏิทิน ย้อนหลังได้ 100 ปี และเลือกวันในอนาคตไม่ได้
        birthDateField = new ThaiDatePicker("วว/ดด/ปปปป (พ.ศ.)", LocalDate.now(), 100);
        birthDateField.setPreferredSize(new Dimension(200, 30));
        genderCombo = new JComboBox<>(new String[]{"-ระบุเพศ-", "ชาย", "หญิง"});
        row2.add(wrapWithLabel("วัน/เดือน/ปีเกิด (พ.ศ.)", birthDateField));
        row2.add(wrapWithLabel("เพศ", genderCombo));
        mainPanel.add(row2, gbc);

        // เบอร์มือถือ
        gbc.gridy++;
        mobileField = createPlaceholderTextField("08xxxxxxxx");
        mainPanel.add(wrapWithLabel("เบอร์มือถือ (ใช้สำหรับเข้าสู่ระบบ)", mobileField), gbc);

        // รหัสผ่าน + ยืนยันรหัสผ่าน
        gbc.gridy++;
        JPanel row3 = new JPanel(new GridLayout(1, 2, 15, 0));
        row3.setBackground(Color.WHITE);
        passField = createPlaceholderPasswordField();
        confirmPassField = createPlaceholderPasswordField();
        row3.add(wrapWithLabel("รหัสผ่าน", passField));
        row3.add(wrapWithLabel("ยืนยันรหัสผ่าน", confirmPassField));
        mainPanel.add(row3, gbc);

        // ปุ่มกด
        gbc.gridy++;
        JPanel buttonRow = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonRow.setBackground(Color.WHITE);

        submitButton = new JButton("สมัครสมาชิก");
        submitButton.setBackground(new Color(41, 158, 215));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFont(new Font("Tahoma", Font.BOLD, 13));
        submitButton.setFocusPainted(false);

        clearButton = new JButton("เคลียร์ข้อมูล");
        clearButton.setBackground(new Color(156, 163, 175));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFont(new Font("Tahoma", Font.BOLD, 13));
        clearButton.setFocusPainted(false);

        buttonRow.add(submitButton);
        buttonRow.add(clearButton);
        mainPanel.add(buttonRow, gbc);

        // กลับหน้า Login
        gbc.gridy++;
        JLabel backLabel = new JLabel("<html><a href='' style='color: #d9534f; text-decoration: none;'>← กลับหน้าเข้าสู่ระบบ</a></html>");
        backLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        mainPanel.add(backLabel, gbc);

        backLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
                SwingUtilities.invokeLater(() -> {
                    new LoginUI().setVisible(true);
                });
            }
        });

        clearButton.addActionListener(e -> {
            firstNameField.setText("");
            lastNameField.setText("");
            birthDateField.clear();
            mobileField.setText("");
            passField.setText("");
            confirmPassField.setText("");
            genderCombo.setSelectedIndex(0);
        });

        submitButton.addActionListener(e -> {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String name = firstName + " " + lastName;
            String phone = mobileField.getText().trim();
            String pass = new String(passField.getPassword()).trim();
            String confirmPass = new String(confirmPassField.getPassword()).trim();

            LocalDate birthDate = birthDateField.getDate();
            Gender gender = Gender.fromLabel((String) genderCombo.getSelectedItem());

            if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "กรุณากรอกข้อมูลให้ครบถ้วน", "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (birthDate == null) {
                JOptionPane.showMessageDialog(this, "กรุณาเลือกวันเกิด", "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (gender == null) {
                JOptionPane.showMessageDialog(this, "กรุณาระบุเพศ", "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!pass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(this, "รหัสผ่านและยืนยันรหัสผ่านไม่ตรงกัน", "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // เบอร์มือถือใช้เป็นชื่อผู้ใช้ตอน login จึงต้องเป็นตัวเลข 10 หลักเท่านั้น
            if (!phone.matches("\\d{10}")) {
                JOptionPane.showMessageDialog(this, "เบอร์มือถือต้องเป็นตัวเลข 10 หลัก", "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // users.csv แยกคอลัมน์ด้วยคอมมา ถ้ามีคอมมาในข้อมูล คอลัมน์จะเลื่อนและ login ไม่ได้
            if (name.contains(",") || pass.contains(",")) {
                JOptionPane.showMessageDialog(this, "ชื่อและรหัสผ่านห้ามมีเครื่องหมายคอมมา (,)", "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // ถ้าเบอร์ซ้ำ login จะเจอแค่บัญชีแรก บัญชีใหม่จะใช้ไม่ได้
            if (User.phoneExists(phone)) {
                JOptionPane.showMessageDialog(this, "เบอร์มือถือนี้สมัครสมาชิกไว้แล้ว", "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                if (!User.register(name, phone, pass, birthDate, gender)) {
                    // มีคนสมัครเบอร์นี้ตัดหน้าไปพอดี (จากอีกหน้าต่างหนึ่ง)
                    JOptionPane.showMessageDialog(this, "เบอร์มือถือนี้สมัครสมาชิกไว้แล้ว", "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JOptionPane.showMessageDialog(this, "สมัครสมาชิกสำเร็จ!", "สำเร็จ", JOptionPane.INFORMATION_MESSAGE);

                dispose();
                new LoginUI().setVisible(true);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "เกิดข้อผิดพลาดในการบันทึก: " + ex.getMessage(), "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
            }
        });

        // หน้านี้ใช้ตัวอักษรขนาด 13 (เฉพาะหน้านี้ ไม่ไปแก้ค่ากลางของโปรแกรม)
        UiTheme.useFontForDefaults(getContentPane(), thaiFont);
    }

    private JPanel wrapWithLabel(String labelText, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 3));
        p.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Tahoma", Font.BOLD, 11));
        lbl.setForeground(new Color(90, 90, 90));
        p.add(lbl, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private JTextField createPlaceholderTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setToolTipText(placeholder);
        tf.setPreferredSize(new Dimension(200, 30));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 210), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return tf;
    }

    private JPasswordField createPlaceholderPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setPreferredSize(new Dimension(200, 30));
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 210), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return pf;
    }

}