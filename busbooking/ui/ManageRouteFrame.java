package busbooking.ui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ManageRouteFrame extends JFrame {

    // =========================================================
    // Components
    // =========================================================

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


    // =========================================================
    // Constructor
    // =========================================================

    public ManageRouteFrame() {

        setTitle("จัดการเส้นทาง - Route");

        setSize(1000, 650);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setResizable(false);


        // =====================================================
        // Font ภาษาไทย
        // =====================================================

        Font thaiFont = new Font(
                "Tahoma",
                Font.PLAIN,
                16
        );

        Font thaiBoldFont = new Font(
                "Tahoma",
                Font.BOLD,
                16
        );

        UIManager.put(
                "Label.font",
                thaiFont
        );

        UIManager.put(
                "Button.font",
                thaiFont
        );

        UIManager.put(
                "TextField.font",
                thaiFont
        );

        UIManager.put(
                "Table.font",
                thaiFont
        );

        UIManager.put(
                "TableHeader.font",
                thaiBoldFont
        );

        UIManager.put(
                "OptionPane.messageFont",
                thaiFont
        );

        UIManager.put(
                "OptionPane.buttonFont",
                thaiFont
        );

        UIManager.put(
                "OptionPane.titleFont",
                thaiBoldFont
        );


        createUI();

        setVisible(true);
    }


    // =========================================================
    // Create UI
    // =========================================================

    private void createUI() {

        JPanel mainPanel = new JPanel(
                new BorderLayout(15, 15)
        );

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        20,
                        20,
                        20
                )
        );


        // =====================================================
        // Title
        // =====================================================

        JLabel titleLabel = new JLabel(
                "จัดการเส้นทาง (Route)",
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
                new Color(0, 90, 160)
        );

        mainPanel.add(
                titleLabel,
                BorderLayout.NORTH
        );


        // =====================================================
        // Form Panel
        // =====================================================

        JPanel formPanel = new JPanel(
                new GridBagLayout()
        );

        // สำคัญ:
        // ไม่ใช้ createTitledBorder
        // เพื่อไม่ให้เกิด □□□□

        formPanel.setBorder(
                BorderFactory.createLineBorder(
                        new Color(210, 210, 210),
                        1
                )
        );


        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(
                        10,
                        15,
                        10,
                        15
                );

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        gbc.anchor =
                GridBagConstraints.WEST;


        // =====================================================
        // Route ID
        // =====================================================

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;

        JLabel lblRouteId =
                new JLabel("Route ID");

        lblRouteId.setFont(
                new Font(
                        "Tahoma",
                        Font.PLAIN,
                        16
                )
        );

        formPanel.add(
                lblRouteId,
                gbc
        );


        txtRouteId = new JTextField();

        txtRouteId.setFont(
                new Font(
                        "Tahoma",
                        Font.PLAIN,
                        16
                )
        );

        txtRouteId.setPreferredSize(
                new Dimension(
                        250,
                        40
                )
        );

        gbc.gridx = 1;
        gbc.weightx = 1;

        formPanel.add(
                txtRouteId,
                gbc
        );


        // =====================================================
        // ต้นทาง
        // =====================================================

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;

        JLabel lblOrigin =
                new JLabel("ต้นทาง");

        lblOrigin.setFont(
                new Font(
                        "Tahoma",
                        Font.PLAIN,
                        16
                )
        );

        formPanel.add(
                lblOrigin,
                gbc
        );


        txtOrigin = new JTextField();

        txtOrigin.setFont(
                new Font(
                        "Tahoma",
                        Font.PLAIN,
                        16
                )
        );

        txtOrigin.setPreferredSize(
                new Dimension(
                        250,
                        40
                )
        );

        gbc.gridx = 1;
        gbc.weightx = 1;

        formPanel.add(
                txtOrigin,
                gbc
        );


        // =====================================================
        // ปลายทาง
        // =====================================================

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;

        JLabel lblDestination =
                new JLabel("ปลายทาง");

        lblDestination.setFont(
                new Font(
                        "Tahoma",
                        Font.PLAIN,
                        16
                )
        );

        formPanel.add(
                lblDestination,
                gbc
        );


        txtDestination = new JTextField();

        txtDestination.setFont(
                new Font(
                        "Tahoma",
                        Font.PLAIN,
                        16
                )
        );

        txtDestination.setPreferredSize(
                new Dimension(
                        250,
                        40
                )
        );

        gbc.gridx = 1;
        gbc.weightx = 1;

        formPanel.add(
                txtDestination,
                gbc
        );


        // =====================================================
        // จุดแวะ
        // =====================================================

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;

        JLabel lblStop =
                new JLabel("จุดแวะ");

        lblStop.setFont(
                new Font(
                        "Tahoma",
                        Font.PLAIN,
                        16
                )
        );

        formPanel.add(
                lblStop,
                gbc
        );


        txtStop = new JTextField();

        txtStop.setFont(
                new Font(
                        "Tahoma",
                        Font.PLAIN,
                        16
                )
        );

        txtStop.setPreferredSize(
                new Dimension(
                        250,
                        40
                )
        );

        gbc.gridx = 1;
        gbc.weightx = 1;

        formPanel.add(
                txtStop,
                gbc
        );


        // =====================================================
        // Buttons
        // =====================================================

        btnAdd =
                new JButton("เพิ่มเส้นทาง");

        btnEdit =
                new JButton("แก้ไข");

        btnDelete =
                new JButton("ลบ");

        btnBack =
                new JButton("กลับหน้าหลัก");


        // -----------------------------------------------------
        // Button Style
        // -----------------------------------------------------

        styleButton(
                btnAdd,
                new Color(40, 180, 70)
        );

        styleButton(
                btnEdit,
                new Color(30, 120, 200)
        );

        styleButton(
                btnDelete,
                new Color(220, 50, 50)
        );

        styleButton(
                btnBack,
                new Color(100, 100, 100)
        );


        // =====================================================
        // Add Button
        // =====================================================

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 1;

        gbc.insets =
                new Insets(
                        20,
                        15,
                        8,
                        15
                );

        formPanel.add(
                btnAdd,
                gbc
        );


        // =====================================================
        // Edit Button
        // =====================================================

        gbc.gridy = 5;

        formPanel.add(
                btnEdit,
                gbc
        );


        // =====================================================
        // Delete Button
        // =====================================================

        gbc.gridy = 6;

        formPanel.add(
                btnDelete,
                gbc
        );


        // =====================================================
        // Back Button
        // =====================================================

        gbc.gridy = 7;

        gbc.insets =
                new Insets(
                        8,
                        15,
                        15,
                        15
                );

        formPanel.add(
                btnBack,
                gbc
        );


        // =====================================================
        // Table
        // =====================================================

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


        routeTable.setRowHeight(35);


        routeTable.setFont(
                new Font(
                        "Tahoma",
                        Font.PLAIN,
                        15
                )
        );


        routeTable.getTableHeader()
                .setFont(
                        new Font(
                                "Tahoma",
                                Font.BOLD,
                                15
                        )
                );


        routeTable.getTableHeader()
                .setReorderingAllowed(false);


        routeTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );


        // =====================================================
        // Table Column Width
        // =====================================================

        routeTable.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(80);

        routeTable.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(120);

        routeTable.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(150);

        routeTable.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(130);


        // =====================================================
        // Scroll Pane
        // =====================================================

        JScrollPane scrollPane =
                new JScrollPane(
                        routeTable
                );


        // สำคัญ:
        // ใช้ LineBorder แทน TitledBorder
        // ป้องกัน □□□□

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        new Color(210, 210, 210),
                        1
                )
        );


        // =====================================================
        // Center Panel
        // =====================================================

        JPanel centerPanel =
                new JPanel(
                        new GridLayout(
                                1,
                                2,
                                15,
                                0
                        )
                );


        centerPanel.add(
                formPanel
        );


        centerPanel.add(
                scrollPane
        );


        mainPanel.add(
                centerPanel,
                BorderLayout.CENTER
        );


        // =====================================================
        // Sample Data
        // =====================================================

        addSampleData();


        // =====================================================
        // Events
        // =====================================================

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


        // =====================================================
        // Table Selection
        // =====================================================

        routeTable
                .getSelectionModel()
                .addListSelectionListener(e -> {

                    if (!e.getValueIsAdjusting()) {

                        int row =
                                routeTable
                                        .getSelectedRow();


                        if (row >= 0) {

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


        setContentPane(
                mainPanel
        );
    }


    // =========================================================
    // Button Style
    // =========================================================

    private void styleButton(
            JButton button,
            Color color
    ) {

        button.setFont(
                new Font(
                        "Tahoma",
                        Font.BOLD,
                        15
                )
        );

        button.setBackground(
                color
        );

        button.setForeground(
                Color.WHITE
        );

        button.setFocusPainted(
                false
        );

        button.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        15,
                        10,
                        15
                )
        );

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );
    }


    // =========================================================
    // Sample Data
    // =========================================================

    private void addSampleData() {

        tableModel.addRow(
                new Object[]{
                        "TR001",
                        "กรุงเทพฯ",
                        "เชียงใหม่",
                        "นครสวรรค์"
                }
        );


        tableModel.addRow(
                new Object[]{
                        "TR002",
                        "กรุงเทพฯ",
                        "พัทยา",
                        "-"
                }
        );
    }


    // =========================================================
    // Add Route
    // =========================================================

    private void addRoute(
            ActionEvent e
    ) {

        String routeId =
                txtRouteId
                        .getText()
                        .trim();


        String origin =
                txtOrigin
                        .getText()
                        .trim();


        String destination =
                txtDestination
                        .getText()
                        .trim();


        String stop =
                txtStop
                        .getText()
                        .trim();


        if (
                routeId.isEmpty()
                        ||
                origin.isEmpty()
                        ||
                destination.isEmpty()
        ) {

            JOptionPane.showMessageDialog(
                    this,
                    "กรุณากรอกข้อมูลให้ครบ",
                    "แจ้งเตือน",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        // =====================================================
        // ตรวจ Route ID ซ้ำ
        // =====================================================

        for (
                int i = 0;
                i < tableModel.getRowCount();
                i++
        ) {

            String existingId =
                    tableModel
                            .getValueAt(
                                    i,
                                    0
                            )
                            .toString();


            if (
                    existingId
                            .equalsIgnoreCase(
                                    routeId
                            )
            ) {

                JOptionPane.showMessageDialog(
                        this,
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


        JOptionPane.showMessageDialog(
                this,
                "เพิ่มเส้นทางเรียบร้อยแล้ว",
                "สำเร็จ",
                JOptionPane.INFORMATION_MESSAGE
        );


        clearForm();
    }


    // =========================================================
    // Edit Route
    // =========================================================

    private void editRoute(
            ActionEvent e
    ) {

        int row =
                routeTable
                        .getSelectedRow();


        if (row < 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "กรุณาเลือกเส้นทางที่ต้องการแก้ไข",
                    "แจ้งเตือน",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        String routeId =
                txtRouteId
                        .getText()
                        .trim();


        String origin =
                txtOrigin
                        .getText()
                        .trim();


        String destination =
                txtDestination
                        .getText()
                        .trim();


        String stop =
                txtStop
                        .getText()
                        .trim();


        if (
                routeId.isEmpty()
                        ||
                origin.isEmpty()
                        ||
                destination.isEmpty()
        ) {

            JOptionPane.showMessageDialog(
                    this,
                    "กรุณากรอกข้อมูลให้ครบ",
                    "แจ้งเตือน",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        tableModel.setValueAt(
                routeId,
                row,
                0
        );


        tableModel.setValueAt(
                origin,
                row,
                1
        );


        tableModel.setValueAt(
                destination,
                row,
                2
        );


        tableModel.setValueAt(
                stop.isEmpty()
                        ? "-"
                        : stop,
                row,
                3
        );


        JOptionPane.showMessageDialog(
                this,
                "แก้ไขเส้นทางเรียบร้อยแล้ว",
                "สำเร็จ",
                JOptionPane.INFORMATION_MESSAGE
        );


        clearForm();
    }


    // =========================================================
    // Delete Route
    // =========================================================

    private void deleteRoute(
            ActionEvent e
    ) {

        int row =
                routeTable
                        .getSelectedRow();


        if (row < 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "กรุณาเลือกเส้นทางที่ต้องการลบ",
                    "แจ้งเตือน",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        int confirm =
                JOptionPane.showConfirmDialog(
                        this,
                        "คุณต้องการลบเส้นทางนี้หรือไม่?",
                        "ยืนยันการลบ",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );


        if (
                confirm
                        ==
                        JOptionPane.YES_OPTION
        ) {

            tableModel.removeRow(
                    row
            );


            JOptionPane.showMessageDialog(
                    this,
                    "ลบเส้นทางเรียบร้อยแล้ว",
                    "สำเร็จ",
                    JOptionPane.INFORMATION_MESSAGE
            );


            clearForm();
        }
    }


    // =========================================================
    // Clear Form
    // =========================================================

    private void clearForm() {

        txtRouteId.setText("");

        txtOrigin.setText("");

        txtDestination.setText("");

        txtStop.setText("");

        routeTable.clearSelection();
    }


    // =========================================================
    // Main
    // =========================================================

    public static void main(
            String[] args
    ) {

        SwingUtilities.invokeLater(
                () -> {

                    new ManageRouteFrame();

                }
        );
    }
}