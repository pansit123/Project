package busbooking.ui;

import busbooking.model.Admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AdminFrame extends JFrame {

    private Admin admin;

    public AdminFrame(Admin admin) {

        this.admin = admin;

        setTitle("Bus Booking - Admin");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        createUI();

        setVisible(true);
    }

    private void createUI() {

        // Main Panel

        JPanel mainPanel = new JPanel(
                new BorderLayout(15, 15)
        );

        mainPanel.setBackground(
                new Color(240, 248, 255)
        );

        mainPanel.setBorder(
                new EmptyBorder(25, 30, 30, 30)
        );

        // Header

        JLabel title = new JLabel(
                "ระบบจัดการการจองตั๋วรถ - ADMIN",
                SwingConstants.CENTER
        );

        title.setFont(
                new Font("Tahoma", Font.BOLD, 26)
        );

        title.setForeground(
                new Color(20, 60, 100)
        );

        mainPanel.add(
                title,
                BorderLayout.NORTH
        );

        // Menu Panel
    
        JPanel menuPanel = new JPanel(
                new GridLayout(1, 3, 20, 0)
        );

        menuPanel.setBackground(
                new Color(240, 248, 255)
        );

        // Route
   
        JPanel routePanel =
                createMenuPanel(
                        "จัดการเส้นทาง",
                        "จัดการ Route",
                        new Color(52, 152, 219)
                );

 
        // Bus Type

        JPanel busPanel =
                createMenuPanel(
                        "จัดการประเภทรถ",
                        "จัดการ BusType",
                        new Color(46, 204, 113)
                );

        // Trip

        JPanel tripPanel =
                createMenuPanel(
                        "จัดการเที่ยวรถ",
                        "จัดการ Trip",
                        new Color(155, 89, 182)
                );

        menuPanel.add(routePanel);
        menuPanel.add(busPanel);
        menuPanel.add(tripPanel);

        mainPanel.add(
                menuPanel,
                BorderLayout.CENTER
        );

        // Footer
  
        JLabel footer = new JLabel(
                "ยินดีต้อนรับเข้าสู่ระบบจัดการสำหรับผู้ดูแล",
                SwingConstants.CENTER
        );

        footer.setFont(
                new Font("Tahoma", Font.PLAIN, 14)
        );

        footer.setForeground(
                Color.DARK_GRAY
        );

        mainPanel.add(
                footer,
                BorderLayout.SOUTH
        );

        add(mainPanel);
    }

    // สร้าง Panel เมนู

    private JPanel createMenuPanel(
            String titleText,
            String buttonText,
            Color buttonColor
    ) {

        JPanel panel = new JPanel(
                new BorderLayout(10, 10)
        );

        panel.setBackground(Color.WHITE);

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(210, 210, 210),
                                1
                        ),
                        new EmptyBorder(
                                25, 15, 25, 15
                        )
                )
        );

        // หัวข้อ

        JLabel title = new JLabel(
                titleText,
                SwingConstants.CENTER
        );

        title.setFont(
                new Font("Tahoma", Font.BOLD, 20)
        );

        title.setForeground(
                new Color(40, 40, 40)
        );

        panel.add(
                title,
                BorderLayout.NORTH
        );

        // คำอธิบาย

        JLabel description = new JLabel(
                getDescription(titleText),
                SwingConstants.CENTER
        );

        description.setFont(
                new Font("Tahoma", Font.PLAIN, 14)
        );

        description.setForeground(
                Color.GRAY
        );

        panel.add(
                description,
                BorderLayout.CENTER
        );

        // ปุ่ม

        JButton button = new JButton(
                buttonText
        );

        button.setFont(
                new Font("Tahoma", Font.BOLD, 16)
        );

        button.setForeground(Color.WHITE);

        button.setBackground(buttonColor);

        button.setFocusPainted(false);

        button.setPreferredSize(
                new Dimension(180, 50)
        );

        // Action

        if (titleText.equals("จัดการเส้นทาง")) {

            button.addActionListener(
                    e -> manageRoute()
            );

        } else if (
                titleText.equals("จัดการประเภทรถ")
        ) {

            button.addActionListener(
                    e -> manageBusType()
            );

        } else if (
                titleText.equals("จัดการเที่ยวรถ")
        ) {

            button.addActionListener(
                    e -> manageTrip()
            );
        }

        panel.add(
                button,
                BorderLayout.SOUTH
        );

        return panel;
    }

    // Description

    private String getDescription(
            String title
    ) {

        if (title.equals("จัดการเส้นทาง")) {

            return "<html><center>"
                    + "เพิ่ม แก้ไข และลบข้อมูล<br>"
                    + "เส้นทางการเดินทาง"
                    + "</center></html>";

        }

        if (title.equals("จัดการประเภทรถ")) {

            return "<html><center>"
                    + "เพิ่ม แก้ไข และลบข้อมูล<br>"
                    + "ประเภทรถและจำนวนที่นั่ง"
                    + "</center></html>";

        }

        if (title.equals("จัดการเที่ยวรถ")) {

            return "<html><center>"
                    + "เพิ่ม แก้ไข และลบข้อมูล<br>"
                    + "เที่ยวรถและเวลาเดินทาง"
                    + "</center></html>";
        }

        return "";
    }

    // =====================================
    // Route
    // =====================================

    private void manageRoute() {

       new ManageRouteFrame();
        
    }

    // BusType

    private void manageBusType() {

        JOptionPane.showMessageDialog(
                this,
                "เปิดหน้าจัดการประเภทรถ (BusType)",
                "จัดการประเภทรถ",
                JOptionPane.INFORMATION_MESSAGE
        );

        // ขั้นต่อไปจะเปลี่ยนตรงนี้เป็น
        // new ManageBusTypeFrame();
    }


    // Trip

    private void manageTrip() {

        JOptionPane.showMessageDialog(
                this,
                "เปิดหน้าจัดการเที่ยวรถ (Trip)",
                "จัดการเที่ยวรถ",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}