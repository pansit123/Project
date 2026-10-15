package busbooking.ui.booking;

import busbooking.data.*;
import busbooking.model.Booking;
import busbooking.ui.UiTheme;

import javax.swing.*;
import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.List;

// หน้าสรุปก่อนยืนยันการจอง เปิดจาก SelectSeatGUI
// refresh = ให้หน้าเลือกที่นั่งวาดผังใหม่ (ที่นั่งที่เพิ่งจองจะกลายเป็นสีเทา)
public class CreateBookingGUI extends JFrame {

    public CreateBookingGUI(String userId, String routeId, String route, LocalDate date, LocalTime time,
                            List<String> seats, double price, BookingService service, Runnable refresh) {
        super("ยืนยันการจอง");
        setSize(470, 410);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        Container p = getContentPane();
        p.setLayout(null);
        p.setBackground(new Color(230, 242, 255));

        String allSeats = String.join(", ", seats);
        String[] details = {
                "รหัสเส้นทาง: " + routeId,
                "จำนวนที่นั่ง: " + seats.size() + " ที่นั่ง",
                "ต้นทาง - ปลายทาง: " + route,
                "ที่นั่ง: " + shorten(allSeats, 34),
                "วันที่: " + date + " เวลา: " + time,
                "ราคาสุทธิ: " + UiTheme.money(seats.size() * price),
                "สถานะ: รอชำระเงิน (ชำระภายใน " + BookingService.PENDING_MINUTES + " นาที)"
        };
        int[] y = {10, 50, 85, 120, 155, 195, 230};
        for (int i = 0; i < details.length; i++) {
            JLabel label = new JLabel(details[i]);
            label.setFont(new Font("Tahoma", Font.BOLD, 15));
            label.setBounds(30, y[i], 420, 30);
            if (i == 3) {
                label.setToolTipText(allSeats);   // จองหลายที่นั่งจนยาวเกินช่อง: ชี้เมาส์ดูครบได้
            }
            p.add(label);
        }

        JButton confirm = new JButton("ยืนยันและไปชำระเงิน");
        confirm.setBounds(30, 295, 235, 45);
        confirm.setFont(new Font("Tahoma", Font.BOLD, 14));
        confirm.setBackground(new Color(0, 51, 153));
        confirm.setForeground(Color.WHITE);
        p.add(confirm);

        JButton cancel = new JButton("กลับไปเลือกที่นั่ง");
        cancel.setBounds(280, 295, 155, 45);
        cancel.setFont(new Font("Tahoma", Font.PLAIN, 13));
        p.add(cancel);

        confirm.addActionListener(e -> {
            try {
                List<Booking> bookings = service.createBooking(userId, routeId, date, time, seats, price);
                List<String> ids = new ArrayList<>();
                for (Booking b : bookings) {
                    ids.add(b.getBookingId());
                }
                JOptionPane.showMessageDialog(this, "สร้างการจองสำเร็จ: " + String.join(", ", ids));
                dispose();
                refresh.run();
                new PaymentFrame(service, userId, ids).setVisible(true);
            } catch (BookingException ex) {
                // ส่วนใหญ่คือมีคนจองที่นั่งตัดหน้า -> ปิดหน้านี้ (ที่นั่งที่แสดงอยู่ใช้ไม่ได้แล้ว)
                // แล้ววาดผังใหม่ให้เลือกที่นั่งจากข้อมูลล่าสุด
                JOptionPane.showMessageDialog(this, ex.getMessage(), "จองไม่สำเร็จ", JOptionPane.ERROR_MESSAGE);
                dispose();
                refresh.run();
            }
        });
        cancel.addActionListener(e -> dispose());
    }

    /** ตัดข้อความที่ยาวเกิน max ตัวอักษร แล้วต่อท้ายด้วย "..." */
    private static String shorten(String text, int max) {
        return text.length() <= max ? text : text.substring(0, max).replaceAll(",\\s*\\S*$", "") + " ...";
    }
}
