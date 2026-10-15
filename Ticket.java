package busbooking.model;

import java.time.LocalDateTime;

/**
 * ตั๋วโดยสาร (รวมใบเสร็จไว้ในตั๋วใบเดียว) 1 แถวใน tickets.csv
 * ออกได้เฉพาะหลังชำระเงินสำเร็จเท่านั้น และผูกกับ Booking ด้วย bookingId
 *
 * ticketNumber คือเลขที่แสดงบนตั๋ว รูปแบบ SBT-yyMMdd-nnnn
 * เช่น SBT-261018-0001 (yyMMdd = วันที่เดินทาง, nnnn = ลำดับตั๋ว)
 *
 * คอลัมน์ใน tickets.csv: ticketId,bookingId,ticketNumber,issuedAt
 */
public class Ticket {

    private final String ticketId;
    private final String bookingId;
    private final String ticketNumber;
    private final LocalDateTime issuedAt;

    public Ticket(String ticketId, String bookingId, String ticketNumber, LocalDateTime issuedAt) {
        this.ticketId = ticketId;
        this.bookingId = bookingId;
        this.ticketNumber = ticketNumber;
        this.issuedAt = issuedAt;
    }

    public String getTicketId() { return ticketId; }
    public String getBookingId() { return bookingId; }
    public String getTicketNumber() { return ticketNumber; }
    public LocalDateTime getIssuedAt() { return issuedAt; }

    public static String[] header() {
        return new String[]{"ticketId", "bookingId", "ticketNumber", "issuedAt"};
    }

    public String[] toCsvRow() {
        return new String[]{ticketId, bookingId, ticketNumber, issuedAt.format(Booking.STAMP)};
    }

    public static Ticket fromCsvRow(String[] f) {
        if (f.length < 4) {
            throw new IllegalArgumentException("tickets.csv ต้องมี 4 คอลัมน์ แต่พบ " + f.length);
        }
        return new Ticket(f[0].trim(), f[1].trim(), f[2].trim(),
                LocalDateTime.parse(f[3].trim(), Booking.STAMP));
    }
}
