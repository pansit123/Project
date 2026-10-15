package busbooking.data;

/**
 * ข้อผิดพลาดที่เกิดจากกติกาของระบบจอง เช่น ที่นั่งถูกจองแล้ว, ยกเลิกไม่ได้
 * ข้อความ (getMessage) เป็นภาษาไทย พร้อมแสดงให้ผู้ใช้เห็นบนหน้าจอได้ทันที
 */
public class BookingException extends Exception {

    private static final long serialVersionUID = 1L;

    public BookingException(String message) {
        super(message);
    }
}
