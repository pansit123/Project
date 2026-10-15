package busbooking.data;

/**
 * อ่านหรือเขียนไฟล์ข้อมูลไม่สำเร็จ เช่น ไฟล์ถูกเปิดค้างใน Excel, ไม่มีสิทธิ์เขียน, ดิสก์เต็ม
 *
 * เป็น RuntimeException เพื่อให้เด้งขึ้นไปจนถึงตัวจัดการ error กลางใน Main ได้เอง
 * (ไม่ต้องเพิ่ม throws ทุกเมธอด) และที่สำคัญคือ "หยุดการทำงานทันที"
 * ไม่ปล่อยให้โปรแกรมทำต่อด้วยข้อมูลว่าง แล้วบันทึกทับไฟล์เดิมจนข้อมูลหาย
 */
public class DataException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DataException(String message, Throwable cause) {
        super(message, cause);
    }
}
