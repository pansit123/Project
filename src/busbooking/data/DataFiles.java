package busbooking.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ที่อยู่ไฟล์ข้อมูลทั้งหมดของโปรแกรม + ตัวช่วยอ่าน/เขียน CSV ที่หลายหน้าจอใช้ร่วมกัน
 * ถ้าจะย้ายโฟลเดอร์ข้อมูล แก้ที่ DIR จุดเดียว
 */
public final class DataFiles {

    private DataFiles() { }

    /** โฟลเดอร์ข้อมูล (เทียบจากโฟลเดอร์โปรเจกต์ที่ใช้รันโปรแกรม) */
    public static final Path DIR = Paths.get("data");

    public static final Path USERS = DIR.resolve("users.csv");
    public static final Path ROUTES = DIR.resolve("routes.csv");
    public static final Path BUS_TYPES = DIR.resolve("bustypes.csv");
    public static final Path TRIP_SCHEDULES = DIR.resolve("trip_schedules.csv");

    public static final String USERS_HEADER = "name,phone,password,birth_date,gender";
    public static final String ROUTES_HEADER = "route_id,origin,destination,stop";
    public static final String BUS_TYPES_HEADER = "bus_type_id,bus_type_name,total_seats";
    public static final String TRIP_SCHEDULES_HEADER =
            "trip_id,route_id,bus_type_id,travel_date,departure_time,price,available_seats";

    // คอลัมน์ของ trip_schedules.csv
    public static final int TRIP_ID = 0;
    public static final int TRIP_ROUTE_ID = 1;
    public static final int TRIP_BUS_TYPE_ID = 2;
    public static final int TRIP_DATE = 3;
    public static final int TRIP_TIME = 4;
    public static final int TRIP_PRICE = 5;
    public static final int TRIP_SEATS = 6;
    public static final int TRIP_COLUMNS = 7;

    /** ตัดอักขระ BOM ที่ Excel อาจเติมไว้หน้าไฟล์ */
    public static String stripBom(String s) {
        return s.replace(String.valueOf((char) 0xFEFF), "");
    }

    /**
     * มีเที่ยวรถใน trip_schedules.csv ที่ใช้รหัสนี้ในคอลัมน์ column อยู่ไหม
     * (ใช้กันไม่ให้ลบ/เปลี่ยนรหัสเส้นทางหรือประเภทรถที่ยังมีเที่ยวรถชี้อยู่)
     */
    public static boolean isUsedByTrip(int column, String id) throws IOException {
        if (!Files.exists(TRIP_SCHEDULES)) {
            return false;   // ยังไม่มีไฟล์เที่ยวรถ = ยังไม่มีเที่ยวรถใช้อะไรเลย (เดิมโยน error ทำให้ลบ/แก้รหัสไม่ได้)
        }
        try (BufferedReader reader = Files.newBufferedReader(TRIP_SCHEDULES, StandardCharsets.UTF_8)) {
            String header = reader.readLine();
            if (header == null || !stripBom(header).trim().equals(TRIP_SCHEDULES_HEADER)) {
                throw new IOException("หัวตาราง trip_schedules.csv ไม่ถูกต้อง");
            }
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] fields = line.split(",", -1);
                if (fields.length != TRIP_COLUMNS) {
                    throw new IOException("ข้อมูลเที่ยวรถมีคอลัมน์ไม่ครบ");
                }
                if (fields[column].trim().equalsIgnoreCase(id)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * เขียนไฟล์ CSV ใหม่ทั้งไฟล์แบบปลอดภัย: เขียนลงไฟล์ชั่วคราวก่อนแล้วค่อยย้ายทับ
     * ถ้าเขียนไม่สำเร็จกลางทาง ไฟล์เดิมจะไม่เสีย
     */
    public static void writeCsv(Path file, String header, List<String> lines) throws IOException {
        Path parent = file.toAbsolutePath().getParent();
        withLock(parent, () -> {
            Files.createDirectories(parent);
            Path temp = Files.createTempFile(parent, file.getFileName().toString(), ".tmp");
            try {
                try (BufferedWriter writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
                    writer.write(header);
                    writer.newLine();
                    for (String line : lines) {
                        writer.write(line);
                        writer.newLine();
                    }
                }
                Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
            } finally {
                Files.deleteIfExists(temp);
            }
            return null;
        });
    }

    // ================= ล็อกไฟล์ข้อมูล =================

    /** งานที่ต้องทำขณะถือล็อก (โยน exception แบบ checked ได้ เช่น BookingException, IOException) */
    public interface LockedAction<T, E extends Exception> {
        T run() throws E;
    }

    /** ล็อกภายในโปรแกรมเดียวกัน (กันหลาย thread) — ReentrantLock จึงเรียกซ้อนกันได้ */
    private static final ReentrantLock JVM_LOCK = new ReentrantLock();

    /**
     * ทำงานอ่าน-แก้-เขียนไฟล์ข้อมูลโดยถือล็อกไว้ตลอด
     * กันไม่ให้เปิดโปรแกรม 2 หน้าต่างแล้วจองที่นั่งเดียวกันได้ทั้งคู่
     * (ต่างโปรแกรมกันใช้ไฟล์ล็อก dir/.lock ส่วนในโปรแกรมเดียวกันใช้ JVM_LOCK)
     * ถ้าอีกโปรแกรมถือล็อกอยู่ จะรอจนกว่าอีกฝั่งทำเสร็จ
     */
    public static <T, E extends Exception> T withLock(Path dir, LockedAction<T, E> action) throws E {
        JVM_LOCK.lock();
        try {
            if (JVM_LOCK.getHoldCount() > 1) {
                return action.run();   // เรียกซ้อน: ถือไฟล์ล็อกอยู่แล้วจากชั้นนอก
            }
            FileChannel channel;
            FileLock fileLock;
            try {
                Files.createDirectories(dir);
                channel = FileChannel.open(dir.resolve(".lock"),
                        StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                fileLock = channel.lock();
            } catch (IOException e) {
                throw new DataException("ล็อกไฟล์ข้อมูลไม่สำเร็จ: " + e.getMessage(), e);
            }
            try {
                return action.run();
            } finally {
                try {
                    fileLock.release();
                    channel.close();
                } catch (IOException ignored) {
                    // ปิดไม่สำเร็จ: ระบบปฏิบัติการจะปล่อยล็อกให้เองเมื่อโปรแกรมปิด
                }
            }
        } finally {
            JVM_LOCK.unlock();
        }
    }
}
