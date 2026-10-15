
package busbooking.ui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;

public class ManageBusTypeFrame extends JFrame {

    private JTable busTypeTable;
    private DefaultTableModel tableModel;

    private JTextField txtBusTypeId;
    private JTextField txtBusTypeName;
    private JTextField txtTotalSeats;

    private static final Font FONT_NORMAL =
            new Font("Tahoma", Font.PLAIN, 15);
    private static final Font FONT_LABEL =
            new Font("Tahoma", Font.PLAIN, 16);
    private static final Font FONT_BUTTON =
            new Font("Tahoma", Font.BOLD, 15);
    private static final Font FONT_TITLE =
            new Font("Tahoma", Font.BOLD, 28);

    private final Path csvPath = findCsvPath();

    public ManageBusTypeFrame() {
        setTitle("จัดการประเภทรถ - Bus Type");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        createUI();
        loadBusTypes();

        setVisible(true);
    }

    private Path findCsvPath() {
        Path path = Paths.get(
                "busbooking", "data", "bustypes.csv"
        );

        if (Files.exists(path)) {
            return path;
        }

        Path alternative = Paths.get(
                "src", "busbooking", "data", "bustypes.csv"
        );

        if (Files.exists(alternative)) {
            return alternative;
        }

        Path alternativeUpper = Paths.get(
                "Src", "busbooking", "data", "bustypes.csv"
        );

        if (Files.exists(alternativeUpper)) {
            return alternativeUpper;
        }

        return path;
    }

