package busbooking.ui.member;

import busbooking.model.User;
import busbooking.ui.booking.BookingHistoryFrame;
import busbooking.ui.booking.SelectSeatGUI;

import javax.swing.*;
import java.awt.*;

public class UserHomeFrame extends JFrame {
    public UserHomeFrame(String userId) {
        super("เมนูผู้โดยสาร - " + User.nameOf(userId));
        setSize(470,320);setLocationRelativeTo(null);setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JPanel p = new JPanel(new GridLayout(4,1,10,10));
        p.setBorder(BorderFactory.createEmptyBorder(20,35,20,35));
        // userId คือเบอร์มือถือ (ใช้เป็นรหัสตอนจอง) แต่หน้านี้ทักทายด้วยชื่อที่สมัครไว้
        JLabel title = new JLabel("ยินดีต้อนรับ คุณ" + User.loggedInUserName, SwingConstants.CENTER);
        JButton seats = new JButton("ค้นหาเที่ยวรถ / เลือกที่นั่ง");
        JButton history = new JButton("ประวัติการจอง / ชำระเงิน / ยกเลิก");
        JButton logout = new JButton("ออกจากระบบ");
        seats.addActionListener(e -> new SelectSeatGUI(userId).setVisible(true));
        history.addActionListener(e -> new BookingHistoryFrame(userId).setVisible(true));
        // ออกจากระบบ: ปิดทุกหน้าของผู้ใช้คนนี้ (หน้าจอง/ประวัติ) และล้างข้อมูลล็อกอิน
        // เดิมหน้าเหล่านั้นยังเปิดค้าง คนถัดไปจองหรือยกเลิกในชื่อคนเดิมได้
        logout.addActionListener(e -> {
            busbooking.Main.closeAllWindows();
            User.logout();
            new LoginUI().setVisible(true);
        });
        p.add(title);p.add(seats);p.add(history);p.add(logout);add(p);
        busbooking.ui.UiTheme.useFontForDefaults(p, new Font("Tahoma", Font.PLAIN, 16));
    }
}
