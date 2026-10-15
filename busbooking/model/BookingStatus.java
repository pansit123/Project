package busbooking.model;

/**
 * สถานะของการจอง (เก็บในคอลัมน์ status ของ bookings.csv)
 *
 * ในไฟล์ CSV เก็บเป็น "รหัสภาษาอังกฤษ" (PENDING / PAID / CANCELLED)
 * ไม่เก็บเป็นภาษาไทย เพื่อไม่ให้ตัวอักษรเพี้ยนเมื่อเปิดไฟล์ต่าง encoding
 * ส่วนหน้าจอให้เรียก getLabel() เพื่อแสดงเป็นภาษาไทย
 */
public enum BookingStatus {

    PENDING("รอชำระเงิน"),   // สร้างการจองแล้ว แต่ยังไม่จ่ายเงิน (ที่นั่งถูกกันไว้)
    PAID("สำเร็จ"),          // ชำระเงินแล้ว และออกตั๋วได้
    CANCELLED("ยกเลิก");     // ยกเลิกแล้ว ที่นั่งกลับมาว่าง

    private final String label;

    BookingStatus(String label) {
        this.label = label;
    }

    /** ข้อความภาษาไทยสำหรับแสดงบนหน้าจอ */
    public String getLabel() {
        return label;
    }

    /** แปลงรหัสจากไฟล์ CSV เป็น enum (ถ้าค่าผิดจะโยน IllegalArgumentException) */
    public static BookingStatus fromCode(String code) {
        return valueOf(code.trim().toUpperCase());
    }
}
