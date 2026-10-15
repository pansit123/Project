package busbooking;

import busbooking.data.BookingService;
import busbooking.model.Booking;
import busbooking.ui.PaymentFrame;
import busbooking.ui.BookingHistoryFrame;
import busbooking.ui.UiTheme;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 * ตัวเปิดทดสอบงานคนที่ 4 แบบเดี่ยวๆ (ใช้ก่อนที่หน้าค้นหาของคนที่ 3 จะเสร็จ)
 *
 * วิธีรัน:  java busbooking.BookingDemo [book|history] [userId]
 *   book    = จำลองว่าคนที่ 3 สร้างการจองที่นั่ง A1,A2 เที่ยว 09:00 พรุ่งนี้แล้ว (สถานะรอชำระ)
 *             จากนั้นเปิด PaymentFrame (งานของเรา) ด้วยรหัสการจองเหล่านั้น
 *   history = เปิดหน้าประวัติการจอง
 */
public class BookingDemo {

    public static void main(String[] args) {
        final String mode = args.length > 0 ? args[0] : "book";
        final String userId = args.length > 1 ? args[1] : "U0001";
        SwingUtilities.invokeLater(() -> {
            UiTheme.installDefaults();
            if ("history".equals(mode)) {
                new BookingHistoryFrame(userId).setVisible(true);
            } else {
                try {
                    BookingService svc = new BookingService();
                    List<String> ids = new ArrayList<>();
                    for (Booking b : svc.createBooking(userId, "R001", LocalDate.now().plusDays(1),
                            LocalTime.of(9, 0), Arrays.asList("A1", "A2"), 350.0)) {
                        ids.add(b.getBookingId());
                    }
                    new PaymentFrame(svc, userId, ids).setVisible(true);
                } catch (Exception ex) {
                    System.out.println("สร้างการจองทดสอบไม่ได้: " + ex.getMessage());
                }
            }
        });
    }
}
