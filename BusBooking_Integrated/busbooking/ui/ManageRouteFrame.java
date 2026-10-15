package busbooking.ui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ManageRouteFrame extends JFrame {

    
    // Components
   

    private JTable routeTable;
    private DefaultTableModel tableModel;

    private JTextField txtRouteId;
    private JTextField txtOrigin;
    private JTextField txtDestination;
    private JTextField txtStop;

    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnBack;


  
    // Fonts
 
    private static final Font FONT_NORMAL =
            new Font("Tahoma", Font.PLAIN, 15);

    private static final Font FONT_LABEL =
            new Font("Tahoma", Font.PLAIN, 16);

    private static final Font FONT_BUTTON =
            new Font("Tahoma", Font.BOLD, 15);

    private static final Font FONT_TITLE =
            new Font("Tahoma", Font.BOLD, 28);

    private static final Font FONT_TABLE_HEADER =
            new Font("Tahoma", Font.BOLD, 15);


    
    // Constructor
   

    public ManageRouteFrame() {

        setTitle("จัดการเส้นทาง - Route");

        setSize(1000, 650);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        setResizable(false);

        createUI();

        setVisible(true);
    }


    
    // Create UI
    

    private void createUI() {

     
        // Main Panel
      

        JPanel mainPanel =
                new JPanel(new BorderLayout(15, 15));

        mainPanel.setBackground(Color.WHITE);

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        20,
                        20,
                        20
                )
        );


        
        // Title


        JLabel titleLabel =
                new JLabel(
                        "จัดการเส้นทาง (Route)",
                        SwingConstants.CENTER
                );

        titleLabel.setFont(FONT_TITLE);

        titleLabel.setForeground(
                new Color(0, 90, 160)
        );

        titleLabel.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        0,
                        5,
                        0
                )
        );

        mainPanel.add(
                titleLabel,
                BorderLayout.NORTH
        );


       
        // LEFT PANEL
        

        JPanel leftPanel =
                new JPanel(new BorderLayout(10, 10));

        leftPanel.setBackground(Color.WHITE);

        leftPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        new LineBorder(
                                new Color(180, 180, 180),
                                1
                        ),
                        BorderFactory.createEmptyBorder(
                                10,
                                12,
                                12,
                                12
                        )
                )
        );



        // Form Title
    

        JLabel formTitle =
                new JLabel("ข้อมูลเส้นทาง");

        formTitle.setFont(
                new Font(
                        "Tahoma",
                        Font.BOLD,
                        16
                )
        );

        formTitle.setForeground(
                new Color(50, 50, 50)
        );

        leftPanel.add(
                formTitle,
                BorderLayout.NORTH
        );



        // Form Panel


        JPanel formPanel =
                new JPanel(new GridBagLayout());

        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(
                        8,
                        0,
                        8,
                        10
                );

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        gbc.anchor =
                GridBagConstraints.WEST;


        
        // Route ID


        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;

        JLabel lblRouteId =
                new JLabel("Route ID");

        lblRouteId.setFont(FONT_LABEL);

        formPanel.add(
                lblRouteId,
                gbc
        );

        txtRouteId =
                new JTextField();

        txtRouteId.setFont(FONT_NORMAL);

        txtRouteId.setPreferredSize(
                new Dimension(350, 40)
        );

        gbc.gridx = 1;
        gbc.weightx = 1;

        gbc.insets =
                new Insets(
                        8,
                        0,
                        8,
                        0
                );

        formPanel.add(
                txtRouteId,
                gbc
        );


     
        // ต้นทาง
    

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;

        gbc.insets =
                new Insets(
                        8,
                        0,
                        8,
                        10
                );

        JLabel lblOrigin =
                new JLabel("ต้นทาง");

        lblOrigin.setFont(FONT_LABEL);

        formPanel.add(
                lblOrigin,
                gbc
        );

        txtOrigin =
                new JTextField();

        txtOrigin.setFont(FONT_NORMAL);

        txtOrigin.setPreferredSize(
                new Dimension(350, 40)
        );

        gbc.gridx = 1;
        gbc.weightx = 1;

        gbc.insets =
                new Insets(
                        8,
                        0,
                        8,
                        0
                );

        formPanel.add(
                txtOrigin,
                gbc
        );


     
        // ปลายทาง
   

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;

        gbc.insets =
                new Insets(
                        8,
                        0,
                        8,
                        10
                );

        JLabel lblDestination =
                new JLabel("ปลายทาง");

        lblDestination.setFont(FONT_LABEL);

        formPanel.add(
                lblDestination,
                gbc
        );

        txtDestination =
                new JTextField();

        txtDestination.setFont(FONT_NORMAL);

        txtDestination.setPreferredSize(
                new Dimension(350, 40)
        );

        gbc.gridx = 1;
        gbc.weightx = 1;

        gbc.insets =
                new Insets(
                        8,
                        0,
                        8,
                        0
                );

        formPanel.add(
                txtDestination,
                gbc
        );


  
        // จุดแวะ


        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;

        gbc.insets =
                new Insets(
                        8,
                        0,
                        8,
                        10
                );

        JLabel lblStop =
                new JLabel("จุดแวะ");

        lblStop.setFont(FONT_LABEL);

        formPanel.add(
                lblStop,
                gbc
        );

        txtStop =
                new JTextField();

        txtStop.setFont(FONT_NORMAL);

        txtStop.setPreferredSize(
                new Dimension(350, 40)
        );

        gbc.gridx = 1;
        gbc.weightx = 1;

        gbc.insets =
                new Insets(
                        8,
                        0,
                        8,
                        0
                );

        formPanel.add(
                txtStop,
                gbc
        );


        leftPanel.add(
                formPanel,
                BorderLayout.CENTER
        );


    
        // Buttons
       

        JPanel buttonPanel =
                new JPanel(
                        new GridLayout(
                                4,
                                1,
                                0,
                                8
                        )
                );

        buttonPanel.setBackground(Color.WHITE);


        // Add


        btnAdd =
                new JButton("เพิ่มเส้นทาง");

        styleButton(
                btnAdd,
                new Color(40, 180, 70)
        );


        
        // Edit
  
        btnEdit =
                new JButton("แก้ไข");

        styleButton(
                btnEdit,
                new Color(30, 120, 200)
        );


    
        // Delete
    

        btnDelete =
                new JButton("ลบ");

        styleButton(
                btnDelete,
                new Color(220, 50, 50)
        );


  
        // Back


        btnBack =
                new JButton("กลับหน้าหลัก");

        styleButton(
                btnBack,
                new Color(100, 100, 100)
        );


        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnBack);


        leftPanel.add(
                buttonPanel,
                BorderLayout.SOUTH
        );


        // RIGHT PANEL


        JPanel rightPanel =
                new JPanel(new BorderLayout(5, 5));

        rightPanel.setBackground(Color.WHITE);

        rightPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        new LineBorder(
                                new Color(180, 180, 180),
                                1
                        ),
                        BorderFactory.createEmptyBorder(
                                10,
                                5,
                                5,
                                5
                        )
                )
        );


   
        // Table Title
  

        JLabel tableTitle =
                new JLabel("รายการเส้นทาง");

        tableTitle.setFont(
                new Font(
                        "Tahoma",
                        Font.BOLD,
                        16
                )
        );

        tableTitle.setForeground(
                new Color(50, 50, 50)
        );

        tableTitle.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        5,
                        5,
                        0
                )
        );

        rightPanel.add(
                tableTitle,
                BorderLayout.NORTH
        );


        
        // Table
       

        String[] columns = {
                "Route ID",
                "ต้นทาง",
                "ปลายทาง",
                "จุดแวะ"
        };


        tableModel =
                new DefaultTableModel(
                        columns,
                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }
                };


        routeTable =
                new JTable(tableModel);


        routeTable.setFont(FONT_NORMAL);

        routeTable.setRowHeight(35);

        routeTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        routeTable.setAutoCreateRowSorter(true);



        // Table Header
        

        routeTable
                .getTableHeader()
                .setFont(
                        FONT_TABLE_HEADER
                );

        routeTable
                .getTableHeader()
                .setPreferredSize(
                        new Dimension(
                                0,
                                35
                        )
                );



        // Column Width


        routeTable
                .getColumnModel()
                .getColumn(0)
                .setPreferredWidth(90);

        routeTable
                .getColumnModel()
                .getColumn(1)
                .setPreferredWidth(130);

        routeTable
                .getColumnModel()
                .getColumn(2)
                .setPreferredWidth(130);

        routeTable
                .getColumnModel()
                .getColumn(3)
                .setPreferredWidth(150);


        JScrollPane scrollPane =
                new JScrollPane(routeTable);

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        new Color(190, 190, 190)
                )
        );


        rightPanel.add(
                scrollPane,
                BorderLayout.CENTER
        );


        // Center


        JPanel centerPanel =
                new JPanel(
                        new GridLayout(
                                1,
                                2,
                                15,
                                0
                        )
                );

        centerPanel.setBackground(Color.WHITE);

        centerPanel.add(leftPanel);

        centerPanel.add(rightPanel);


        mainPanel.add(
                centerPanel,
                BorderLayout.CENTER
        );



        // Sample Data


        loadRoutes();



        // Events
  

        btnAdd.addActionListener(
                this::addRoute
        );

        btnEdit.addActionListener(
                this::editRoute
        );

        btnDelete.addActionListener(
                this::deleteRoute
        );

        btnBack.addActionListener(
                e -> dispose()
        );



        // Table Selection


        routeTable
                .getSelectionModel()
                .addListSelectionListener(e -> {

                    if (!e.getValueIsAdjusting()) {

                        int viewRow =
                                routeTable.getSelectedRow();

                        if (viewRow >= 0) {

                            int row =
                                    routeTable
                                            .convertRowIndexToModel(
                                                    viewRow
                                            );

                            txtRouteId.setText(
                                    tableModel
                                            .getValueAt(
                                                    row,
                                                    0
                                            )
                                            .toString()
                            );

                            txtOrigin.setText(
                                    tableModel
                                            .getValueAt(
                                                    row,
                                                    1
                                            )
                                            .toString()
                            );

                            txtDestination.setText(
                                    tableModel
                                            .getValueAt(
                                                    row,
                                                    2
                                            )
                                            .toString()
                            );

                            txtStop.setText(
                                    tableModel
                                            .getValueAt(
                                                    row,
                                                    3
                                            )
                                            .toString()
                            );
                        }
                    }
                });


        // =================================================
        // Set Content
        // =================================================

        setContentPane(mainPanel);
    }


    // =====================================================
    // Button Style
    // =====================================================

    private void styleButton(
            JButton button,
            Color background
    ) {

        button.setFont(FONT_BUTTON);

        button.setBackground(background);

        button.setForeground(Color.WHITE);

        button.setFocusPainted(false);

        button.setBorderPainted(false);

        button.setOpaque(true);

        button.setPreferredSize(
                new Dimension(
                        350,
                        42
                )
        );
    }


    // =====================================================
    // Sample Data
    // =====================================================

    // รองรับการรันจากโฟลเดอร์ Src หรือโฟลเดอร์โปรเจกต์
    private Path getRoutesPath() {
        Path inSrc = Paths.get("busbooking", "data", "routes.csv");
        Path fromRoot = Paths.get("src", "busbooking", "data", "routes.csv");
        Path fromRootUpper = Paths.get("Src", "busbooking", "data", "routes.csv");
        if (Files.exists(inSrc)) return inSrc;
        if (Files.exists(fromRoot)) return fromRoot;
        if (Files.exists(fromRootUpper)) return fromRootUpper;
        return inSrc;
    }

    private void loadRoutes() {
        Path path = getRoutesPath();
        tableModel.setRowCount(0);
        if (!Files.exists(path)) {
            showMessage("ไม่พบไฟล์ routes.csv ที่: " + path.toAbsolutePath(),
                    "ไม่พบไฟล์", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber == 1) {
                    // ตรวจสอบหัวคอลัมน์ (รองรับ UTF-8 BOM)
                    String header = line.replace("\uFEFF", "").trim();
                    if (!header.equalsIgnoreCase("route_id,origin,destination,stop")) {
                        throw new IOException("หัวคอลัมน์ต้องเป็น route_id,origin,destination,stop");
                    }
                    continue;
                }
                if (line.trim().isEmpty()) continue;
                String[] fields = line.split(",", -1);
                if (fields.length != 4) {
                    throw new IOException("จำนวนคอลัมน์ไม่ถูกต้องที่บรรทัด " + lineNumber);
                }
                tableModel.addRow(fields);
            }
        } catch (IOException ex) {
            tableModel.setRowCount(0);
            showMessage("อ่าน routes.csv ไม่สำเร็จ: " + ex.getMessage(),
                    "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean saveRoutes() {
        Path path = getRoutesPath();
        try {
            if (path.getParent() != null) Files.createDirectories(path.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                writer.write("route_id,origin,destination,stop");
                writer.newLine();
                for (int row = 0; row < tableModel.getRowCount(); row++) {
                    String[] fields = new String[4];
                    for (int col = 0; col < 4; col++) {
                        fields[col] = tableModel.getValueAt(row, col).toString();
                    }
                    writer.write(String.join(",", fields));
                    writer.newLine();
                }
            }
            return true;
        } catch (IOException ex) {
            showMessage("บันทึก routes.csv ไม่สำเร็จ: " + ex.getMessage(),
                    "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // CSV แบบง่ายนี้ไม่รองรับ comma หรือการขึ้นบรรทัดใหม่ภายในช่องข้อมูล
    private boolean isValidCsvField(String value) {
        return !value.contains(",") && !value.contains("\n") && !value.contains("\r");
    }


    // Add Route
   

    private void addRoute(ActionEvent e) {

        String routeId =
                txtRouteId.getText().trim();

        String origin =
                txtOrigin.getText().trim();

        String destination =
                txtDestination.getText().trim();

        String stop =
                txtStop.getText().trim();


        if (!isValidCsvField(routeId) || !isValidCsvField(origin)
                || !isValidCsvField(destination) || !isValidCsvField(stop)) {
            showMessage("ข้อมูลต้องไม่มีเครื่องหมายจุลภาค (,) หรือขึ้นบรรทัดใหม่",
                    "แจ้งเตือน", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (routeId.isEmpty()
                || origin.isEmpty()
                || destination.isEmpty()) {

            showMessage(
                    "กรุณากรอกข้อมูลให้ครบ",
                    "แจ้งเตือน",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        // ตรวจสอบ Route ID ซ้ำ

        for (int i = 0;
             i < tableModel.getRowCount();
             i++) {

            String existingId =
                    tableModel
                            .getValueAt(i, 0)
                            .toString();

            if (existingId.equalsIgnoreCase(routeId)) {

                showMessage(
                        "Route ID นี้มีอยู่แล้ว",
                        "แจ้งเตือน",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }
        }


        tableModel.addRow(
                new Object[]{
                        routeId,
                        origin,
                        destination,
                        stop.isEmpty()
                                ? "-"
                                : stop
                }
        );


        if (!saveRoutes()) {
            loadRoutes(); // คืนค่าจากไฟล์หากบันทึกไม่สำเร็จ
            return;
        }

        showMessage(
                "เพิ่มเส้นทางเรียบร้อยแล้ว",
                "สำเร็จ",
                JOptionPane.INFORMATION_MESSAGE
        );


        clearForm();
    }


    // =====================================================
    // Edit Route
    // =====================================================

private void editRoute(ActionEvent e) {

    int viewRow = routeTable.getSelectedRow();

    if (viewRow < 0) {
        showMessage(
            "กรุณาเลือกเส้นทางที่ต้องการแก้ไข",
            "แจ้งเตือน",
            JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    int row = routeTable.convertRowIndexToModel(viewRow);

    // Route ID เดิมจากตาราง
    String oldRouteId = tableModel.getValueAt(row, 0).toString();

    // ข้อมูลใหม่จากช่องกรอก
    String newRouteId = txtRouteId.getText().trim();
    String origin = txtOrigin.getText().trim();
    String destination = txtDestination.getText().trim();
    String stop = txtStop.getText().trim();

    // ตรวจสอบข้อมูล CSV
    if (!isValidCsvField(newRouteId)
            || !isValidCsvField(origin)
            || !isValidCsvField(destination)
            || !isValidCsvField(stop)) {

        showMessage(
            "ข้อมูลต้องไม่มีเครื่องหมายจุลภาค (,) หรือขึ้นบรรทัดใหม่",
            "แจ้งเตือน",
            JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    // ตรวจสอบช่องที่จำเป็น
    if (newRouteId.isEmpty()
            || origin.isEmpty()
            || destination.isEmpty()) {

        showMessage(
            "กรุณากรอกข้อมูลให้ครบ",
            "แจ้งเตือน",
            JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    // ตรวจสอบ Route ID ซ้ำ
    for (int i = 0; i < tableModel.getRowCount(); i++) {

        if (i == row) {
            continue;
        }

        String existingId = tableModel.getValueAt(i, 0).toString();

        if (existingId.equalsIgnoreCase(newRouteId)) {
            showMessage(
                "Route ID นี้มีอยู่แล้ว",
                "แจ้งเตือน",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
    }

    // ถ้าเปลี่ยน Route ID ต้องตรวจสอบว่ามี Trip ใช้งานหรือไม่
    if (!oldRouteId.equals(newRouteId)) {

        try {

            if (isRouteUsedByTrip(oldRouteId)) {

                showMessage(
                    "ไม่สามารถเปลี่ยน Route ID จาก "
                    + oldRouteId + " เป็น " + newRouteId
                    + "\nเนื่องจากมีเที่ยวรถใช้งานเส้นทางนี้อยู่",
                    "ไม่สามารถแก้ไขได้",
                    JOptionPane.WARNING_MESSAGE
                );

                return;
            }

        } catch (IOException ex) {

            showMessage(
                "ไม่สามารถตรวจสอบข้อมูลเที่ยวรถได้\n"
                + ex.getMessage(),
                "ข้อผิดพลาด",
                JOptionPane.ERROR_MESSAGE
            );

            return;
        }
    }

    // อัปเดตข้อมูลในตาราง
    tableModel.setValueAt(newRouteId, row, 0);
    tableModel.setValueAt(origin, row, 1);
    tableModel.setValueAt(destination, row, 2);
    tableModel.setValueAt(
        stop.isEmpty() ? "-" : stop,
        row,
        3
    );

    // บันทึกลง CSV
    if (!saveRoutes()) {
        loadRoutes();
        return;
    }

    showMessage(
        "แก้ไขเส้นทางเรียบร้อยแล้ว",
        "สำเร็จ",
        JOptionPane.INFORMATION_MESSAGE
    );

    clearForm();
}

    // =====================================================
    // Delete Route
    // =====================================================
private boolean isRouteUsedByTrip(String routeId) throws IOException {

    Path path = Paths.get(
        "busbooking", "data", "trip_schedules.csv"
    );

    if (!Files.exists(path)) {
        path = Paths.get(
            "Src", "busbooking", "data", "trip_schedules.csv"
        );
    }

    if (!Files.exists(path)) {
        path = Paths.get(
            "src", "busbooking", "data", "trip_schedules.csv"
        );
    }

    if (!Files.exists(path)) {
        throw new IOException("ไม่พบไฟล์ trip_schedules.csv");
    }

    try (BufferedReader reader =
            Files.newBufferedReader(path, StandardCharsets.UTF_8)) {

        String header = reader.readLine();

        if (header == null ||
            !header.replace("\uFEFF", "").trim().equals(
                "trip_id,route_id,bus_type_id,travel_date,departure_time,price,available_seats"
            )) {
            throw new IOException("หัวตาราง trip_schedules.csv ไม่ถูกต้อง");
        }

        String line;

        while ((line = reader.readLine()) != null) {

            if (line.trim().isEmpty()) {
                continue;
            }

            String[] fields = line.split(",", -1);

            if (fields.length != 7) {
                throw new IOException("ข้อมูลเที่ยวรถมีคอลัมน์ไม่ครบ");
            }

            if (fields[1].trim().equalsIgnoreCase(routeId)) {
                return true;
            }
        }
    }

    return false;
}
    private void deleteRoute(ActionEvent e) {

        int viewRow =
                routeTable.getSelectedRow();


        if (viewRow < 0) {

            showMessage(
                    "กรุณาเลือกเส้นทางที่ต้องการลบ",
                    "แจ้งเตือน",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        int row =
                routeTable
                        .convertRowIndexToModel(
                                viewRow
                        );


        String routeId =
                tableModel
                        .getValueAt(
                                row,
                                0
                        )
                        .toString();


                        try {

    if (isRouteUsedByTrip(routeId)) {

        showMessage(
            "ไม่สามารถลบเส้นทาง " + routeId
            + "\nเนื่องจากมีเที่ยวรถใช้งานเส้นทางนี้อยู่",
            "ไม่สามารถลบได้",
            JOptionPane.WARNING_MESSAGE
        );

        return;
    }

} catch (IOException ex) {

    showMessage(
        "ไม่สามารถตรวจสอบข้อมูลเที่ยวรถได้\n"
        + ex.getMessage(),
        "ข้อผิดพลาด",
        JOptionPane.ERROR_MESSAGE
    );

    return;
}

        int confirm =
                JOptionPane.showConfirmDialog(
                        this,
                        "คุณต้องการลบเส้นทาง "
                                + routeId
                                + " หรือไม่?",
                        "ยืนยันการลบ",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );


        if (confirm ==
                JOptionPane.YES_OPTION) {

            tableModel.removeRow(row);


            if (!saveRoutes()) {
                loadRoutes();
                return;
            }

            showMessage(
                    "ลบเส้นทางเรียบร้อยแล้ว",
                    "สำเร็จ",
                    JOptionPane.INFORMATION_MESSAGE
            );


            clearForm();
        }
    }


    // =====================================================
    // Clear Form
    // =====================================================

    private void clearForm() {

        txtRouteId.setText("");

        txtOrigin.setText("");

        txtDestination.setText("");

        txtStop.setText("");

        routeTable.clearSelection();

        txtRouteId.requestFocus();
    }


    // =====================================================
    // Message Dialog
    // =====================================================

    private void showMessage(
            String message,
            String title,
            int messageType
    ) {

        JOptionPane.showMessageDialog(
                this,
                message,
                title,
                messageType
        );
    }


    // =====================================================
    // Main
    // =====================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {

                UIManager.setLookAndFeel(
                        UIManager
                                .getSystemLookAndFeelClassName()
                );

            } catch (Exception ex) {

                ex.printStackTrace();
            }


            new ManageRouteFrame();
        });
    }
}