package tests;

import busbooking.data.BookingException;
import busbooking.data.BookingService;
import busbooking.model.Booking;
import busbooking.model.BookingStatus;
import busbooking.model.PaymentMethod;
import busbooking.model.PaymentStatus;
import busbooking.model.Ticket;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * ชุดทดสอบ BookingService แบบรันด้วย main() (ไม่ต้องพึ่ง JUnit)
 * ใช้โฟลเดอร์ชั่วคราวใหม่ทุกครั้ง จึงไม่แตะไฟล์ csv จริง
 * รัน: java tests.BookingServiceTest   -> ต้องขึ้น "ผ่านทั้งหมด"
 */
public class BookingServiceTest {

    private static int passed = 0;
    private static int failed = 0;

    private static void check(String name, boolean ok) {
        if (ok) {
            passed++;
            System.out.println("  [ผ่าน] " + name);
        } else {
            failed++;
            System.out.println("  [ไม่ผ่าน] " + name);
        }
    }

    /** ตรวจว่าการเรียกนี้ต้องโยน BookingException */
    private static void expectFail(String name, Runnable2 r) {
        try {
            r.run();
            check(name + " (ควรโยน exception)", false);
        } catch (BookingException e) {
            check(name + " -> \"" + e.getMessage() + "\"", true);
        }
    }

    private interface Runnable2 {
        void run() throws BookingException;
    }

