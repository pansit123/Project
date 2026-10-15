package busbooking.ui;
import javax.swing.*;
import java.awt.*;
public class UserHomeFrame extends JFrame {
    public UserHomeFrame(String userId) {
        super("เมนูผู้โดยสาร - " + userId);
        setSize(470,320);setLocationRelativeTo(null);setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JPanel p = new JPanel(new GridLayout(4,1,10,10));
        p.setBorder(BorderFactory.createEmptyBorder(20,35,20,35));
        JLabel title = new JLabel("ยินดีต้อนรับ " + userId, SwingConstants.CENTER);
        JButton seats = new JButton("ค้นหาเที่ยวรถ / เลือกที่นั่ง");
        JButton history = new JButton("ประวัติการจอง / ชำระเงิน / ยกเลิก");
        JButton logout = new JButton("ออกจากระบบ");
        seats.addActionListener(e -> new SelectSeatGUI(userId).setVisible(true));
        history.addActionListener(e -> new BookingHistoryFrame(userId).setVisible(true));
        logout.addActionListener(e -> {dispose();new login.LoginUI().setVisible(true);});
        p.add(title);p.add(seats);p.add(history);p.add(logout);add(p);
    }
}
