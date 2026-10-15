package busbooking;
import javax.swing.*;
import java.awt.*;
public class Main {
    public static void main(String[] args) {
        UIManager.put("OptionPane.messageFont", new Font("Tahoma", Font.PLAIN, 16));
        UIManager.put("Button.font", new Font("Tahoma", Font.PLAIN, 16));
        UIManager.put("Label.font", new Font("Tahoma", Font.PLAIN, 16));
        UIManager.put("OptionPane.buttonFont", new Font("Tahoma", Font.PLAIN, 14));
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Bus Booking - เลือกประเภทผู้ใช้");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(440,280); f.setLocationRelativeTo(null);
            JPanel p = new JPanel(new GridLayout(3,1,12,12));
            p.setBorder(BorderFactory.createEmptyBorder(25,35,25,35));
            JLabel title = new JLabel("ระบบจองตั๋วรถบัส",SwingConstants.CENTER);
            title.setFont(new Font("Tahoma",Font.BOLD,22));
            JButton user = new JButton("เข้าสู่ระบบผู้โดยสาร / สมัครสมาชิก");
            JButton admin = new JButton("เข้าสู่ระบบผู้ดูแล (Admin)");
            user.setFont(new Font("Tahoma", Font.PLAIN, 16));
            admin.setFont(new Font("Tahoma", Font.PLAIN, 16));
            user.addActionListener(e -> {new login.LoginUI().setVisible(true); f.dispose();});
            admin.addActionListener(e -> {new busbooking.ui.AdminLoginFrame(); f.dispose();});
            p.add(title);p.add(user);p.add(admin);f.setContentPane(p);f.setVisible(true);
        });
    }
}
