package busbooking.ui;

import javax.swing.*;
import java.awt.*;

public class AdminFrame extends JFrame {

    // Components
  
    private JButton btnRoute;
    private JButton btnBusType;
    private JButton btnSchedule;
    private JButton btnBookingSummary;
    private JButton btnLogout;

    // Constructor
   
    public AdminFrame() {

        setTitle("ระบบจัดการ Admin");

        setSize(650, 600);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        setResizable(false);

        setupFont();

        createUI();

        setVisible(true);
    }

    // Font
  
    private void setupFont() {

        UIManager.put(
                "Label.font",
                new Font(
                        "Tahoma",
                        Font.PLAIN,
                        16
                )
        );

        UIManager.put(
                "Button.font",
                new Font(
                        "Tahoma",
                        Font.PLAIN,
                        17
                )
        );
    }

    // Create UI

    private void createUI() {

        JPanel mainPanel =
                new JPanel(
                        new BorderLayout(
                                20,
                                20
                        )
                );

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        30,
                        50,
                        30,
                        50
                )
        );

        // Title

        JLabel titleLabel =
                new JLabel(
                        "ระบบจัดการ Admin",
                        SwingConstants.CENTER
                );

        titleLabel.setFont(
                new Font(
                        "Tahoma",
                        Font.BOLD,
                        30
                )
        );

        titleLabel.setForeground(
                new Color(
                        0,
                        90,
                        160
                )
        );

        mainPanel.add(
                titleLabel,
                BorderLayout.NORTH
        );

        // Menu
  
        JPanel menuPanel =
                new JPanel(
                        new GridLayout(
                                5,
                                1,
                                0,
                                15
                        )
                );

        btnRoute =
                new JButton(
                        "จัดการเส้นทาง"
                );

        btnBusType =
                new JButton(
                        "จัดการประเภทรถ"
                );

        btnSchedule =
                new JButton(
                        "จัดการตารางเวลา"
                );

        btnBookingSummary =
                new JButton(
                        "ดูยอดการจอง"
                );

        btnLogout =
                new JButton(
                        "ออกจากระบบ"
                );

        // Button Colors
       
        btnRoute.setBackground(
                new Color(
                        40,
                        120,
                        200
                )
        );

        btnBusType.setBackground(
                new Color(
                        40,
                        180,
                        70
                )
        );

        btnSchedule.setBackground(
                new Color(
                        150,
                        90,
                        200
                )
        );

        btnBookingSummary.setBackground(
                new Color(
                        230,
                        140,
                        40
                )
        );

        btnLogout.setBackground(
                new Color(
                        100,
                        100,
                        100
                )
        );

        btnRoute.setForeground(
                Color.WHITE
        );

        btnBusType.setForeground(
                Color.WHITE
        );

        btnSchedule.setForeground(
                Color.WHITE
        );

        btnBookingSummary.setForeground(
                Color.WHITE
        );

        btnLogout.setForeground(
                Color.WHITE
        );

        // Add Buttons
     
        menuPanel.add(
                btnRoute
        );

        menuPanel.add(
                btnBusType
        );

        menuPanel.add(
                btnSchedule
        );

        menuPanel.add(
                btnBookingSummary
        );

        menuPanel.add(
                btnLogout
        );

        mainPanel.add(
                menuPanel,
                BorderLayout.CENTER
        );

        // Events

        btnRoute.addActionListener(
                e -> openRoute()
        );

        btnBusType.addActionListener(
                e -> showNotReady(
                        "จัดการประเภทรถ"
                )
        );

        btnSchedule.addActionListener(
                e -> showNotReady(
                        "จัดการตารางเวลา"
                )
        );

        btnBookingSummary.addActionListener(
                e -> showNotReady(
                        "ดูยอดการจอง"
                )
        );

        btnLogout.addActionListener(
                e -> logout()
        );

        setContentPane(mainPanel);
    }

    // Open Route

    private void openRoute() {

        new ManageRouteFrame();
    }

    // Not Ready

    private void showNotReady(
            String menuName
    ) {

        JOptionPane.showMessageDialog(
                this,
                menuName
                        + "\n\nส่วนนี้กำลังพัฒนา",
                "Admin",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // Logout
  
    private void logout() {

        int confirm =
                JOptionPane.showConfirmDialog(
                        this,
                        "คุณต้องการออกจากระบบหรือไม่?",
                        "ออกจากระบบ",
                        JOptionPane.YES_NO_OPTION
                );

        if (confirm ==
                JOptionPane.YES_OPTION) {

            dispose();

            new AdminLoginFrame();
        }
    }

    // Main
  
    public static void main(
            String[] args
    ) {

        SwingUtilities.invokeLater(
                () -> new AdminFrame()
        );
    }
}