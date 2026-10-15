package busbooking.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * ข้อมูลการจอง 1 รายการ = 1 ที่นั่ง (1 แถวใน bookings.csv)
 *
 * ถ้าผู้ใช้จองหลายที่นั่งพร้อมกัน ระบบจะสร้าง Booking หลายแถว (ที่นั่งละ 1 แถว)
 * เที่ยวรถระบุด้วย routeId + travelDate + departTime
 * (ไม่ต้องมีตารางเที่ยวรถรายวัน เพราะรถฟิก 8 เที่ยว/วัน ตามที่อาจารย์แนะนำ)
 *
 * คอลัมน์ใน bookings.csv:
 * bookingId,userId,routeId,travelDate,departTime,seatNo,price,status,bookingDate
 */
public class Booking {

    /** รูปแบบวันเวลาที่เก็บในไฟล์ เช่น 2026-10-15 09:46:12 (ไม่มีเครื่องหมายคอมมา) */
    public static final DateTimeFormatter STAMP =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String bookingId;
    private final String userId;
    private final String routeId;
    private final LocalDate travelDate;
    private final LocalTime departTime;
    private final String seatNo;
    private final double price;          // ราคาต่อ 1 ที่นั่ง
    private BookingStatus status;
    private final LocalDateTime bookingDate;

    public Booking(String bookingId, String userId, String routeId,
                   LocalDate travelDate, LocalTime departTime,
                   String seatNo, double price,
                   BookingStatus status, LocalDateTime bookingDate) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.routeId = routeId;
        this.travelDate = travelDate;
        this.departTime = departTime;
        this.seatNo = seatNo;
        this.price = price;
        this.status = status;
        this.bookingDate = bookingDate;
    }

    // ---------------- getter / setter ----------------

    public String getBookingId() { return bookingId; }
    public String getUserId() { return userId; }
    public String getRouteId() { return routeId; }
    public LocalDate getTravelDate() { return travelDate; }
    public LocalTime getDepartTime() { return departTime; }
    public String getSeatNo() { return seatNo; }
    public double getPrice() { return price; }
    public BookingStatus getStatus() { return status; }
    public LocalDateTime getBookingDate() { return bookingDate; }

    /** เปลี่ยนสถานะ (ใช้ตอนชำระเงิน / ยกเลิก) */
    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    /** วันเวลาที่รถออกจริง ใช้เช็คว่าเที่ยวนี้ออกไปแล้วหรือยัง */
    public LocalDateTime getDepartureDateTime() {
        return LocalDateTime.of(travelDate, departTime);
    }

    // ---------------- แปลงจาก/เป็นแถว CSV ----------------

    /** หัวคอลัมน์ของ bookings.csv (คืนอาร์เรย์ใหม่ทุกครั้ง กันโดนแก้ค่า) */
    public static String[] header() {
        return new String[]{"bookingId", "userId", "routeId", "travelDate",
                "departTime", "seatNo", "price", "status", "bookingDate"};
    }

    /** แปลงเป็นแถวสำหรับเขียนลงไฟล์ */
    public String[] toCsvRow() {
        return new String[]{
                bookingId, userId, routeId,
                travelDate.toString(),          // 2026-10-18
                departTime.toString(),          // 08:00
                seatNo,
                String.valueOf(price),
                status.name(),                  // PENDING / PAID / CANCELLED
                bookingDate.format(STAMP)
        };
    }

    /**
     * แปลงแถวจากไฟล์เป็น Booking
     * ถ้าจำนวนคอลัมน์ไม่ครบหรือค่าผิดรูปแบบ จะโยน RuntimeException
     * (BookingService จะข้ามแถวนั้นไป ไม่ให้โปรแกรมล้มทั้งหมด)
     */
    public static Booking fromCsvRow(String[] f) {
        if (f.length < 9) {
            throw new IllegalArgumentException("bookings.csv ต้องมี 9 คอลัมน์ แต่พบ " + f.length);
        }
        return new Booking(
                f[0].trim(), f[1].trim(), f[2].trim(),
                LocalDate.parse(f[3].trim()),
                LocalTime.parse(f[4].trim()),
                f[5].trim(),
                Double.parseDouble(f[6].trim()),
                BookingStatus.fromCode(f[7]),
                LocalDateTime.parse(f[8].trim(), STAMP));
    }

    @Override
    public String toString() {
        return bookingId + " | " + routeId + " | " + travelDate + " "
                + departTime + " | ที่นั่ง " + seatNo + " | " + status.getLabel();
    }
}