    public static void main(String[] args) throws Exception {
        File dir = Files.createTempDirectory("bookingtest").toFile();
        try (FileWriter w = new FileWriter(new File(dir, "routes.csv"))) {
            w.write("routeId,origin,destination\nR001,Bangkok,Chiang Mai\n");
        }
        BookingService s = new BookingService(dir.getPath());
        LocalDate day = LocalDate.now().plusDays(2);
        LocalTime t9 = LocalTime.of(9, 0);

        System.out.println("1) สร้างการจอง");
        List<Booking> bk = s.createBooking("U1", "R001", day, t9, Arrays.asList("A1", "A2"), 350);
        check("ได้ 2 แถว", bk.size() == 2);
        check("รหัส BK0001/BK0002", bk.get(0).getBookingId().equals("BK0001")
                && bk.get(1).getBookingId().equals("BK0002"));
        check("สถานะเริ่มต้น PENDING", bk.get(0).getStatus() == BookingStatus.PENDING);
        check("ที่นั่งถูกกันไว้ 2 ที่", s.countBookedSeats("R001", day, t9) == 2);

        System.out.println("2) ป้องกันจองซ้ำ / ข้อมูลผิด");
        expectFail("จอง A1 ซ้ำ", () -> s.createBooking("U2", "R001", day, t9, Arrays.asList("A1"), 350));
        expectFail("ที่นั่งซ้ำในคำขอเดียว", () -> s.createBooking("U2", "R001", day, t9, Arrays.asList("B1", "B1"), 350));
        expectFail("ไม่เลือกที่นั่ง", () -> s.createBooking("U2", "R001", day, t9, Collections.<String>emptyList(), 350));
        expectFail("ไม่มี userId", () -> s.createBooking("", "R001", day, t9, Arrays.asList("B2"), 350));
        expectFail("เที่ยวที่ออกไปแล้ว", () -> s.createBooking("U2", "R001", LocalDate.now().minusDays(1), t9, Arrays.asList("B2"), 350));
        expectFail("มีคอมมาในรหัสที่นั่ง", () -> s.createBooking("U2", "R001", day, t9, Arrays.asList("B,2"), 350));
        check("จองไม่สำเร็จแล้วไม่มีแถวหลุดเข้าไฟล์", s.countBookedSeats("R001", day, t9) == 2);

        System.out.println("3) ชำระเงิน + ออกตั๋ว");
        List<String> ids = Arrays.asList(bk.get(0).getBookingId(), bk.get(1).getBookingId());
        List<Ticket> tk = s.payBookings(ids, PaymentMethod.PROMPTPAY);
        check("ได้ตั๋ว 2 ใบ", tk.size() == 2);
        check("สถานะเป็น PAID", s.findBooking("BK0001").getStatus() == BookingStatus.PAID);
        check("payment SUCCESS", s.getPaymentStatus("BK0001") == PaymentStatus.SUCCESS);
        String yy = day.format(java.time.format.DateTimeFormatter.ofPattern("yyMMdd"));
        check("เลขตั๋วรูปแบบ SBT-" + yy + "-0001", tk.get(0).getTicketNumber().equals("SBT-" + yy + "-0001"));
        check("issueTicket ซ้ำได้ตั๋วใบเดิม", s.issueTicket("BK0001").getTicketId().equals(tk.get(0).getTicketId()));
        expectFail("จ่ายซ้ำ", () -> s.processPayment("BK0001", PaymentMethod.PROMPTPAY));
        expectFail("ออกตั๋วก่อนจ่าย", () -> {
            List<Booking> x = s.createBooking("U1", "R001", day, t9, Arrays.asList("C1"), 350);
            s.issueTicket(x.get(0).getBookingId());
        });

        System.out.println("4) พิมพ์ตั๋ว");
        File f = s.printTicket(tk.get(0));
        check("สร้างไฟล์ " + f.getName(), f.exists() && f.length() > 0);
        String content = new String(Files.readAllBytes(f.toPath()), "UTF-8");
        check("ไฟล์ตั๋วมีชื่อเส้นทาง", content.contains("Bangkok"));

        System.out.println("5) ยกเลิก + คืนเงิน + สิทธิ์");
        expectFail("คนอื่นยกเลิกไม่ได้", () -> s.cancelBooking("BK0001", "U2"));
        s.cancelBooking("BK0001", "U1");
        check("สถานะ CANCELLED", s.findBooking("BK0001").getStatus() == BookingStatus.CANCELLED);
        check("payment REFUNDED", s.getPaymentStatus("BK0001") == PaymentStatus.REFUNDED);
        check("ที่นั่ง A1 ว่างกลับมา", s.isSeatAvailable("R001", day, t9, "A1"));
        expectFail("ยกเลิกซ้ำ", () -> s.cancelBooking("BK0001", "U1"));
        List<Booking> again = s.createBooking("U2", "R001", day, t9, Arrays.asList("A1"), 350);
        check("คนอื่นจอง A1 ได้หลังยกเลิก", again.size() == 1);

        System.out.println("6) ประวัติ / สรุปรายวัน / ชื่อเส้นทาง");
        List<Booking> hist = s.viewBookingHistory("U1");
        check("ประวัติ U1 ไม่ปนของคนอื่น", !hist.isEmpty() && allUser(hist, "U1"));
        check("เรียงใหม่สุดก่อน", hist.get(0).getBookingId().compareTo(hist.get(hist.size() - 1).getBookingId()) >= 0);
        Map<String, Integer> sum = s.getBookingSummary(day);
        check("สรุปรายวัน key R001|09:00", sum.containsKey("R001|09:00"));
        check("ชื่อเส้นทาง", s.getRouteName("R001").contains("Bangkok"));
        check("ไม่มีเส้นทาง -> คืนรหัสเดิม", s.getRouteName("R999").equals("R999"));

        System.out.println("7) แถวเสียในไฟล์ไม่ทำให้ล้ม");
        try (FileWriter w = new FileWriter(new File(dir, "bookings.csv"), true)) {
            w.write("BAD,ROW\n");
        }
        check("ยังอ่านประวัติได้", !s.viewBookingHistory("U1").isEmpty());

        System.out.println("8) ตรวจเลขบัตร");
        check("16 หลักผ่าน", BookingService.isValidCardNumber("4111111111111111"));
        check("15 หลักไม่ผ่าน", !BookingService.isValidCardNumber("411111111111111"));
        check("มีตัวอักษรไม่ผ่าน", !BookingService.isValidCardNumber("41111111111111ab"));

        System.out.println("9) หมดเวลาชำระ " + BookingService.PENDING_MINUTES + " นาที");
        String old = java.time.LocalDateTime.now().minusMinutes(BookingService.PENDING_MINUTES + 5)
                .format(Booking.STAMP);
        try (FileWriter w = new FileWriter(new File(dir, "bookings.csv"), true)) {
            w.write("BK0900,U3,R001," + day + ",09:00,D1,350.0,PENDING," + old + "\n");
        }
        check("รอชำระที่หมดเวลาไม่กันที่นั่ง", s.isSeatAvailable("R001", day, t9, "D1"));
        expectFail("จ่ายหลังหมดเวลา", () -> s.processPayment("BK0900", PaymentMethod.PROMPTPAY));
        check("isExpired = true", BookingService.isExpired(s.findBooking("BK0900")));

        System.out.println("\nผ่าน " + passed + " / ไม่ผ่าน " + failed);
        System.out.println(failed == 0 ? "ผ่านทั้งหมด" : "มีข้อที่ไม่ผ่าน");
        System.exit(failed == 0 ? 0 : 1);
    }

    private static boolean allUser(List<Booking> l, String u) {
        for (Booking b : l) {
            if (!b.getUserId().equals(u)) {
                return false;
            }
        }
        return true;
    }
}
