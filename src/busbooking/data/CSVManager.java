package busbooking.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * อ่าน/เขียนไฟล์ CSV แบบง่าย (แยกคอลัมน์ด้วยคอมมา ไม่รองรับคอมมาในข้อมูล)
 *
 * อ่านหรือเขียนไม่สำเร็จจะโยน DataException เสมอ ไม่เงียบไว้
 * (เดิมถ้าอ่านไม่ได้จะคืนรายการว่าง แล้วตอนบันทึกจะเขียนทับไฟล์เดิมจนข้อมูลหายหมด)
 */
public class CSVManager {

    public static List<String[]> loadFromCSV(String fileName) {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName), StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                data.add(line.split(","));
            }
        } catch (IOException e) {
            throw new DataException("อ่านไฟล์ " + fileName + " ไม่สำเร็จ: " + e.getMessage(), e);
        }
        return data;
    }

    /** เขียนทั้งไฟล์ใหม่ แถวแรกคือหัวตาราง (เขียนลงไฟล์ชั่วคราวก่อน ไฟล์เดิมจึงไม่เสียถ้าเขียนไม่สำเร็จ) */
    public static void saveToCSV(String fileName, List<String[]> data) {
        List<String> lines = new ArrayList<>();
        for (int i = 1; i < data.size(); i++) {
            lines.add(String.join(",", data.get(i)));
        }
        try {
            DataFiles.writeCsv(Paths.get(fileName), String.join(",", data.get(0)), lines);
        } catch (IOException e) {
            throw new DataException("บันทึกไฟล์ " + fileName + " ไม่สำเร็จ: " + e.getMessage(), e);
        }
    }
}
