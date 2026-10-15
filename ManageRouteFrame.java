package busbooking.ui.admin;

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
import java.util.List;

public class ManageRouteFrame extends JFrame {

    private JTable routeTable;
    private DefaultTableModel tableModel;

    private JTextField txtRouteId;
    private JTextField txtOrigin;
    private JTextField txtDestination;
    private JTextField txtStop;


    private static final String HEADER = DataFiles.ROUTES_HEADER;
    private final Path csvPath = DataFiles.ROUTES;
    // อ่านไฟล์ไม่สำเร็จ = ตารางไม่ครบ ห้ามบันทึกทับ (ไม่งั้นข้อมูลเส้นทางเดิมหายหมด)
    private boolean dataValid = true;

    public ManageRouteFrame() {
        setTitle("จัดการเส้นทาง - Route");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        createUI();
        loadRoutes();

        setVisible(true);
    }

    private void createUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.add(AdminUi.title("จัดการเส้นทาง (Route)"), BorderLayout.NORTH);

        // ================= LEFT PANEL: ฟอร์ม + ปุ่ม =================

        txtRouteId = new JTextField();
        txtOrigin = new JTextField();
        txtDestination = new JTextField();
        txtStop = new JTextField();

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        AdminUi.addFormRow(formPanel, 0, "Route ID", txtRouteId);
        AdminUi.addFormRow(formPanel, 1, "ต้นทาง", txtOrigin);
        AdminUi.addFormRow(formPanel, 2, "ปลายทาง", txtDestination);
        AdminUi.addFormRow(formPanel, 3, "จุดแวะ", txtStop);

        JButton btnAdd = AdminUi.button("เพิ่มเส้นทาง", AdminUi.GREEN);
        JButton btnEdit = AdminUi.button("แก้ไข", AdminUi.BLUE);
        JButton btnDelete = AdminUi.button("ลบ", AdminUi.RED);
        JButton btnBack = AdminUi.button("กลับหน้าหลัก", AdminUi.GRAY);

        JPanel leftPanel = AdminUi.box();
        leftPanel.add(AdminUi.heading("ข้อมูลเส้นทาง"), BorderLayout.NORTH);
        leftPanel.add(formPanel, BorderLayout.CENTER);
        leftPanel.add(AdminUi.buttonColumn(btnAdd, btnEdit, btnDelete, btnBack), BorderLayout.SOUTH);

        // ================= RIGHT PANEL: ตาราง =================

