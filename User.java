package busbooking.model;

import busbooking.data.DataException;
import busbooking.data.DataFiles;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String phone;
    private String password;

    // ตัวแปรเก็บชื่อผู้ใช้ปัจจุบันที่ล็อกอินสำเร็จ
    public static String loggedInUserName = "";
    public static String loggedInPhone = "";

    public User(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    // อ่านเป็น UTF-8 ให้ตรงกับตอนสมัครสมาชิก ชื่อภาษาไทยจะได้ไม่เพี้ยน
    private static BufferedReader openUsersFile() throws IOException {
        return Files.newBufferedReader(DataFiles.USERS, StandardCharsets.UTF_8);
    }

    /** จำนวนคอลัมน์ของ users.csv: name,phone,password,birth_date,gender */
    private static final int USER_COLUMNS = 5;

    /** สมัครสมาชิกโดยไม่ระบุวันเกิด/เพศ (ช่องนั้นจะว่าง) */
    public static boolean register(String name, String phone, String password) throws IOException {
        return register(name, phone, password, null, null);
    }

    /**
     * สมัครสมาชิก: เพิ่มแถวใหม่ลง users.csv (ถ้ายังไม่มีไฟล์ จะสร้างพร้อมแถวหัวตาราง)
     * ผู้เรียกต้องตรวจข้อมูลมาก่อนแล้ว (ไม่มีคอมมา)
     *
     * วันเกิดเก็บเป็นปี ค.ศ. รูปแบบ yyyy-MM-dd (เหมือนวันที่อื่นในโปรแกรม) เพศเก็บเป็น MALE / FEMALE
     * ถ้าไฟล์ยังเป็นรูปแบบเก่า (3 คอลัมน์) จะปรับหัวตารางและเติมช่องว่างให้แถวเดิมก่อน สมาชิกเดิมจึงล็อกอินได้ตามปกติ
     * เขียนทั้งไฟล์ใหม่แบบปลอดภัย (ไฟล์ชั่วคราว -> ย้ายทับ) จึงไม่มีปัญหาบรรทัดสุดท้ายไม่มีขึ้นบรรทัดใหม่
     *
     * @return false ถ้าเบอร์นี้มีคนสมัครไปแล้ว (ตรวจซ้ำขณะถือล็อก กันสองโปรแกรมสมัครเบอร์เดียวกันพร้อมกัน)
     */
    public static boolean register(String name, String phone, String password,
                                   LocalDate birthDate, Gender gender) throws IOException {
        return DataFiles.withLock(DataFiles.DIR, () -> {
            if (phoneExists(phone)) {
                return false;
            }
            List<String> rows = new ArrayList<>();
            if (Files.exists(DataFiles.USERS)) {
                List<String> lines = Files.readAllLines(DataFiles.USERS, StandardCharsets.UTF_8);
                for (int i = 1; i < lines.size(); i++) {   // แถวแรกคือหัวตาราง (เขียนใหม่เป็นแบบล่าสุด)
                    if (!lines.get(i).trim().isEmpty()) {
                        rows.add(padColumns(lines.get(i)));
                    }
                }
            }
            rows.add(String.join(",", name, phone, password,
                    birthDate == null ? "" : birthDate.toString(),
                    gender == null ? "" : gender.name()));
            DataFiles.writeCsv(DataFiles.USERS, DataFiles.USERS_HEADER, rows);
            return true;
        });
    }

    /** เติมคอลัมน์ว่างให้แถวของสมาชิกเก่า (ที่สมัครก่อนมีวันเกิด/เพศ) ให้ครบ 5 คอลัมน์ */
    private static String padColumns(String line) {
        StringBuilder sb = new StringBuilder(line);
        for (int n = line.split(",", -1).length; n < USER_COLUMNS; n++) {
            sb.append(',');
        }
        return sb.toString();
    }

    /**
     * ชื่อสมาชิกทั้งหมด: เบอร์มือถือ -> ชื่อ (ใช้แสดงชื่อผู้โดยสารแทนเบอร์ในตั๋ว/ประวัติ/สรุปการจอง)
     * ยังไม่มีไฟล์ = ยังไม่มีสมาชิก (คืน map ว่าง)
     */
    public static java.util.Map<String, String> loadNames() {
        java.util.Map<String, String> names = new java.util.HashMap<>();
        try (BufferedReader br = openUsersFile()) {
            br.readLine(); // ข้าม Header
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 2 && !data[1].trim().isEmpty()) {
                    names.put(data[1].trim(), data[0].trim());
                }
            }
        } catch (NoSuchFileException e) {
            // ยังไม่มีสมาชิก
        } catch (IOException e) {
            throw new DataException("อ่านไฟล์ users.csv ไม่สำเร็จ: " + e.getMessage(), e);
        }
        return names;
    }

    /** ชื่อของสมาชิกเบอร์นี้ ถ้าไม่พบ (หรืออ่านไฟล์ไม่ได้) คืนเบอร์กลับไปแทน ใช้แค่แสดงผล */
    public static String nameOf(String phone) {
        try {
            String name = loadNames().get(phone);
            return name == null || name.isEmpty() ? phone : name;
        } catch (DataException e) {
            return phone;
        }
    }

    /** ข้อความแสดงผู้โดยสาร เช่น "สมชาย ใจดี (0812345678)" */
    public static String displayName(String phone, java.util.Map<String, String> names) {
        String name = names.get(phone);
        return name == null || name.isEmpty() ? phone : name + " (" + phone + ")";
    }

    /** ออกจากระบบ: ล้างข้อมูลผู้ใช้ที่ล็อกอินอยู่ */
    public static void logout() {
        loggedInUserName = "";
        loggedInPhone = "";
    }

    // ใช้ตอนสมัครสมาชิก กันไม่ให้สมัครเบอร์ซ้ำ
    public static boolean phoneExists(String phone) {
        try (BufferedReader br = openUsersFile()) {
            br.readLine(); // ข้าม Header
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 2 && data[1].trim().equals(phone)) {
                    return true;
                }
            }
        } catch (NoSuchFileException e) {
            // ยังไม่มีไฟล์ users.csv แปลว่ายังไม่มีใครสมัคร
        } catch (IOException e) {
            // อ่านไม่ได้ด้วยเหตุอื่น: ห้ามตอบว่า "ไม่ซ้ำ" มั่วๆ เพราะจะได้บัญชีซ้ำ
            throw new DataException("อ่านไฟล์ users.csv ไม่สำเร็จ: " + e.getMessage(), e);
        }
        return false;
    }

    public boolean login(String inputPhone, String inputPassword) {
        try (BufferedReader br = openUsersFile()) {
            String line;
            boolean isFirstLine = true; // ข้าม Header แถวแรกของ CSV
            
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                // รูปแบบใน users.csv: name,phone,password
                String[] data = line.split(",");
                
                if (data.length >= 3) {
                    String savedName = data[0].trim();
                    String savedPhone = data[1].trim();
                    String savedPassword = data[2].trim();

                    if (savedPhone.equals(inputPhone) && savedPassword.equals(inputPassword)) {
                        loggedInUserName = savedName;
                        loggedInPhone = savedPhone;
                        return true;
                    }
                }
            }
        } catch (NoSuchFileException e) {
            // ยังไม่มีไฟล์ users.csv = ยังไม่มีใครสมัคร
        } catch (IOException e) {
            throw new DataException("อ่านไฟล์ users.csv ไม่สำเร็จ: " + e.getMessage(), e);
        }
        return false;
    }
}