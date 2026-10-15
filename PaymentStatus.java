package busbooking.model;

/**
 * สถานะของการชำระเงิน (คอลัมน์ status ของ payments.csv)
 */
public enum PaymentStatus {

    SUCCESS("สำเร็จ"),
    FAILED("ไม่สำเร็จ"),
    REFUNDED("คืนเงินแล้ว");   // เกิดตอนยกเลิกการจองที่จ่ายเงินไปแล้ว (จำลองการคืนเงิน)

    private final String label;

    PaymentStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static PaymentStatus fromCode(String code) {
        return valueOf(code.trim().toUpperCase());
    }
}
