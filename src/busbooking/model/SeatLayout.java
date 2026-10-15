package busbooking.model;

/**
 * กติกาการตั้งชื่อที่นั่งบนรถ: แถวละ 4 ที่ (D C | ทางเดิน | B A) เรียงเป็นคอลัมน์ 1, 2, 3, ...
 * ที่นั่งลำดับที่ 0, 1, 2, 3 = D1 C1 B1 A1 ต่อด้วย D2 C2 B2 A2 ไปเรื่อยๆ
 *
 * รถที่มี n ที่นั่ง จะมีที่นั่งลำดับ 0 ถึง n-1 (หน้าเลือกที่นั่งวาดตามนี้)
 */
public final class SeatLayout {

    private SeatLayout() { }

    /** ตัวอักษรของแต่ละแถวที่นั่ง จากบนลงล่างในผัง */
    public static final String[] LANES = {"D", "C", "B", "A"};

    /** ทางเดินอยู่หลังแถวลำดับนี้ (หลังแถว C) */
    public static final int AISLE_AFTER_LANE = 1;

    /** ชื่อที่นั่งลำดับที่ index เช่น 0 -> D1, 3 -> A1, 39 -> A10 */
    public static String seatName(int index) {
        return LANES[index % LANES.length] + (index / LANES.length + 1);
    }

    /** ลำดับของที่นั่งจากชื่อ (ไม่สนตัวพิมพ์เล็ก/ใหญ่) หรือ -1 ถ้าชื่อไม่ตรงรูปแบบ */
    public static int seatIndex(String name) {
        if (name == null || name.trim().length() < 2) {
            return -1;
        }
        String s = name.trim().toUpperCase();
        int lane = -1;
        for (int i = 0; i < LANES.length; i++) {
            if (s.startsWith(LANES[i])) {
                lane = i;
            }
        }
        try {
            int column = Integer.parseInt(s.substring(1));
            return lane < 0 || column < 1 ? -1 : (column - 1) * LANES.length + lane;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
