package busbooking.ui.admin;

import busbooking.data.BookingService;
import busbooking.data.DataException;
import busbooking.data.DataFiles;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ManageBusTypeFrame extends JFrame {

    private JTable busTypeTable;
    private DefaultTableModel tableModel;

    private JTextField txtBusTypeId;
    private JTextField txtBusTypeName;
    private JTextField txtTotalSeats;


    private final Path csvPath = DataFiles.BUS_TYPES;
    private boolean dataValid = true;

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

    private void createUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.add(AdminUi.title("จัดการประเภทรถ (Bus Type)"), BorderLayout.NORTH);

        // ================= LEFT PANEL =================

        txtBusTypeId = new JTextField();
        txtBusTypeName = new JTextField();
        txtTotalSeats = new JTextField();

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        AdminUi.addFormRow(formPanel, 0, "Bus Type ID", txtBusTypeId);
        AdminUi.addFormRow(formPanel, 1, "ชื่อประเภทรถ", txtBusTypeName);
        AdminUi.addFormRow(formPanel, 2, "จำนวนที่นั่ง", txtTotalSeats);

        JButton btnAdd = AdminUi.button("เพิ่มประเภทรถ", AdminUi.GREEN);
        JButton btnEdit = AdminUi.button("แก้ไข", AdminUi.BLUE);
        JButton btnDelete = AdminUi.button("ลบ", AdminUi.RED);
        JButton btnBack = AdminUi.button("กลับหน้าหลัก", AdminUi.GRAY);

        JPanel leftPanel = AdminUi.box();
        leftPanel.add(AdminUi.heading("ข้อมูลประเภทรถ"), BorderLayout.NORTH);
        leftPanel.add(formPanel, BorderLayout.CENTER);
        leftPanel.add(AdminUi.buttonColumn(btnAdd, btnEdit, btnDelete, btnBack), BorderLayout.SOUTH);

        // ================= RIGHT PANEL =================

        String[] columns = {"Bus Type ID", "ชื่อประเภทรถ", "จำนวนที่นั่ง"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        busTypeTable = AdminUi.table(tableModel);

        JPanel rightPanel = AdminUi.box();
        rightPanel.add(AdminUi.heading("รายการประเภทรถ"), BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(busTypeTable), BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 15, 0));
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

        busTypeTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || busTypeTable.getSelectedRow() < 0) {
                return;
            }
            int row = busTypeTable.convertRowIndexToModel(busTypeTable.getSelectedRow());
            txtBusTypeId.setText(tableModel.getValueAt(row, 0).toString());
            txtBusTypeName.setText(tableModel.getValueAt(row, 1).toString());
            txtTotalSeats.setText(tableModel.getValueAt(row, 2).toString());
        });
    }

    // ================= CSV =================

    private void loadBusTypes() {
        tableModel.setRowCount(0);
        dataValid = true;

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
            // แถวแรกต้องเป็นหัวตาราง (ตัด BOM ที่ Excel อาจเติมไว้)
            String header = reader.readLine();
            if (header == null || !DataFiles.stripBom(header).trim()
                    .equalsIgnoreCase(DataFiles.BUS_TYPES_HEADER)) {
                throw new IOException("หัวคอลัมน์ต้องเป็น " + DataFiles.BUS_TYPES_HEADER);
            }

            String line;
            Set<String> ids = new HashSet<>();

            while ((line = reader.readLine()) != null) {
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

                if (!ids.add(id.toLowerCase(Locale.ROOT))) {
                    throw new IOException(
                            "รหัสประเภทรถซ้ำ: " + id
                    );
                }

                tableModel.addRow(
                        new Object[]{id, name, seats}
                );
            }

        } catch (IOException | RuntimeException ex) {
            // ตารางไม่ครบ ห้ามบันทึกทับ ไม่งั้นประเภทรถที่ยังไม่ได้อ่านจะหายจากไฟล์
            tableModel.setRowCount(0);
            dataValid = false;
            JOptionPane.showMessageDialog(
                    this,
                    "อ่าน CSV ไม่สำเร็จ:\n" + ex.getMessage()
                            + "\nกรุณาแก้ไฟล์แล้วเปิดหน้านี้ใหม่ (ระหว่างนี้จะแก้ไขข้อมูลไม่ได้)",
                    "เกิดข้อผิดพลาด",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private boolean saveBusTypes() {
        if (!dataValid) {
            showWarning("ข้อมูลในไฟล์ bustypes.csv ไม่ถูกต้อง กรุณาแก้ไฟล์แล้วเปิดหน้านี้ใหม่");
            return false;
        }
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            lines.add(tableModel.getValueAt(i, 0) + ","
                    + tableModel.getValueAt(i, 1) + ","
                    + tableModel.getValueAt(i, 2));
        }
        try {
            DataFiles.writeCsv(csvPath, DataFiles.BUS_TYPES_HEADER, lines);
            // จำนวนที่นั่งของรถเปลี่ยน -> ที่นั่งว่างของเที่ยวรถที่ใช้รถประเภทนี้ต้องคำนวณใหม่
            try {
                new BookingService().refreshAvailableSeats();
            } catch (DataException ex) {
                System.out.println("อัปเดตที่นั่งว่างไม่สำเร็จ: " + ex.getMessage());
            }
            return true;
        } catch (IOException | DataException ex) {
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
        if (!dataValid) {
            showWarning("ข้อมูลในไฟล์ bustypes.csv ไม่ถูกต้อง กรุณาแก้ไฟล์แล้วเปิดหน้านี้ใหม่");
            return;
        }
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
        return DataFiles.isUsedByTrip(DataFiles.TRIP_BUS_TYPE_ID, busTypeId);
    }

    // ================= EDIT =================

    private void editBusType() {
        if (!dataValid) {
            showWarning("ข้อมูลในไฟล์ bustypes.csv ไม่ถูกต้อง กรุณาแก้ไฟล์แล้วเปิดหน้านี้ใหม่");
            return;
        }
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

        // ลดจำนวนที่นั่ง: ห้ามลดจนที่นั่งที่มีคนจองไว้แล้วหายไปจากผัง
        int oldSeats = Integer.parseInt(tableModel.getValueAt(row, 2).toString());
        int newSeats = Integer.parseInt(data[2]);
        if (newSeats < oldSeats) {
            try {
                java.util.List<String> conflicts = new BookingService().bookedSeatsBeyond(oldId, newSeats);
                if (!conflicts.isEmpty()) {
                    showWarning("ไม่สามารถลดจำนวนที่นั่งเหลือ " + newSeats + " ที่ได้\n"
                            + "เพราะมีการจองที่นั่งที่จะหายไปจากผัง:\n" + String.join("\n", firstFew(conflicts)));
                    return;
                }
            } catch (DataException ex) {
                showWarning("ไม่สามารถตรวจสอบข้อมูลการจองได้\n" + ex.getMessage());
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

    /** แสดงแค่ 10 รายการแรก (ถ้ามีมากกว่านั้นบอกจำนวนที่เหลือ) กันกล่องข้อความยาวล้นจอ */
    private static java.util.List<String> firstFew(java.util.List<String> items) {
        if (items.size() <= 10) {
            return items;
        }
        java.util.List<String> shown = new ArrayList<>(items.subList(0, 10));
        shown.add("... และอีก " + (items.size() - 10) + " รายการ");
        return shown;
    }

    // ================= DELETE =================

    private void deleteBusType() {
        if (!dataValid) {
            showWarning("ข้อมูลในไฟล์ bustypes.csv ไม่ถูกต้อง กรุณาแก้ไฟล์แล้วเปิดหน้านี้ใหม่");
            return;
        }
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
        AdminUi.warn(this, message);
    }

    private void showSuccess(String message) {
        AdminUi.success(this, message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ManageBusTypeFrame::new);
    }
}
