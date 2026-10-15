package login;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class User {
    private String phone;
    private String password;

    // ตัวแปรเก็บชื่อผู้ใช้ปัจจุบันที่ล็อกอินสำเร็จ
    public static String loggedInUserName = "";

    public User(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    public boolean login(String inputPhone, String inputPassword) {
        try (BufferedReader br = new BufferedReader(new FileReader("users.csv"))) {
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
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            // หากยังไม่มีไฟล์ users.csv หรือไฟล์ว่าง
        }
        return false;
    }
}