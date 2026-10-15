package busbooking.model;

/**
 * เพศของสมาชิก (คอลัมน์ gender ของ users.csv)
 * ในไฟล์เก็บเป็นรหัสภาษาอังกฤษ ส่วนหน้าจอแสดงด้วย getLabel()
 */
public enum Gender {

    MALE("ชาย"),
    FEMALE("หญิง");

    private final String label;

    Gender(String label) {
        this.label = label;
    }

    /** ข้อความภาษาไทยสำหรับแสดงบนหน้าจอ */
    public String getLabel() {
        return label;
    }

    /** แปลงข้อความบนหน้าจอ ("ชาย"/"หญิง") เป็น Gender หรือ null ถ้ายังไม่ได้เลือก */
    public static Gender fromLabel(String label) {
        for (Gender g : values()) {
            if (g.label.equals(label)) {
                return g;
            }
        }
        return null;
    }

    /** แปลงรหัสจากไฟล์เป็น Gender หรือ null ถ้าว่าง/อ่านไม่ได้ (สมาชิกเก่าที่สมัครก่อนมีคอลัมน์นี้) */
    public static Gender fromCode(String code) {
        try {
            return code == null || code.trim().isEmpty() ? null : valueOf(code.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