        String[] columns = {"Route ID", "ต้นทาง", "ปลายทาง", "จุดแวะ"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        routeTable = AdminUi.table(tableModel, 90, 130, 130, 150);

        JPanel rightPanel = AdminUi.box();
        rightPanel.add(AdminUi.heading("รายการเส้นทาง"), BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(routeTable), BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(leftPanel);
        centerPanel.add(rightPanel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);

        // ================= EVENTS =================

        btnAdd.addActionListener(e -> addRoute());
        btnEdit.addActionListener(e -> editRoute());
        btnDelete.addActionListener(e -> deleteRoute());
        btnBack.addActionListener(e -> dispose());

        // คลิกแถวในตาราง -> เอาข้อมูลมาใส่ในฟอร์ม
        routeTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || routeTable.getSelectedRow() < 0) {
                return;
            }
            int row = routeTable.convertRowIndexToModel(routeTable.getSelectedRow());
            txtRouteId.setText(tableModel.getValueAt(row, 0).toString());
            txtOrigin.setText(tableModel.getValueAt(row, 1).toString());
            txtDestination.setText(tableModel.getValueAt(row, 2).toString());
            txtStop.setText(tableModel.getValueAt(row, 3).toString());
        });
    }

    // ================= CSV =================

    private void loadRoutes() {
        tableModel.setRowCount(0);
        dataValid = true;

        if (!Files.exists(csvPath)) {
            showMessage("ไม่พบไฟล์ routes.csv ที่: " + csvPath.toAbsolutePath(),
                    "ไม่พบไฟล์", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {
            // ตรวจสอบหัวคอลัมน์ (ตัดอักขระ BOM ที่ Excel อาจเติมไว้หน้าไฟล์)
            String header = reader.readLine();
            if (header == null || !DataFiles.stripBom(header).trim().equalsIgnoreCase(HEADER)) {
                throw new IOException("หัวคอลัมน์ต้องเป็น " + HEADER);
            }

            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] fields = line.split(",", -1);
                if (fields.length != 4) {
                    throw new IOException("จำนวนคอลัมน์ไม่ถูกต้องที่บรรทัด " + lineNumber);
                }
                // ตัดช่องว่างหัวท้าย (เช่น "R001 " จากการแก้ใน Excel) ไม่งั้นเทียบรหัสกับเที่ยวรถไม่ตรง
                for (int i = 0; i < fields.length; i++) {
                    fields[i] = fields[i].trim();
                }
                tableModel.addRow(fields);
            }
        } catch (IOException | RuntimeException ex) {
            tableModel.setRowCount(0);
            dataValid = false;
            showMessage("อ่าน routes.csv ไม่สำเร็จ: " + ex.getMessage()
                            + "\nกรุณาแก้ไฟล์แล้วเปิดหน้านี้ใหม่ (ระหว่างนี้จะแก้ไขข้อมูลไม่ได้)",
                    "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean saveRoutes() {
        if (!dataValid) {
            showWarning("ข้อมูลในไฟล์ routes.csv ไม่ถูกต้อง กรุณาแก้ไฟล์แล้วเปิดหน้านี้ใหม่");
            return false;
        }
        List<String> lines = new ArrayList<>();
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            String[] fields = new String[4];
            for (int col = 0; col < 4; col++) {
                fields[col] = tableModel.getValueAt(row, col).toString();
            }
            lines.add(String.join(",", fields));
        }
        try {
            DataFiles.writeCsv(csvPath, HEADER, lines);
            return true;
        } catch (IOException | busbooking.data.DataException ex) {
            showMessage("บันทึก routes.csv ไม่สำเร็จ: " + ex.getMessage(),
                    "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // เส้นทางนี้มีเที่ยวรถใช้อยู่ไหม (ถ้ามี ห้ามลบ/เปลี่ยนรหัส ไม่งั้นเที่ยวรถจะชี้ไปเส้นทางที่ไม่มีอยู่)
    private boolean isRouteUsedByTrip(String routeId) throws IOException {
        return DataFiles.isUsedByTrip(DataFiles.TRIP_ROUTE_ID, routeId);
    }

    // ================= VALIDATION =================

    // อ่านค่าจากฟอร์มแล้วตรวจ คืน null ถ้าข้อมูลไม่ผ่าน (แสดงข้อความเตือนให้แล้ว)
    private String[] getFormData() {
        String routeId = txtRouteId.getText().trim();
        String origin = txtOrigin.getText().trim();
        String destination = txtDestination.getText().trim();
        String stop = txtStop.getText().trim();

        // CSV แบบง่ายนี้ไม่รองรับ comma หรือการขึ้นบรรทัดใหม่ภายในช่องข้อมูล
        for (String value : new String[]{routeId, origin, destination, stop}) {
            if (value.contains(",") || value.contains("\n") || value.contains("\r")) {
                showWarning("ข้อมูลต้องไม่มีเครื่องหมายจุลภาค (,) หรือขึ้นบรรทัดใหม่");
                return null;
            }
        }

        if (routeId.isEmpty() || origin.isEmpty() || destination.isEmpty()) {
            showWarning("กรุณากรอกข้อมูลให้ครบ");
            return null;
        }

        return new String[]{routeId, origin, destination, stop.isEmpty() ? "-" : stop};
    }

    private boolean isDuplicateId(String id, int excludedRow) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (i != excludedRow && tableModel.getValueAt(i, 0).toString().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }

    // ================= ADD =================

    private void addRoute() {
        if (!dataValid) {
            showWarning("ข้อมูลในไฟล์ routes.csv ไม่ถูกต้อง กรุณาแก้ไฟล์แล้วเปิดหน้านี้ใหม่");
            return;
        }
        String[] data = getFormData();
        if (data == null) {
            return;
        }

        if (isDuplicateId(data[0], -1)) {
            showWarning("Route ID นี้มีอยู่แล้ว");
            return;
        }

        tableModel.addRow(data);

        if (saveRoutes()) {
            showSuccess("เพิ่มเส้นทางเรียบร้อยแล้ว");
            clearForm();
        } else {
            loadRoutes();   // คืนค่าจากไฟล์หากบันทึกไม่สำเร็จ
        }
    }

    // ================= EDIT =================

    private void editRoute() {
        if (!dataValid) {
            showWarning("ข้อมูลในไฟล์ routes.csv ไม่ถูกต้อง กรุณาแก้ไฟล์แล้วเปิดหน้านี้ใหม่");
            return;
        }
        int viewRow = routeTable.getSelectedRow();
        if (viewRow < 0) {
            showWarning("กรุณาเลือกเส้นทางที่ต้องการแก้ไข");
            return;
        }

        int row = routeTable.convertRowIndexToModel(viewRow);
        String[] data = getFormData();
        if (data == null) {
            return;
        }

        if (isDuplicateId(data[0], row)) {
            showWarning("Route ID นี้มีอยู่แล้ว");
            return;
        }

        // ถ้าเปลี่ยน Route ID ต้องตรวจสอบว่ามีเที่ยวรถใช้งานอยู่หรือไม่
        String oldRouteId = tableModel.getValueAt(row, 0).toString();
        if (!oldRouteId.equals(data[0])) {
            try {
                if (isRouteUsedByTrip(oldRouteId)) {
                    showMessage("ไม่สามารถเปลี่ยน Route ID จาก " + oldRouteId + " เป็น " + data[0]
                                    + "\nเนื่องจากมีเที่ยวรถใช้งานเส้นทางนี้อยู่",
                            "ไม่สามารถแก้ไขได้", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (IOException ex) {
                showMessage("ไม่สามารถตรวจสอบข้อมูลเที่ยวรถได้\n" + ex.getMessage(),
                        "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        for (int col = 0; col < 4; col++) {
            tableModel.setValueAt(data[col], row, col);
        }

        if (saveRoutes()) {
            showSuccess("แก้ไขเส้นทางเรียบร้อยแล้ว");
            clearForm();
        } else {
            loadRoutes();
        }
    }

    // ================= DELETE =================

    private void deleteRoute() {
        if (!dataValid) {
            showWarning("ข้อมูลในไฟล์ routes.csv ไม่ถูกต้อง กรุณาแก้ไฟล์แล้วเปิดหน้านี้ใหม่");
            return;
        }
        int viewRow = routeTable.getSelectedRow();
        if (viewRow < 0) {
            showWarning("กรุณาเลือกเส้นทางที่ต้องการลบ");
            return;
        }

        int row = routeTable.convertRowIndexToModel(viewRow);
        String routeId = tableModel.getValueAt(row, 0).toString();

        try {
            if (isRouteUsedByTrip(routeId)) {
                showMessage("ไม่สามารถลบเส้นทาง " + routeId
                                + "\nเนื่องจากมีเที่ยวรถใช้งานเส้นทางนี้อยู่",
                        "ไม่สามารถลบได้", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (IOException ex) {
            showMessage("ไม่สามารถตรวจสอบข้อมูลเที่ยวรถได้\n" + ex.getMessage(),
                    "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "คุณต้องการลบเส้นทาง " + routeId + " หรือไม่?",
                "ยืนยันการลบ", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        tableModel.removeRow(row);

        if (saveRoutes()) {
            showSuccess("ลบเส้นทางเรียบร้อยแล้ว");
            clearForm();
        } else {
            loadRoutes();
        }
    }

    private void clearForm() {
        txtRouteId.setText("");
        txtOrigin.setText("");
        txtDestination.setText("");
        txtStop.setText("");
        routeTable.clearSelection();
        txtRouteId.requestFocus();
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    private void showWarning(String message) {
        AdminUi.warn(this, message);
    }

    private void showSuccess(String message) {
        AdminUi.success(this, message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ManageRouteFrame::new);
    }
}
