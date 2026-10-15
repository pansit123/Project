package busbooking.model;

import java.time.LocalDateTime;

/**
 * ข้อมูลการชำระเงิน 1 รายการ (1 แถวใน payments.csv) ผูกกับ Booking ด้วย bookingId
 *
 * หมายเหตุ: ระบบนี้เป็นการ "จำลอง" การชำระเงิน
 * จึงบันทึกแค่ว่าจ่ายด้วยวิธีไหน จำนวนเท่าไร เมื่อไร
 * ไม่เก็บเลขบัตรหรือข้อมูลละเอียดอ่อนใดๆ ลงไฟล์
 *
 * คอลัมน์ใน payments.csv: paymentId,bookingId,method,amount,status,paidAt
 */
public class Payment {

    private final String paymentId;
    private final String bookingId;
    private final PaymentMethod method;
    private final double amount;
    private PaymentStatus status;
    private final LocalDateTime paidAt;

    public Payment(String paymentId, String bookingId, PaymentMethod method,
                   double amount, PaymentStatus status, LocalDateTime paidAt) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.method = method;
        this.amount = amount;
        this.status = status;
        this.paidAt = paidAt;
    }

    public String getPaymentId() { return paymentId; }
    public String getBookingId() { return bookingId; }
    public PaymentMethod getMethod() { return method; }
    public double getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }
    public LocalDateTime getPaidAt() { return paidAt; }

    /** เปลี่ยนสถานะ (ใช้ตอนคืนเงินเมื่อยกเลิกการจอง) */
    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public static String[] header() {
        return new String[]{"paymentId", "bookingId", "method", "amount", "status", "paidAt"};
    }

    public String[] toCsvRow() {
        return new String[]{
                paymentId, bookingId, method.name(),
                String.valueOf(amount), status.name(),
                paidAt.format(Booking.STAMP)
        };
    }

    public static Payment fromCsvRow(String[] f) {
        if (f.length < 6) {
            throw new IllegalArgumentException("payments.csv ต้องมี 6 คอลัมน์ แต่พบ " + f.length);
        }
        return new Payment(
                f[0].trim(), f[1].trim(),
                PaymentMethod.fromCode(f[2]),
                Double.parseDouble(f[3].trim()),
                PaymentStatus.fromCode(f[4]),
                LocalDateTime.parse(f[5].trim(), Booking.STAMP));
    }
}