    private void createUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        JLabel titleLabel = new JLabel(
                "จัดการประเภทรถ (Bus Type)",
                SwingConstants.CENTER
        );
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(new Color(0, 90, 160));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // ================= LEFT PANEL =================

        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(180, 180, 180)),
                        BorderFactory.createEmptyBorder(10, 12, 12, 12)
                )
        );

        JLabel formTitle = new JLabel("ข้อมูลประเภทรถ");
        formTitle.setFont(
                new Font("Tahoma", Font.BOLD, 16)
        );
        leftPanel.add(formTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtBusTypeId = new JTextField();
        txtBusTypeName = new JTextField();
        txtTotalSeats = new JTextField();

        addFormRow(
                formPanel, gbc, 0,
                "Bus Type ID", txtBusTypeId
        );

        addFormRow(
                formPanel, gbc, 1,
                "ชื่อประเภทรถ", txtBusTypeName
        );

        addFormRow(
                formPanel, gbc, 2,
                "จำนวนที่นั่ง", txtTotalSeats
        );

        leftPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(
                new GridLayout(4, 1, 0, 8)
        );
        buttonPanel.setBackground(Color.WHITE);

        JButton btnAdd = createButton(
                "เพิ่มประเภทรถ", new Color(40, 180, 70)
        );

        JButton btnEdit = createButton(
                "แก้ไข", new Color(30, 120, 200)
        );

        JButton btnDelete = createButton(
                "ลบ", new Color(220, 50, 50)
        );

        JButton btnBack = createButton(
                "กลับหน้าหลัก", new Color(100, 100, 100)
        );

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnBack);

        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        // ================= RIGHT PANEL =================

        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(180, 180, 180)),
                        BorderFactory.createEmptyBorder(10, 5, 5, 5)
                )
        );

        JLabel tableTitle = new JLabel("รายการประเภทรถ");
        tableTitle.setFont(
                new Font("Tahoma", Font.BOLD, 16)
        );
        rightPanel.add(tableTitle, BorderLayout.NORTH);

        String[] columns = {
                "Bus Type ID",
                "ชื่อประเภทรถ",
                "จำนวนที่นั่ง"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        busTypeTable = new JTable(tableModel);
        busTypeTable.setFont(FONT_NORMAL);
        busTypeTable.setRowHeight(35);
        busTypeTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
        busTypeTable.setAutoCreateRowSorter(true);
        busTypeTable.getTableHeader().setFont(
                new Font("Tahoma", Font.BOLD, 15)
        );
        busTypeTable.getTableHeader().setPreferredSize(
                new Dimension(0, 35)
        );

        rightPanel.add(
                new JScrollPane(busTypeTable),
                BorderLayout.CENTER
        );

        JPanel centerPanel = new JPanel(
                new GridLayout(1, 2, 15, 0)
        );
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(leftPanel);
        centerPanel.add(rightPanel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);

        // ================= EVENTS =================

        btnAdd.addActionListener(e -> addBusType());
        btnEdit.addActionListener(e -> editBusType());
        btnDelete.addActionListener(e -> deleteBusType());
        btnBack.addActionListener(e -> dispose());

        busTypeTable.getSelectionModel()
                .addListSelectionListener(e -> {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    int viewRow = busTypeTable.getSelectedRow();

                    if (viewRow >= 0) {
                        int row = busTypeTable
                                .convertRowIndexToModel(viewRow);

                        txtBusTypeId.setText(
                                tableModel.getValueAt(row, 0).toString()
                        );

                        txtBusTypeName.setText(
                                tableModel.getValueAt(row, 1).toString()
                        );

                        txtTotalSeats.setText(
                                tableModel.getValueAt(row, 2).toString()
                        );
                    }
                });
    }

    private void addFormRow(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String label,
            JTextField field
    ) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_LABEL);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.insets = new Insets(8, 0, 8, 10);
        panel.add(lbl, gbc);

        field.setFont(FONT_NORMAL);
        field.setPreferredSize(new Dimension(250, 40));

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.insets = new Insets(8, 0, 8, 0);
        panel.add(field, gbc);
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(350, 42));
        return button;
    }

    // ================= CSV =================

    private void loadBusTypes() {
        tableModel.setRowCount(0);

        if (!Files.exists(csvPath)) {
            JOptionPane.showMessageDialog(
                    this,
                    "ไม่พบไฟล์ bustypes.csv\n"
                            + csvPath.toAbsolutePath(),
                    "ไม่พบไฟล์",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(
                csvPath, StandardCharsets.UTF_8
        )) {
            String line;
            boolean firstLine = true;
            Set<String> ids = new HashSet<>();

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(",", -1);

                if (data.length != 3) {
                    throw new IOException(
                            "จำนวนคอลัมน์ไม่ถูกต้อง: " + line
                    );
                }

                String id = data[0].trim();
                String name = data[1].trim();
                int seats;

                try {
                    seats = Integer.parseInt(data[2].trim());
                } catch (NumberFormatException ex) {
                    throw new IOException(
                            "จำนวนที่นั่งไม่ถูกต้อง: " + line
                    );
                }

                if (id.isEmpty() || name.isEmpty() || seats <= 0) {
                    throw new IOException(
                            "ข้อมูลไม่ถูกต้อง: " + line
                    );
                }

                if (!ids.add(id.toLowerCase(java.util.Locale.ROOT))) {
                    throw new IOException(
                            "รหัสประเภทรถซ้ำ: " + id
                    );
                }

                tableModel.addRow(
                        new Object[]{id, name, seats}
                );
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "อ่าน CSV ไม่สำเร็จ:\n" + ex.getMessage(),
                    "เกิดข้อผิดพลาด",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private boolean saveBusTypes() {
        try {
            Path parent = csvPath.toAbsolutePath().getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            Path tempFile = Files.createTempFile(
                    parent, "bustypes-", ".tmp"
            );

            try {
                try (BufferedWriter writer = Files.newBufferedWriter(
                        tempFile, StandardCharsets.UTF_8
                )) {
                    writer.write(
                            "bus_type_id,bus_type_name,total_seats"
                    );
                    writer.newLine();

                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        String id = tableModel
                                .getValueAt(i, 0).toString();
                        String name = tableModel
                                .getValueAt(i, 1).toString();
                        String seats = tableModel
                                .getValueAt(i, 2).toString();

                        writer.write(
                                id + "," + name + "," + seats
                        );
                        writer.newLine();
                    }
                }

                Files.move(
                        tempFile,
                        csvPath,
                        StandardCopyOption.REPLACE_EXISTING
                );

            } finally {
                Files.deleteIfExists(tempFile);
            }

            return true;

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "บันทึก CSV ไม่สำเร็จ:\n" + ex.getMessage(),
                    "เกิดข้อผิดพลาด",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    }

    // ================= VALIDATION =================

    private String[] getFormData() {
        String id = txtBusTypeId.getText().trim();
        String name = txtBusTypeName.getText().trim();
        String seatsText = txtTotalSeats.getText().trim();

        if (id.isEmpty() || name.isEmpty() || seatsText.isEmpty()) {
            showWarning("กรุณากรอกข้อมูลให้ครบ");
            return null;
        }

        if (id.contains(",") || name.contains(",")
                || id.contains("\n") || name.contains("\n")
                || id.contains("\r") || name.contains("\r")) {
            showWarning("ข้อมูลต้องไม่มีเครื่องหมายจุลภาคหรือขึ้นบรรทัดใหม่");
            return null;
        }

        try {
            int seats = Integer.parseInt(seatsText);

            if (seats <= 0) {
                showWarning("จำนวนที่นั่งต้องมากกว่า 0");
                return null;
            }

            return new String[]{
                    id, name, String.valueOf(seats)
            };

        } catch (NumberFormatException ex) {
            showWarning("จำนวนที่นั่งต้องเป็นตัวเลขจำนวนเต็ม");
            return null;
        }
    }

    private boolean isDuplicateId(String id, int excludedRow) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (i == excludedRow) {
                continue;
            }

            String existingId = tableModel
                    .getValueAt(i, 0).toString();

            if (existingId.equalsIgnoreCase(id)) {
                return true;
            }
        }

        return false;
    }

    // ================= ADD =================

    private void addBusType() {
        String[] data = getFormData();

        if (data == null) {
            return;
        }

        if (isDuplicateId(data[0], -1)) {
            showWarning("Bus Type ID นี้มีอยู่แล้ว");
            return;
        }

        tableModel.addRow(new Object[]{
                data[0],
                data[1],
                Integer.parseInt(data[2])
        });

        if (saveBusTypes()) {
            showSuccess("เพิ่มประเภทรถเรียบร้อยแล้ว");
            clearForm();
        } else {
            loadBusTypes();
        }
    }

    // ตรวจสอบว่า Bus Type มีเที่ยวรถอ้างอิงอยู่หรือไม่
    private boolean isBusTypeUsedByTrip(String busTypeId) throws IOException {
        Path path = Paths.get("busbooking", "data", "trip_schedules.csv");
        if (!Files.exists(path)) path = Paths.get("src", "busbooking", "data", "trip_schedules.csv");
        if (!Files.exists(path)) path = Paths.get("Src", "busbooking", "data", "trip_schedules.csv");
        if (!Files.exists(path)) throw new IOException("ไม่พบไฟล์ trip_schedules.csv");

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String header = reader.readLine();
            if (header == null || !header.replace("\uFEFF", "").trim().equals(
                    "trip_id,route_id,bus_type_id,travel_date,departure_time,price,available_seats")) {
                throw new IOException("หัวตาราง trip_schedules.csv ไม่ถูกต้อง");
            }
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] fields = line.split(",", -1);
                if (fields.length != 7) throw new IOException("ข้อมูลเที่ยวรถมีคอลัมน์ไม่ครบ");
                if (fields[2].trim().equalsIgnoreCase(busTypeId)) return true;
            }
        }
        return false;
    }

    // ================= EDIT =================

    private void editBusType() {
        int viewRow = busTypeTable.getSelectedRow();

        if (viewRow < 0) {
            showWarning("กรุณาเลือกประเภทรถที่ต้องการแก้ไข");
            return;
        }

        int row = busTypeTable.convertRowIndexToModel(viewRow);
        String[] data = getFormData();

        if (data == null) {
            return;
        }

        if (isDuplicateId(data[0], row)) {
            showWarning("Bus Type ID นี้มีอยู่แล้ว");
            return;
        }

        String oldId = tableModel.getValueAt(row, 0).toString();
        if (!oldId.equals(data[0])) {
            try {
                if (isBusTypeUsedByTrip(oldId)) {
                    showWarning("ไม่สามารถเปลี่ยน Bus Type ID จาก " + oldId + " เป็น " + data[0]
                            + "\nเนื่องจากมีเที่ยวรถใช้งานประเภทรถนี้อยู่");
                    return;
                }
            } catch (IOException ex) {
                showWarning("ไม่สามารถตรวจสอบข้อมูลเที่ยวรถได้\n" + ex.getMessage());
                return;
            }
        }

        tableModel.setValueAt(data[0], row, 0);
        tableModel.setValueAt(data[1], row, 1);
        tableModel.setValueAt(
                Integer.parseInt(data[2]), row, 2
        );

        if (saveBusTypes()) {
            showSuccess("แก้ไขประเภทรถเรียบร้อยแล้ว");
            clearForm();
        } else {
            loadBusTypes();
        }
    }

    // ================= DELETE =================

    private void deleteBusType() {
        int viewRow = busTypeTable.getSelectedRow();

        if (viewRow < 0) {
            showWarning("กรุณาเลือกประเภทรถที่ต้องการลบ");
            return;
        }

        int row = busTypeTable.convertRowIndexToModel(viewRow);
        String id = tableModel.getValueAt(row, 0).toString();

        try {
            if (isBusTypeUsedByTrip(id)) {
                showWarning("ไม่สามารถลบประเภทรถ " + id
                        + "\nเนื่องจากมีเที่ยวรถใช้งานประเภทรถนี้อยู่");
                return;
            }
        } catch (IOException ex) {
            showWarning("ไม่สามารถตรวจสอบข้อมูลเที่ยวรถได้\n" + ex.getMessage());
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "ต้องการลบประเภทรถ " + id + " หรือไม่?",
                "ยืนยันการลบ",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        tableModel.removeRow(row);

        if (saveBusTypes()) {
            showSuccess("ลบประเภทรถเรียบร้อยแล้ว");
            clearForm();
        } else {
            loadBusTypes();
        }
    }

    private void clearForm() {
        txtBusTypeId.setText("");
        txtBusTypeName.setText("");
        txtTotalSeats.setText("");
        busTypeTable.clearSelection();
        txtBusTypeId.requestFocusInWindow();
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "แจ้งเตือน",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "สำเร็จ",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ManageBusTypeFrame::new);
    }
}
