package login;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class RegisterFormUI extends JFrame {

    // =========================
    // ComboBox
    // =========================
    private JComboBox<String> memberStatusCombo;
    private JComboBox<String> genderCombo;
    private JComboBox<String> nationalityCombo;
    private JComboBox<String> occupationCombo;

    private JComboBox<String> provinceCombo;
    private JComboBox<String> amphurCombo;
    private JComboBox<String> tambonCombo;

    // =========================
    // TextField
    // =========================
    private JTextField idNumberField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField birthDateField;
    private JTextField nationalityDetailField;
    private JTextField phoneField;
    private JTextField mobileField;
    private JTextField occupationDetailField;
    private JTextField addressField;
    private JTextField postalField;
    private JTextField emailField;
    private JTextField confirmEmailField;

    private JPasswordField passField;
    private JPasswordField confirmPassField;

    private JCheckBox newsletterCheckBox;

    private JButton submitButton;
    private JButton clearButton;

    // ==========================================================
    // เก็บข้อมูล (จังหวัด -> อำเภอ -> ตำบล)
    // ==========================================================
    private Map<String, Map<String, Map<String, String>>> addressData
            = new LinkedHashMap<>();

    public RegisterFormUI() {

        setTitle("สมัครสมาชิก - ระบบจองตั๋วรถบัส");
        setSize(750, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Font thaiFont = new Font("Tahoma", Font.PLAIN, 13);
        setUIFont(new javax.swing.plaf.FontUIResource(thaiFont));

        // ==========================================================
        // Main Panel
        // ==========================================================

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);

        add(scrollPane);

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // ==========================================================
        // ส่วนที่ 1 : ข้อมูลส่วนตัว
        // ==========================================================

        JLabel section1 = new JLabel(
                "สมัครสมาชิก: กรุณาระบุข้อมูลส่วนตัวของท่านเพื่อใช้ในการสมัคร"
        );

        section1.setFont(new Font("Tahoma", Font.BOLD, 13));
        section1.setForeground(new Color(60, 60, 60));

        mainPanel.add(section1, gbc);

        gbc.gridy++;

        JSeparator sep1 = new JSeparator();
        sep1.setForeground(new Color(200, 200, 200));

        mainPanel.add(sep1, gbc);

        // ==========================================================
        // เลขบัตรประชาชน
        // ==========================================================

        gbc.gridy++;

        idNumberField =
                createPlaceholderTextField("เลขบัตรประจำตัวประชาชน 13 หลัก");

        mainPanel.add(
                wrapWithLabel(
                        "เลขบัตรประจำตัวประชาชน",
                        idNumberField
                ),
                gbc
        );

        // ==========================================================
        // ชื่อ + นามสกุล
        // ==========================================================

        gbc.gridy++;

        JPanel row2 = new JPanel(
                new GridLayout(1, 2, 15, 0)
        );

        row2.setBackground(Color.WHITE);

        firstNameField =
                createPlaceholderTextField("กรอกชื่อ");

        lastNameField =
                createPlaceholderTextField("กรอกนามสกุล");

        row2.add(
                wrapWithLabel("ชื่อ", firstNameField)
        );

        row2.add(
                wrapWithLabel("นามสกุล", lastNameField)
        );

        mainPanel.add(row2, gbc);

        // ==========================================================
        // วันเกิด + สถานะสมาชิก
        // ==========================================================

        gbc.gridy++;

        JPanel row3 = new JPanel(
                new GridLayout(1, 2, 15, 0)
        );

        row3.setBackground(Color.WHITE);

        birthDateField =
                createPlaceholderTextField("วว/ดด/ปปปป (พ.ศ.)");

        memberStatusCombo =
                new JComboBox<>(
                        new String[]{
                                "-เคยเป็นสมาชิก-",
                                "เคยเป็นสมาชิก",
                                "ไม่เคยเป็นสมาชิก"
                        }
                );

        row3.add(
                wrapWithLabel(
                        "วัน/เดือน/ปีเกิด (พ.ศ.)",
                        birthDateField
                )
        );

        row3.add(
                wrapWithLabel(
                        "สถานะสมาชิก",
                        memberStatusCombo
                )
        );

        mainPanel.add(row3, gbc);

        // ==========================================================
        // เพศ
        // ==========================================================

        gbc.gridy++;

        JPanel row4 = new JPanel(
                new GridLayout(1, 2, 15, 0)
        );

        row4.setBackground(Color.WHITE);

        genderCombo =
                new JComboBox<>(
                        new String[]{
                                "-ระบุเพศ-",
                                "ชาย",
                                "หญิง"
                        }
                );

        row4.add(
                wrapWithLabel(
                        "เพศ",
                        genderCombo
                )
        );

        row4.add(new JLabel(""));

        mainPanel.add(row4, gbc);

        // ==========================================================
        // สัญชาติ
        // ==========================================================

        gbc.gridy++;

        JPanel row5 = new JPanel(
                new GridLayout(1, 2, 15, 0)
        );

        row5.setBackground(Color.WHITE);

        nationalityCombo =
                new JComboBox<>(
                        new String[]{
                                "-สัญชาติ-",
                                "ไทย",
                                "อื่นๆ"
                        }
                );

        nationalityDetailField =
                createPlaceholderTextField("ระบุสัญชาติ");

        row5.add(
                wrapWithLabel(
                        "สัญชาติ",
                        nationalityCombo
                )
        );

        row5.add(
                wrapWithLabel(
                        "ระบุสัญชาติ",
                        nationalityDetailField
                )
        );

        mainPanel.add(row5, gbc);

        // ==========================================================
        // เบอร์โทร
        // ==========================================================

        gbc.gridy++;

        JPanel row6 = new JPanel(
                new GridLayout(1, 2, 15, 0)
        );

        row6.setBackground(Color.WHITE);

        mobileField =
                createPlaceholderTextField("08xxxxxxxx");

        phoneField =
                createPlaceholderTextField(
                        "เบอร์โทรศัพท์บ้าน (ถ้ามี)"
                );

        row6.add(
                wrapWithLabel(
                        "เบอร์มือถือ",
                        mobileField
                )
        );

        row6.add(
                wrapWithLabel(
                        "เบอร์โทรศัพท์",
                        phoneField
                )
        );

        mainPanel.add(row6, gbc);

        // ==========================================================
        // อาชีพ
        // ==========================================================

        gbc.gridy++;

        JPanel row7 = new JPanel(
                new GridLayout(1, 2, 15, 0)
        );

        row7.setBackground(Color.WHITE);

        occupationCombo =
                new JComboBox<>(
                        new String[]{
                                "-อาชีพ-",
                                "พนักงานบริษัท",
                                "นักเรียน/นักศึกษา",
                                "ธุรกิจส่วนตัว",
                                "อื่นๆ"
                        }
                );

        occupationDetailField =
                createPlaceholderTextField("ระบุอาชีพ");

        row7.add(
                wrapWithLabel(
                        "อาชีพ",
                        occupationCombo
                )
        );

        row7.add(
                wrapWithLabel(
                        "ระบุอาชีพ",
                        occupationDetailField
                )
        );

        mainPanel.add(row7, gbc);

        // ==========================================================
        // ที่อยู่
        // ==========================================================

        gbc.gridy++;

        addressField =
                createPlaceholderTextField(
                        "บ้านเลขที่, ซอย, ถนน"
                );

        mainPanel.add(
                wrapWithLabel(
                        "ที่อยู่",
                        addressField
                ),
                gbc
        );

        // ==========================================================
        // จังหวัด + อำเภอ
        // ==========================================================

        gbc.gridy++;

        JPanel row9 = new JPanel(
                new GridLayout(1, 2, 15, 0)
        );

        row9.setBackground(Color.WHITE);

        provinceCombo = new JComboBox<>();
        amphurCombo = new JComboBox<>();

        provinceCombo.addItem("-จังหวัด-");
        amphurCombo.addItem("-อำเภอ/เขต-");

        row9.add(
                wrapWithLabel(
                        "จังหวัด",
                        provinceCombo
                )
        );

        row9.add(
                wrapWithLabel(
                        "อำเภอ/เขต",
                        amphurCombo
                )
        );

        mainPanel.add(row9, gbc);

        // ==========================================================
        // ตำบล + รหัสไปรษณีย์
        // ==========================================================

        gbc.gridy++;

        JPanel row10 = new JPanel(
                new GridLayout(1, 2, 15, 0)
        );

        row10.setBackground(Color.WHITE);

        tambonCombo = new JComboBox<>();
        tambonCombo.addItem("-ตำบล/แขวง-");

        postalField =
                createPlaceholderTextField(
                        "รหัสไปรษณีย์ 5 หลัก"
                );

        row10.add(
                wrapWithLabel(
                        "ตำบล/แขวง",
                        tambonCombo
                )
        );

        row10.add(
                wrapWithLabel(
                        "รหัสไปรษณีย์",
                        postalField
                )
        );

        mainPanel.add(row10, gbc);

        // ==========================================================
        // โหลดข้อมูลจังหวัด อำเภอ ตำบล
        // ==========================================================

        loadAddressData();
        loadProvinces();

        // ==========================================================
        // Event การเลือกจังหวัด / อำเภอ / ตำบล
        // ==========================================================

        provinceCombo.addActionListener(e -> {
            loadAmphurs();
        });

        amphurCombo.addActionListener(e -> {
            loadTambons();
        });

        tambonCombo.addActionListener(e -> {
            loadPostalCode();
        });

        // ==========================================================
        // ช่องว่าง
        // ==========================================================

        gbc.gridy++;

        mainPanel.add(
                Box.createRigidArea(
                        new Dimension(0, 10)
                ),
                gbc
        );

        // ==========================================================
        // ส่วนที่ 2 : อีเมลและรหัสผ่าน
        // ==========================================================

        gbc.gridy++;

        JLabel section2 = new JLabel(
                "สมัครสมาชิก: กรุณาระบุอีเมลและกำหนดรหัสผ่านเพื่อใช้ในการล็อกอินเข้าสู่ระบบ"
        );

        section2.setFont(
                new Font(
                        "Tahoma",
                        Font.BOLD,
                        13
                )
        );

        section2.setForeground(
                new Color(60, 60, 60)
        );

        mainPanel.add(section2, gbc);

        gbc.gridy++;

        JSeparator sep2 = new JSeparator();

        sep2.setForeground(
                new Color(200, 200, 200)
        );

        mainPanel.add(sep2, gbc);

        // ==========================================================
        // อีเมล + ยืนยันอีเมล
        // ==========================================================

        gbc.gridy++;

        JPanel row11 = new JPanel(
                new GridLayout(1, 2, 15, 0)
        );

        row11.setBackground(Color.WHITE);

        emailField =
                createPlaceholderTextField(
                        "example@email.com"
                );

        confirmEmailField =
                createPlaceholderTextField(
                        "ยืนยัน example@email.com"
                );

        row11.add(
                wrapWithLabel(
                        "อีเมล",
                        emailField
                )
        );

        row11.add(
                wrapWithLabel(
                        "ยืนยันอีเมล",
                        confirmEmailField
                )
        );

        mainPanel.add(row11, gbc);

        // ==========================================================
        // รหัสผ่าน
        // ==========================================================

        gbc.gridy++;

        JPanel row12 = new JPanel(
                new GridLayout(1, 2, 15, 0)
        );

        row12.setBackground(Color.WHITE);

        passField =
                createPlaceholderPasswordField();

        confirmPassField =
                createPlaceholderPasswordField();

        row12.add(
                wrapWithLabel(
                        "รหัสผ่าน",
                        passField
                )
        );

        row12.add(
                wrapWithLabel(
                        "ยืนยันรหัสผ่าน",
                        confirmPassField
                )
        );

        mainPanel.add(row12, gbc);

        // ==========================================================
        // ข่าวสาร
        // ==========================================================

        gbc.gridy++;

        newsletterCheckBox =
                new JCheckBox(
                        " ติดตามข่าวสารและสิทธิประโยชน์ผ่านอีเมล"
                );

        newsletterCheckBox.setBackground(Color.WHITE);

        mainPanel.add(
                newsletterCheckBox,
                gbc
        );

        // ==========================================================
        // ปุ่ม
        // ==========================================================

        gbc.gridy++;

        JPanel buttonRow = new JPanel(
                new GridLayout(1, 2, 15, 0)
        );

        buttonRow.setBackground(Color.WHITE);

        submitButton =
                new JButton("สมัครสมาชิก");

        submitButton.setBackground(
                new Color(41, 158, 215)
        );

        submitButton.setForeground(Color.WHITE);

        submitButton.setFont(
                new Font(
                        "Tahoma",
                        Font.BOLD,
                        13
                )
        );

        submitButton.setFocusPainted(false);

        clearButton =
                new JButton("เคลียร์ข้อมูล");

        clearButton.setBackground(
                new Color(156, 163, 175)
        );

        clearButton.setForeground(Color.WHITE);

        clearButton.setFont(
                new Font(
                        "Tahoma",
                        Font.BOLD,
                        13
                )
        );

        clearButton.setFocusPainted(false);

        buttonRow.add(submitButton);
        buttonRow.add(clearButton);

        mainPanel.add(buttonRow, gbc);

        // ==========================================================
        // กลับหน้า Login
        // ==========================================================

        gbc.gridy++;

        JLabel backLabel =
                new JLabel(
                        "<html><a href='' style='color: #d9534f; text-decoration: none;'>← กลับหน้าเข้าสู่ระบบ</a></html>"
                );

        backLabel.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        mainPanel.add(
                backLabel,
                gbc
        );

        backLabel.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent evt
                    ) {

                        dispose();

                        SwingUtilities.invokeLater(() -> {
                            new login.LoginUI().setVisible(true);
                        });
                    }
                }
        );

        // ==========================================================
        // ปุ่มเคลียร์ข้อมูล
        // ==========================================================

        clearButton.addActionListener(e -> {

            idNumberField.setText("");
            firstNameField.setText("");
            lastNameField.setText("");
            birthDateField.setText("");
            nationalityDetailField.setText("");
            mobileField.setText("");
            phoneField.setText("");
            occupationDetailField.setText("");
            addressField.setText("");
            postalField.setText("");
            emailField.setText("");
            confirmEmailField.setText("");

            passField.setText("");
            confirmPassField.setText("");

            provinceCombo.setSelectedIndex(0);

            amphurCombo.removeAllItems();
            amphurCombo.addItem("-อำเภอ/เขต-");

            tambonCombo.removeAllItems();
            tambonCombo.addItem("-ตำบล/แขวง-");

        });

        // ==========================================================
        // ปุ่มสมัครสมาชิก
        // ==========================================================

        submitButton.addActionListener(e -> {

            String idCard =
                    idNumberField.getText().trim();

            String firstName =
                    firstNameField.getText().trim();

            String lastName =
                    lastNameField.getText().trim();

            String name =
                    firstName + " " + lastName;

            String email =
                    emailField.getText().trim();

            String confirmEmail =
                    confirmEmailField.getText().trim();

            String pass =
                    new String(
                            passField.getPassword()
                    ).trim();

            String confirmPass =
                    new String(
                            confirmPassField.getPassword()
                    ).trim();

            String phone =
                    mobileField.getText().trim();

            String address =
                    addressField.getText().trim();

            String province =
                    String.valueOf(
                            provinceCombo.getSelectedItem()
                    );

            String amphur =
                    String.valueOf(
                            amphurCombo.getSelectedItem()
                    );

            String tambon =
                    String.valueOf(
                            tambonCombo.getSelectedItem()
                    );

            String postal =
                    postalField.getText().trim();

            if (idCard.isEmpty()
                    || firstName.isEmpty()
                    || lastName.isEmpty()
                    || email.isEmpty()
                    || pass.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "กรุณากรอกข้อมูลที่สำคัญให้ครบถ้วน",
                        "ข้อผิดพลาด",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            if (!email.equals(confirmEmail)) {

                JOptionPane.showMessageDialog(
                        this,
                        "อีเมลและยืนยันอีเมลไม่ตรงกัน",
                        "ข้อผิดพลาด",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            if (!pass.equals(confirmPass)) {

                JOptionPane.showMessageDialog(
                        this,
                        "รหัสผ่านและยืนยันรหัสผ่านไม่ตรงกัน",
                        "ข้อผิดพลาด",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            String userData =
                    idCard + "|"
                    + name + "|"
                    + email + "|"
                    + pass + "|"
                    + phone + "|"
                    + address + "|"
                    + province + "|"
                    + amphur + "|"
                    + tambon + "|"
                    + postal;

            try (
                    FileWriter fw =
                            new FileWriter(
                                    "users_database.txt",
                                    true
                            );

                    BufferedWriter bw =
                            new BufferedWriter(fw);

                    PrintWriter out =
                            new PrintWriter(bw)
            ) {

                out.println(userData);

                JOptionPane.showMessageDialog(
                        this,
                        "สมัครสมาชิกและบันทึกข้อมูลสำเร็จ!",
                        "สำเร็จ",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } catch (IOException ex) {

                JOptionPane.showMessageDialog(
                        this,
                        "เกิดข้อผิดพลาดในการบันทึก: "
                                + ex.getMessage(),
                        "ข้อผิดพลาด",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        });

    }

    // ==========================================================
    // อ่านข้อมูลจาก address.txt (แบบ จังหวัด|อำเภอ|ตำบล)
    // ==========================================================

    private void loadAddressData() {

        try (
                BufferedReader br =
                        new BufferedReader(
                                new FileReader(
                                        "address.txt"
                                )
                        )
        ) {

            String line;

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split("\\|");

                if (data.length != 3) {
                    continue;
                }

                String province = data[0].trim();
                String amphur = data[1].trim();
                String tambon = data[2].trim();

                addressData.putIfAbsent(
                        province,
                        new LinkedHashMap<>()
                );

                addressData
                        .get(province)
                        .putIfAbsent(
                                amphur,
                                new LinkedHashMap<>()
                        );

                addressData
                        .get(province)
                        .get(amphur)
                        .put(
                                tambon,
                                ""
                        );
            }

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "ไม่พบไฟล์ address.txt\n\n"
                            + "กรุณาสร้างไฟล์ address.txt "
                            + "ในรูปแบบ:\n\n"
                            + "จังหวัด|อำเภอ|ตำบล",
                    "ไม่พบไฟล์ข้อมูล",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ==========================================================
    // โหลดจังหวัด
    // ==========================================================

    private void loadProvinces() {

        provinceCombo.removeAllItems();
        provinceCombo.addItem("-จังหวัด-");

        for (String province : addressData.keySet()) {
            provinceCombo.addItem(province);
        }
    }

    // ==========================================================
    // โหลดอำเภอ
    // ==========================================================

    private void loadAmphurs() {

        amphurCombo.removeAllItems();
        tambonCombo.removeAllItems();

        amphurCombo.addItem("-อำเภอ/เขต-");
        tambonCombo.addItem("-ตำบล/แขวง-");

        String province =
                (String) provinceCombo.getSelectedItem();

        if (province == null || province.equals("-จังหวัด-")) {
            return;
        }

        Map<String, Map<String, String>> amphurs =
                addressData.get(province);

        if (amphurs == null) {
            return;
        }

        for (String amphur : amphurs.keySet()) {
            amphurCombo.addItem(amphur);
        }
    }

    // ==========================================================
    // โหลดตำบล
    // ==========================================================

    private void loadTambons() {

        tambonCombo.removeAllItems();
        tambonCombo.addItem("-ตำบล/แขวง-");

        String province =
                (String) provinceCombo.getSelectedItem();

        String amphur =
                (String) amphurCombo.getSelectedItem();

        if (province == null
                || amphur == null
                || province.equals("-จังหวัด-")
                || amphur.equals("-อำเภอ/เขต-")) {

            return;
        }

        Map<String, String> tambons =
                addressData
                        .get(province)
                        .get(amphur);

        if (tambons == null) {
            return;
        }

        for (String tambon : tambons.keySet()) {
            tambonCombo.addItem(tambon);
        }
    }

    // ==========================================================
    // โหลดรหัสไปรษณีย์
    // ==========================================================

    private void loadPostalCode() {
        // ให้ผู้ใช้งานพิมพ์กรอกรหัสไปรษณีย์เอง
    }

    // ==========================================================
    // Label + Component
    // ==========================================================

    private JPanel wrapWithLabel(
            String labelText,
            JComponent comp
    ) {

        JPanel p =
                new JPanel(
                        new BorderLayout(0, 3)
                );

        p.setBackground(Color.WHITE);

        JLabel lbl =
                new JLabel(labelText);

        lbl.setFont(
                new Font(
                        "Tahoma",
                        Font.BOLD,
                        11
                )
        );

        lbl.setForeground(
                new Color(90, 90, 90)
        );

        p.add(
                lbl,
                BorderLayout.NORTH
        );

        p.add(
                comp,
                BorderLayout.CENTER
        );

        return p;
    }

    // ==========================================================
    // TextField
    // ==========================================================

    private JTextField createPlaceholderTextField(
            String placeholder
    ) {

        JTextField tf =
                new JTextField();

        tf.setToolTipText(placeholder);

        tf.setPreferredSize(
                new Dimension(
                        200,
                        30
                )
        );

        tf.setBorder(
                BorderFactory.createCompoundBorder(

                        BorderFactory.createLineBorder(
                                new Color(
                                        200,
                                        205,
                                        210
                                ),
                                1
                        ),

                        BorderFactory.createEmptyBorder(
                                4,
                                8,
                                4,
                                8
                        )
                )
        );

        return tf;
    }

    // ==========================================================
    // PasswordField
    // ==========================================================

    private JPasswordField createPlaceholderPasswordField() {

        JPasswordField pf =
                new JPasswordField();

        pf.setPreferredSize(
                new Dimension(
                        200,
                        30
                )
        );

        pf.setBorder(
                BorderFactory.createCompoundBorder(

                        BorderFactory.createLineBorder(
                                new Color(
                                        200,
                                        205,
                                        210
                                ),
                                1
                        ),

                        BorderFactory.createEmptyBorder(
                                4,
                                8,
                                4,
                                8
                        )
                )
        );

        return pf;
    }

    // ==========================================================
    // Font
    // ==========================================================

    private static void setUIFont(
            javax.swing.plaf.FontUIResource f
    ) {

        java.util.Enumeration<Object> keys =
                UIManager.getDefaults().keys();

        while (keys.hasMoreElements()) {

            Object key =
                    keys.nextElement();

            Object value =
                    UIManager.get(key);

            if (value instanceof
                    javax.swing.plaf.FontUIResource) {

                UIManager.put(
                        key,
                        f
                );
            }
        }
    }

    // ==========================================================
    // Main
    // ==========================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new RegisterFormUI().setVisible(true);
        });
    }
}