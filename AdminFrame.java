package busbooking.ui.admin;

import javax.swing.*;
import java.awt.*;

public class AdminFrame extends JFrame {

    public AdminFrame() {
        setTitle("ระบบจัดการ Admin");
        setSize(650, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        createUI();
        setVisible(true);
    }

    private void createUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel titleLabel = new JLabel("ระบบจัดการ Admin", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 30));
        titleLabel.setForeground(new Color(0, 90, 160));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel menuPanel = new JPanel(new GridLayout(5, 1, 0, 15));

        JButton btnRoute = createButton("จัดการเส้นทาง", new Color(40, 120, 200));
        JButton btnBusType = createButton("จัดการประเภทรถ", new Color(40, 180, 70));
        JButton btnSchedule = createButton("จัดการเที่ยวรถ", new Color(150, 90, 200));
        JButton btnBookingSummary = createButton("ดูยอดการจอง", new Color(230, 140, 40));
        JButton btnLogout = createButton("ออกจากระบบ", new Color(100, 100, 100));

        menuPanel.add(btnRoute);
        menuPanel.add(btnBusType);
        menuPanel.add(btnSchedule);
        menuPanel.add(btnBookingSummary);
        menuPanel.add(btnLogout);
        mainPanel.add(menuPanel, BorderLayout.CENTER);

        btnRoute.addActionListener(e -> new ManageRouteFrame());
        btnBusType.addActionListener(e -> new ManageBusTypeFrame());
        btnSchedule.addActionListener(e -> new ManageTripFrame());
        btnBookingSummary.addActionListener(e -> new BookingSummaryFrame());
        btnLogout.addActionListener(e -> logout());

        setContentPane(mainPanel);
    }

    private JButton createButton(String text, Color background) {
        JButton button = AdminUi.button(text, background);
        button.setFont(new Font("Tahoma", Font.PLAIN, 17));
        return button;
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
                this, "คุณต้องการออกจากระบบหรือไม่?", "ออกจากระบบ", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // ปิดหน้าจัดการทุกหน้าที่เปิดค้างไว้ด้วย ไม่ให้คนถัดไปแก้ข้อมูลต่อได้โดยไม่ต้อง login
            busbooking.Main.closeAllWindows();
            new AdminLoginFrame();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminFrame::new);
    }
}
